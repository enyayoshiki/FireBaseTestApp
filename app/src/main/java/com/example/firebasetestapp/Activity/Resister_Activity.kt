package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_resister_.*
import kotlinx.android.synthetic.main.activity_resister_login.editMail_View
import kotlinx.android.synthetic.main.activity_resister_login.editPass_View
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
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                showToast(if (it.isSuccessful) R.string.success else R.string.error)

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

    var selectImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectImageUri = data.data
            Log.d("resister", "selectImageUri : $selectImageUri")
            Log.d("resister", "selectImageUri.toString : ${selectImageUri.toString()}")
            Picasso.get().load(selectImageUri.toString()).into(myImageview)
            myImageviewBtn.alpha = 0f
        }
    }

    var userName = ""

    private fun resisterImage() {
        userName = editName_resister.text.toString()
        if (selectImageUri == null || userName.isEmpty()) return

        val fileName = UUID.randomUUID().toString()
        Log.d("resister", "$fileName")
        val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
        ref.putFile(selectImageUri!!).addOnSuccessListener {
            Log.d("selectImage", "UploadTask : $it")
            Log.d("selectImage", "filename : $fileName")
            Log.d("selectImage", "selectImageUri : $selectImageUri")
            Log.d("selectImage", "selectImage_to_String : ${selectImageUri.toString()}")

            ref.downloadUrl.addOnSuccessListener {
                saveUserDatatoFireStore(it.toString())
            }
        }
    }

    private fun saveUserDatatoFireStore(saveImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val db = FirebaseFirestore.getInstance()
        val user = User(uid, userName, saveImageUrl)
        db.collection("User").document("$uid").set(user)
            .addOnSuccessListener {
                Log.d("resister", "saveUserDatatoFireStore")
                LatestMessage_Activity.start(this)
            }
            .addOnFailureListener {
                Log.d("resister", "$it")
            }
    }
    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, Resister_Activity::class.java))
        }
    }
}