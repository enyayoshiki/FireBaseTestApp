package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.app.Activity
import android.content.Context
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
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.changefrontimage.*
import kotlinx.android.synthetic.main.profile_setting.*
import java.util.*

class ChangeChatRoomsFrontImage: AppCompatActivity()  {

    private val db = FirebaseFirestore.getInstance()
    private var profileImage : String = ""
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.changefrontimage)
        supportActionBar?.hide()

        initialize()
    }
    private fun initialize(){
        initData()
        initlayout()
    }

    private fun initData(){

    }
    private fun initlayout() {
        changeFrontImage_imageCamera_btn.setImageResource(R.drawable.ic_camera_image)
        changeFrontImage_imageFolder_btn.setImageResource(R.drawable.ic_folder_image)

        profileImage = intent.getStringExtra(ROOMIMAGE) ?: ""
        Log.d("frontimage", "frontImage: $profileImage")
        if (profileImage == R.drawable.sample_frontimage.toString()) {
//            profile_Image_setting_ImageView.setImageResource(profileImage.toInt()) as ImageView
            Picasso.get().load(profileImage.toInt()).into(changeFrontImage_ImageView)
        }
        else{
            Picasso.get().load(profileImage).into(changeFrontImage_ImageView)
        }

        initClick()

    }

    private fun initClick(){
        changeFrontImage_imageFolder_btn.setOnClickListener{
            uploadImageFromFolder()
        }
        changeFrontImage_imageCamera_btn.setOnClickListener{
            uploadImageFromCamera()
        }
        changeFrontImage_excuteChange_btn.setOnClickListener {
            saveFirebaseStorage()
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
            imageUri = data.data
            Picasso.get().load(imageUri).into(changeFrontImage_ImageView)

        } else if (requestCode == REQUEST_CODE_START_CAMERA) {
            (data.extras?.get("data") as? Bitmap)?.also {
                changeFrontImage_ImageView.setImageBitmap(it)
            }
            imageUri = data.data
        }
    }

    private fun saveFirebaseStorage() {
        val fileName = UUID.randomUUID().toString()
        if (imageUri !== null) {
            val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
            ref.putFile(imageUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    profileImage = it.toString()
                    frontImageChange()
                }.addOnCanceledListener {
                    showToast(R.string.error)
                }
            }
        } else frontImageChange()
    }


    private fun frontImageChange(){
        showProgress()
        val roomId = intent.getStringExtra(ROOMID)
        db.collection("ChatRooms").document(roomId!!)
            .update(mapOf(
            "frontImage" to profileImage
            )
        )
        showToast(R.string.success)
        hideProgress()
        finish()
//            .set(ChatRooms().apply {
//                frontImage = profileImage
//            }).addOnSuccessListener {
//
//            }
//            .addOnFailureListener {
//                showToast(R.string.error)
//            }
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

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

    companion object{
        private const val REQUEST_CODE_CHOOSE_IMAGE = 1000
        private const val REQUEST_CODE_START_CAMERA = 1001
        const val ROOMIMAGE = "ROOMIMAGE"
        const val ROOMID = "ROOMID"
        fun start(activity: Context,roomId: String, roomImage: String) {
            val intent = Intent(activity, ChangeChatRoomsFrontImage::class.java).apply {
                putExtra(ROOMID, roomId)
                putExtra(ROOMIMAGE, roomImage)
            }
            activity.startActivity(intent)
        }
    }
}