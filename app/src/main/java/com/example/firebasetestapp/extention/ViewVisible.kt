package com.example.firebasetestapp.extention

import android.util.Log
import android.view.View
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.google.firebase.messaging.RemoteMessage
import okhttp3.*
import java.io.IOException

fun View.Visible(isVisible : Boolean){
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun sendPush( fcmToken: String?, message: String, roomId: String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader(
                "Authorization",
                "key=AAAAYoCkPaY:APA91bFAxP61YE38Sc33dA8uVCzmU0FtIKzNdTjk5LUXkhPkQneTFGjKHpcqlQyaXHPfFkcE7SVVNo0pDZms4fmfIT7reBFopEnPimgvz4229howecP8kmDj2pMfE4UMXXIh0x3XUN-O"
            )
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    "{\"to\":\"$fcmToken\",\"data\":{\"name\":\"FireStoreTestApp\",\"message\":\"$message\",\"roomId\":\"$roomId\"}}"
                )
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM_Custom", "失敗")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("FCM_Custom", "成功")
            }
        })
}