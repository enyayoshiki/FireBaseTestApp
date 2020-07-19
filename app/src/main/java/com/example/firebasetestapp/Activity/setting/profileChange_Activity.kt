package com.example.firebasetestapp.Activity.setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetestapp.Activity.LatestMessage_Activity
import com.example.firebasetestapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.profile_setting.*
import java.util.*

class profileChange_Activity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var uid : String? = ""
    private var profileName = ""
    private var profileImage = ""
    private var profileImageUri: Uri? = null
    private var imageChange: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setting)
        Log.d("profile_change", "onCreate")

        initialinze()
    }
    private fun initialinze(){
        Log.d("profile_change", "initialize()")

        uid = FirebaseAuth.getInstance().uid
        initlayout()
    }
    private fun initlayout() {
        Log.d("profile_change", "initlayout()")
        val cameraBtn = findViewById<ImageView>(R.id.profile_image_setting_imageCamera_button)
        cameraBtn.setImageResource(R.drawable.ic_camera_image)
        val folderBtn = findViewById<ImageView>(R.id.profile_image_setting_imageFolder_button)
        folderBtn.setImageResource(R.drawable.ic_folder_image)

        db.collection("User").document("${FirebaseAuth.getInstance().uid}").get()
            .addOnSuccessListener {
                Log.d("profile_change", "Succeses")
                profileName = it["username"].toString()
                profileImage = it["userImage"].toString()

                Picasso.get().load(profileImage).into(profile_Image_setting_ImageView)
                profile_user_name_now_settintg_textView.text = profileName
            }

        profile_image_setting_imageFolder_button.setOnClickListener{
            uploadImageFromFolder()
        }
        profile_image_setting_imageCamera_button.setOnClickListener{
            uploadImageFromCamera()
        }
        save_profile_change_Btn.setOnClickListener {
            saveProfileChange()
        }
    }

    private fun uploadImageFromFolder(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE)
    }

    private fun uploadImageFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_START_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
            profileImageUri = data.data
            Picasso.get().load(profileImageUri).into(profile_Image_setting_ImageView)
            imageChange = true
        } else if (requestCode == REQUEST_CODE_START_CAMERA) {
//            Picasso.get().load(data.data).into(profile_Image_setting_ImageView)
            (data.extras?.get("data") as? Bitmap)?.also {
                profile_Image_setting_ImageView.setImageBitmap(it)
            }
                profileImageUri = data.data
            imageChange = true

        }
    }

    private fun saveProfileChange(){
        if (!edit_profile_change_username_textView.text.isEmpty()) {
            profileName = edit_profile_change_username_textView.text.toString()
        }
        if (imageChange) {
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
            ref.putFile(profileImageUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                profileImage = it.toString()
                val user = com.example.firebasetestapp.dataClass.User(uid!!, profileName, profileImage)
                db.collection("User").document("$uid")
                    .set(user).addOnSuccessListener {
                        Toast.makeText(applicationContext, "プロフィールを変更しました", Toast.LENGTH_LONG)
                        LatestMessage_Activity.start(this)
                    }
                    .addOnFailureListener{
                        Log.d("profile_change", "saveProfile_Fail_B : $it")
                    } }

            }.addOnFailureListener{
                Log.d("profile_change", "saveProfile_Fail_A : $it")
            }
        }
        val user = com.example.firebasetestapp.dataClass.User(uid!!, profileName, profileImage)
        db.collection("User").document("$uid")
            .set(user).addOnSuccessListener {
                Toast.makeText(applicationContext, "プロフィールを変更しました", Toast.LENGTH_LONG)
                LatestMessage_Activity.start(this)
            }
            .addOnFailureListener{
                Log.d("profile_change", "saveProfile_Fail_B : $it")
            }


    }

    companion object{
        private const val REQUEST_CODE_CHOOSE_IMAGE = 1000
        private const val REQUEST_CODE_START_CAMERA = 1001

        fun start(activity: Activity) {
            val intent = Intent(activity, profileChange_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}
