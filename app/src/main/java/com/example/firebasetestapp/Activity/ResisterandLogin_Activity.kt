package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.core.content.ContextCompat
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.LoginType
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_resister_login.*
import com.squareup.picasso.Picasso
import java.util.*

class ResisterandLogin_Activity : AppCompatActivity() {

    var nowLoginType = LoginType.Login
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister_login)
        initialize()
    }

    private fun initialize() {
        if (auth.currentUser !== null) {
            LatestMessage_Activity.start(this)
        }
        initLayout()
    }

    private fun initLayout() {
        imageVisible(false)
        changeLayout(LoginType.Login)

        loginTextView.setOnClickListener {
            imageVisible(false)
            changeLayout(LoginType.Login)
        }
        registerTextView.setOnClickListener {
            imageVisible(true)
            changeLayout(LoginType.Resister)
        }
        passChangeTextView.setOnClickListener {
            imageVisible(false)
            changeLayout(LoginType.PassChange)
        }

        myImageviewBtn.setOnClickListener {
            selectImage()
        }

        excuteBtn.setOnClickListener {
            val email = editMail_View.text.toString()
            val pass = editPass_View.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "email//passwordを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when (excuteBtn.text) {
                "ログイン" -> login(email, pass)
                "登録" -> register(email, pass)
                "登録変更" -> changeRegister(email, pass)
            }
        }
        closeImageView.setOnClickListener {
            finish()
        }
    }

    private fun changeLayout(loginType: LoginType) {
        nowLoginType = loginType
        excuteBtn.text = getString(excuteBtnString(loginType))
        textViewColorChange(loginTextView, loginType == LoginType.Login)
        textViewColorChange(registerTextView, loginType == LoginType.Resister)
        textViewColorChange(passChangeTextView, loginType == LoginType.PassChange)
    }

    private fun textViewColorChange(textView: TextView, isSelected: Boolean) {
        textView.apply {
            setTextColor(
                ContextCompat.getColor(
                    this@ResisterandLogin_Activity,
                    if (isSelected) android.R.color.white else android.R.color.black
                )
            )
            setBackgroundResource(if (isSelected) R.drawable.selector_button_black_to_black_80 else R.drawable.selector_button_white_to_white_80)
        }
    }

    private fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()

                    LatestMessage_Activity.start(this)

                } else Toast.makeText(
                    this,
                    R.string.error, Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Log.d("error", "$it")
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
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


    private fun changeRegister(email: String, pass: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                showToast(if (it.isSuccessful) R.string.success else R.string.error)
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
        userName = editName_View.text.toString()
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

    private fun excuteBtnString(loginType: LoginType): Int {
        return when (loginType) {
            LoginType.Login -> R.string.excute_login.toString().toInt()
            LoginType.Resister -> R.string.excute_register.toString().toInt()
            LoginType.PassChange -> R.string.excute_change.toString().toInt()
        }
    }

    private fun imageVisible(isVisible: Boolean) {
        if (isVisible) myImageviewBtn.visibility = View.VISIBLE
        else myImageviewBtn.visibility = View.INVISIBLE
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun start(activity: Activity) {
            activity.finishAffinity()
            FirebaseAuth.getInstance().signOut()
            activity.startActivity(Intent(activity, ResisterandLogin_Activity::class.java))
        }
    }
}

