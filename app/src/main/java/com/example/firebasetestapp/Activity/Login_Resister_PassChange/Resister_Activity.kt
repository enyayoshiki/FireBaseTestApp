package com.example.firebasetestapp.Activity.Login_Resister_PassChange

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.extention.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_resister_.*
import java.util.*

class Resister_Activity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister_)
        supportActionBar?.hide()

        myImageviewBtn.setOnClickListener {
            selectImage()
        }

        excute_resister_Btn.setOnClickListener {
            val email = editMail_resiter_View.text.toString()
            val pass = editPass_resister.text.toString()

            register(email, pass)

        }
    }

    private fun register(email: String, pass: String) {

        showProgress()

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {

                resisterImage()

            }.addOnFailureListener {
                Log.d("error", "$it")
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    var selectImageUri: Uri? =  null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectImageUri = data.data
            Picasso.get().load(selectImageUri.toString()).into(myImageview)
            myImageviewBtn.alpha = 0f
        }
    }

    var resistUserName = ""

    private fun resisterImage() {

        resistUserName = editName_resister.text.toString()
        if (selectImageUri == null || resistUserName.isEmpty()){
            saveUserDatatoFireStore("")
            return
        }

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
        ref.putFile(selectImageUri!!).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener {
                saveUserDatatoFireStore(it.toString())
            }
        }
    }

    private fun saveUserDatatoFireStore(saveImageUrl: String) {

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val newFcmToken = if (task.isSuccessful) task.result?.token ?: "" else ""
            val userId = FirebaseAuth.getInstance().uid ?: ""
            val db = FirebaseFirestore.getInstance()
            db.collection("Users").document(userId)
                .set(User().apply {
                    uid = userId
                    userName = resistUserName
                    userImage = saveImageUrl
                    fcmToken = newFcmToken
                })
                .addOnSuccessListener {
                    showToast(this, R.string.success)
                    HomeFragment_Activity.start(this)

                    hideProgress()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                    hideProgress()
                }
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



    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, Resister_Activity::class.java))
        }
    }
}