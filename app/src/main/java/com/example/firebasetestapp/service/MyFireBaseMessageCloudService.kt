package com.example.firebasetestapp.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.firebasetestapp.R
import com.example.firebasetestapp.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class MyFireBaseMessageCloudService: FirebaseMessagingService(){

    private val notificationManager by lazy { applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onNewToken(token: String) {
        // 端末＋アプリを一意に識別するためのトークンを取得
        Log.i("FIREBASE", "[SERVICE] Token = ${token ?: "Empty"}")
        //ToDo ここに送信する内容を記入するらしい

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.w("FCM_Custom", "onMessageReceived remoteMessage:$remoteMessage")
        showNotificationFromData(remoteMessage)
//
//        remoteMessage.let { message ->
//            // 通知メッセージ
//            message.notification?.let {
//                // 通知メッセージを処理
//                Log.w("push", "通知notification")
//
//            }
//
//            // データメッセージ
//            message.data?.let {
//                // データメッセージを処理
//                Log.w("push", "通知data")
//
//            }
//        }
    }

    private fun showNotificationFromData(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data["name"] ?: "たいとる"
        val body = data["message"] ?: "ぼでい"
        val roomId = data["roomId"] ?: ""
        val roomNotifyId = data["roomNotifyId"] ?: "0"
        var tempId = 0
        try {
            tempId = roomNotifyId.toInt()
        } catch(e: Exception) {}

        Log.w("FCM_Custom", "showNotificationFromData title:$title body:$body")
        showFcmNotification(title, body, null)
    }


    /**
     * Fcmの通知を表示
     *
     * @param title
     * @param message
     * @param redirectType
     * @param contentId
     * @param imageUrl
     */
    fun showFcmNotification(title: String, message: String, imageUrl: String?) {
        val intent = Intent(applicationContext, SplashActivity::class.java).putExtra("moveToRoom", "roomId")
        val tempTitle = if (title.isNotEmpty()) title else applicationContext.getString(R.string.app_name)
//        if (imageUrl == null)
            showNotification(message, intent, title = tempTitle)
//        else
//            imageUrl.makeBitmapFromUrl({
//                showNotification(NotificationType.FCM, message, intent, it, tempTitle)
//            }) {
//                showNotification(NotificationType.FCM, message, intent, title = tempTitle)
//            }
    }

    /**
     * 通知を表示
     *
     * @param type
     * @param content
     * @param intent
     * @param largeIcon
     * @param title
     */
    private fun showNotification(
        content: String,
        intent: Intent = Intent(applicationContext, SplashActivity::class.java),
        largeIcon: Bitmap? = null,
        title: String = applicationContext.getString(R.string.app_name)
    ) {
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()
        showNotification(title, content, pendingIntent, largeIcon)
    }

    /**
     * Channelの作成
     *
     * @param type
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channelId = "channelId"
        val channelName = "channelName"
        if (notificationManager.getNotificationChannel(channelId) == null)
            notificationManager.createNotificationChannel( NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelName
            })
    }

    /**
     * 通知の表示
     *
     * @param type
     * @param title
     * @param content
     * @param pendingIntent
     * @param largeIcon
     */
    private fun showNotification(title: String, content: String, pendingIntent: PendingIntent, largeIcon: Bitmap?) {
        val notification = NotificationCompat.Builder(applicationContext, "channelId")
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                largeIcon?.also {
                    setLargeIcon(it)
                }
            }
            .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(content))
            .build()

        notificationManager.notify(1, notification)
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