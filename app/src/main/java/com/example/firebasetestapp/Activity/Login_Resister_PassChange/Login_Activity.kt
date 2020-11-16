package com.example.firebasetestapp.Activity.Login_Resister_PassChange

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_resister_login.*

class Login_Activity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var progressDialog: MaterialDialog? = null


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
            ChangePassword_Activity.start(
                this
            )
        }

        closeImageView.setOnClickListener {
            finish()
        }
    }

    private fun login(email: String, pass: String) {
        showProgress()
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {

                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (it.isSuccessful && uid != null) {
                    showToast(R.string.success)
                    getUserData(uid)
//                   HomeFragment_Activity.start(this)

                } else Toast.makeText(
                    this,
                    R.string.error, Toast.LENGTH_SHORT
                ).show()

            }.addOnFailureListener {
                Log.d("error", "$it")
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserData(uid: String) {
        FirebaseFirestore.getInstance().collection("Users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.toObjects(User::class.java)?.firstOrNull { it.uid == uid }?.also {
                        updateFcmToken(it)
                    } ?: run {
                        HomeFragment_Activity.start(this)
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.error, Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateFcmToken(user: User) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val newFcmToken = if (task.isSuccessful) task.result?.token ?: "" else ""
            FirebaseFirestore.getInstance().collection("Users").document(user.uid)
                .set(user.apply {
                    fcmToken = newFcmToken
                })
                .addOnSuccessListener {
                    showToast(R.string.success)
                    HomeFragment_Activity.start(this)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                }
        }
    }

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

    companion object {
        fun start(activity: Activity) {
            activity.finishAffinity()
            FirebaseAuth.getInstance().signOut()
            activity.startActivity(Intent(activity, Login_Activity::class.java))
        }
    }
}

