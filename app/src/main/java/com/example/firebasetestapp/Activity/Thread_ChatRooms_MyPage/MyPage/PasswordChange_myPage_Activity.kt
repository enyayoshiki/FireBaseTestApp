package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password_.excute_changePass_Btn
import kotlinx.android.synthetic.main.activity_profilechange_password_.*

class PasswordChange_myPage_Activity : AppCompatActivity()  {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilechange_password_)
        supportActionBar?.hide()



        excute_changePass_Btn.setOnClickListener {

            val email = edit_mailadress_changePass_editView.text ?: ""
            if (email.isNotEmpty()){
                changeRegister(email.toString())
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
                    HomeFragment_Activity.start(this)
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

    companion object{
        fun start(activity: Activity) {
            val intent = Intent(activity, PasswordChange_myPage_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}