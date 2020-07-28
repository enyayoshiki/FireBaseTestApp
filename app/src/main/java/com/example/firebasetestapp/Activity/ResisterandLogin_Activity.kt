package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasetestapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_resister_login.*

class ResisterandLogin_Activity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister_login)
        initialize()
    }

    private fun initialize() {

        initLayout()
    }

    private fun initLayout() {
        supportActionBar?.hide()

        excute_login_Btn.setOnClickListener {
            val email = editMail_View.text.toString()
            val pass = editPass_View.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "email//passwordを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email, pass)
        }


        to_Resister.setOnClickListener{
            Resister_Activity.start(this)
        }

        to_ChangePassword.setOnClickListener{
            ChangePassword_Activity.start(this)
        }
        closeImageView.setOnClickListener {
            finish()
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

