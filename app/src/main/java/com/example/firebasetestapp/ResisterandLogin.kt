package com.example.firebasetestapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.ConstraintWidget.INVISIBLE
import androidx.constraintlayout.solver.widgets.ConstraintWidget.VISIBLE
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_resister_login.*
import com.squareup.picasso.Picasso
import java.util.*


class ResisterandLogin : AppCompatActivity() {

    var nowLoginType = LoginType.Login
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister_login)
        initialize()
        imageVisible(false)
        changeLayout(LoginType.Login)
    }

    private fun initialize() {
        initLayout()
    }

    private fun initLayout() {
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
                    this@ResisterandLogin,
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

                    val intent = Intent(this,HomeLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
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
            Log.d("error", "$selectImageUri")
            Picasso.get().load(selectImageUri).into(myImageview)
            myImageviewBtn.alpha = 0f
        }
    }

    private fun resisterImage() {
        if (selectImageUri == null) return

        val fileName = UUID.randomUUID().toString()
        Log.d("resister", "$fileName")
        val ref = FirebaseStorage.getInstance().getReference("image/$fileName")
        ref.putFile(selectImageUri!!).addOnSuccessListener {
            Log.d("resister", "$it")
            saveUserDatatoFirebase(it.toString())
        }
    }

    private fun saveUserDatatoFirebase(saveImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        Log.d("resister", "Not yet")
        val user = User(uid, editName_View.text.toString(), saveImageUrl)
        ref.setValue(user).addOnSuccessListener {
            Log.d("resister", "success")

            val intent = Intent(this,HomeLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }.addOnFailureListener{
            Log.d("error","$it")
        }
    }


    private fun excuteBtnString(loginType: LoginType): Int {
        return when (loginType) {
            LoginType.Login -> R.string.excute_login.toString().toInt()
            LoginType.Resister -> R.string.excute_register.toString().toInt()
            LoginType.PassChange -> R.string.excute_change.toString().toInt()
        }
    }

    @SuppressLint("WrongConstant")
    private fun imageVisible(isVisible: Boolean) {
        if (isVisible) myImageviewBtn.visibility = VISIBLE else myImageviewBtn.visibility =
            INVISIBLE
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }
}

