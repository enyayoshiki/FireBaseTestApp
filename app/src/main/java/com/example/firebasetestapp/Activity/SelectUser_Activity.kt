package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetestapp.CustomAdapter.SelectUserAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_selectuser.*

class SelectUser_Activity :  AppCompatActivity() {

    private val customAdapter by lazy { SelectUserAdapter(this) }
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectuser)
        Log.d("chooseuseer", "選択画面")
        supportActionBar?.title = "Select User"

        chooseUser_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
        fetchData()
    }

    private fun fetchData() {
        Log.d("chooseuser", "fetchData開始")
        db.collection("User").get().addOnSuccessListener {
            Log.d("chooseuser", "fetchData実行")

            val user = it.toObjects(User::class.java)
            customAdapter.refresh(user)
        }
            .addOnFailureListener {
                Log.d("chooseuser", "$it")
            }
    }

    companion object {
        val USER_NAME = "USER_NAME"
        val USER_KEY = "USER_KEY"
        val USER_IMAGE = "USER_IMAGE"

        fun start(activity: Activity) {
            val intent = Intent(activity, SelectUser_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}

