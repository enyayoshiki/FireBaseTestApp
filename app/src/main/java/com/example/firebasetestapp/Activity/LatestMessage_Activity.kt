package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetestapp.CustomAdapter.LatestMessageCustomAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.LatestMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home_login.*
import kotlinx.android.synthetic.main.chat_room.*

class LatestMessage_Activity:  AppCompatActivity() {

    private val customAdapter by lazy { LatestMessageCustomAdapter(this) }
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_login)
        Log.d("home", "home画面")
        supportActionBar?.title = "HOME"

        val fromId = FirebaseAuth.getInstance().uid
        if (fromId == null) {
            ResisterandLogin_Activity.start(this)
        }
        latestMessage_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
        latestMessageShow(fromId)
    }


    private fun latestMessageShow(fromId : String?) {
        Log.d("latestmessage", "latestChatLogShow")
        db.collection("LatestMessage:$fromId")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    Log.d("latestmessage", "Listen failed.", e)
                    return@addSnapshotListener
                }
                val latestMessage = snapshot.toObjects(LatestMessage::class.java)
                for (dc in snapshot!!.documentChanges){
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> customAdapter.refresh(latestMessage)
                        DocumentChange.Type.MODIFIED -> customAdapter.refresh(latestMessage)
                    }
                }
            }
        }


        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_homelogin, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            Log.d("home", "メニュー表示")
            when (item?.itemId) {
                R.id.newMessage_menu -> {
                   SelectUser_Activity.start(this)
                }

                R.id.setting_menu ->{

                }

                R.id.signOut_menu -> {
                  ResisterandLogin_Activity.start(this)
                }
            }
            return super.onOptionsItemSelected(item)
        }

    companion object {
        val USER_NAME = "USER_NAME"
        val USER_KEY = "USER_KEY"
        val USER_IMAGE = "USER_IMAGE"

        fun start(activity: Activity) {
            activity.finishAffinity()
            val intent = Intent(activity, LatestMessage_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}







