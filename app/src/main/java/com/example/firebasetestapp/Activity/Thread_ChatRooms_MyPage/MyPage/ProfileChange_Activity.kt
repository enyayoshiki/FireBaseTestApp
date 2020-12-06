package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.extention.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_setting.*
import java.util.*

class ProfileChange_Activity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var fromId : String? = ""
    private var profileName = ""
    private var profileImage = ""
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setting)
        supportActionBar?.hide()
        Log.d("profile_change", "onCreate")

        initialinze()
    }
    private fun initialinze(){
        showProgress()

        fromId = FirebaseAuth.getInstance().uid
        initlayout()

    }
    private fun initlayout() {
        val cameraBtn = findViewById<ImageView>(R.id.profile_image_setting_imageCamera_button)
        cameraBtn.setImageResource(R.drawable.ic_camera_image)
        val folderBtn = findViewById<ImageView>(R.id.profile_image_setting_imageFolder_button)
        folderBtn.setImageResource(R.drawable.ic_folder_image)

        db.collection("Users").document("${FirebaseAuth.getInstance().uid}").get()
            .addOnSuccessListener {
                profileName = it["userName"].toString()
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
            excuteProfileChange()
        }
        hideProgress()
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
            imageUri = data.data
            Picasso.get().load(imageUri).into(profile_Image_setting_ImageView)

        } else if (requestCode == REQUEST_CODE_START_CAMERA) {
            (data.extras?.get("data") as? Bitmap)?.also {
                profile_Image_setting_ImageView.setImageBitmap(it)
            }
            imageUri = data.data
        }
    }

    private fun excuteProfileChange() {
        imageChangeCheck()
    }

    private fun imageChangeCheck() {
        showProgress()
        val fileName = UUID.randomUUID().toString()
        Log.d("imageChange", "スタート$fileName")
        if (imageUri !== null) {
            val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
            Log.d("imageChange", "imagechange")
            ref.putFile(imageUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    profileImage = it.toString()
                    Log.d("imageChange", "成功 : $profileImage")
                    profileChange()
                }.addOnCanceledListener {
                    Log.d("imageChange", "失敗")
                    showToast(this, R.string.error)
                }
            }
        } else profileChange()
    }

    private fun profileChange(){

        val changeName = edit_profile_change_username_textView.text.toString()
        if (changeName.isNotEmpty()) {
            profileName = changeName
        }
        db.collection("Users").document(fromId!!)
            .set(com.example.firebasetestapp.dataClass.User().apply {
                uid = fromId ?: ""
                userName = profileName
                userImage = profileImage
            }).addOnSuccessListener {

                showToast(this, R.string.profile_setting_text)
                HomeFragment_Activity.start(this)
            }
            .addOnFailureListener {

                showToast(this, R.string.error)
            }
    }


    private var progressDialog: MaterialDialog? = null

    private fun showProgress() {
        hideProgress()
        progressDialog = this.let {
            MaterialDialog(it).apply {
                cancelable(false)
                setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false))
                show()
            }
        }
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }



    companion object{
        private const val REQUEST_CODE_CHOOSE_IMAGE = 1000
        private const val REQUEST_CODE_START_CAMERA = 1001

        fun start(activity: Activity) {
            val intent = Intent(activity, ProfileChange_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}
