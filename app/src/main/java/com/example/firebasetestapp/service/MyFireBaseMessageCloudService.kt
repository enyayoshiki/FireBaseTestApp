package com.example.firebasetestapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class MyFireBaseMessageCloudService: FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        // 端末＋アプリを一意に識別するためのトークンを取得
        Log.i("FIREBASE", "[SERVICE] Token = ${token ?: "Empty"}")
        //ToDo ここに送信する内容を記入するらしい

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        showNotification(remoteMessage)

        remoteMessage.let { message ->
            // 通知メッセージ
            message.notification?.let {
                // 通知メッセージを処理
                Log.w("push", "通知notification")

            }

            // データメッセージ
            message.data?.let {
                // データメッセージを処理
                Log.w("push", "通知data")

            }
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage){
        val dataBody = remoteMessage.notification?.body
        val dataTitle = remoteMessage.notification?.title
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId).apply {
            setSmallIcon(R.drawable.sample_frontimage)
            setContentTitle(dataTitle)
            setContentText(dataBody)
            setAutoCancel(true)
            setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)

        }
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_name)
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        notificationManager.notify(createNotificationId(), notificationBuilder.build())
    }

    /**
     * 通知IDを作成する
     */
    private fun createNotificationId() : Int{
        val now = Date()
        return Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now))
    }
}