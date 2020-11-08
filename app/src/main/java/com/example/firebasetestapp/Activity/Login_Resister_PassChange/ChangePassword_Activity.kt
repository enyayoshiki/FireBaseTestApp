package com.example.firebasetestapp.Activity.Login_Resister_PassChange

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password_.*

class ChangePassword_Activity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password_)
        supportActionBar?.hide()

        val email = editMail_changePassword_View.text.toString()

        excute_changePass_Btn.setOnClickListener {
            if (email.isEmpty()) {
                changeRegister(email)
            } else
                showToast(R.string.warn_mail)
            return@setOnClickListener
        }
    }

    private fun changeRegister(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(R.string.success)

                  Login_Activity.start(this)
                } else if (it.isCanceled) showToast(R.string.error)

            }.addOnFailureListener {
                Log.d("error", "$it")
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("error", "$it")
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }
    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, ChangePassword_Activity::class.java))
        }
    }
}