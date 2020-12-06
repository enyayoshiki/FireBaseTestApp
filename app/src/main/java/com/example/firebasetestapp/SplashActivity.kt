package com.example.firebasetestapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.Activity.Login_Resister_PassChange.Login_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.dataClass.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var roomId: String = ""

    private val runnable = Runnable {

        val user = auth.currentUser
        roomId = intent.getStringExtra("moveToRoom") ?: ""
        Timber.i("roomID : ${roomId}")
        if (user == null){
            Login_Activity.start(this)
        } else if (roomId.isNotEmpty()) {
            getUserData(user.uid, roomId)
        } else {
            HomeFragment_Activity.start(this)
        }
    }

    private fun getUserData(uid : String, roomId: String) {
        FirebaseFirestore.getInstance().collection("Users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.toObjects(User::class.java)?.firstOrNull { it.uid == uid }?.also {
                        updataFcmToken(it)
                    } ?: kotlin.run {
                        HomeFragment_Activity.start(this)
                    }
                } else {
                    HomeFragment_Activity.start(this)
                }
            }
    }

    private fun updataFcmToken(user: User) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val newFcmToken = if (task.isSuccessful) task.result?.token ?: "" else ""
            FirebaseFirestore.getInstance().collection("Users").document(user.uid)
                .set(user.apply {
                    fcmToken = newFcmToken
                })
                .addOnSuccessListener {
                    if (roomId.isEmpty()) HomeFragment_Activity.start(this)
                    else In_ChatRoom_Activity.startChatRooms(this,roomId, "", "", "")
                }
                .addOnFailureListener{
                    if (roomId.isEmpty()) HomeFragment_Activity.start(this)
                    else In_ChatRoom_Activity.startChatRooms(this,roomId, "", "", "")
                }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知チャンネルを作成する
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("push", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            Log.i("push", "[CALLBACK] Token = ${task.result?.token}")
        }

        handler.postDelayed(runnable, 1000)
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacks(runnable)
    }
}