package com.example.firebasetestapp.helper

import com.example.firebasetestapp.FirebaseTestApplication.Companion.applicationContext
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.FcmRequest
import com.google.gson.Gson
import okhttp3.*
import timber.log.Timber
import java.io.IOException

object FcmSendHelper {

    fun sendPushAll(data: FcmRequest.Data, onSuccess: () -> Unit, onFailure: () -> Unit) {
        sendPush(FcmRequest().apply {
            to = "/topics/all"
            this.data = data
        }, onSuccess, onFailure)
    }

    fun sendPush(request: FcmRequest, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(applicationContext.getString(R.string.fcm_end_point))
            .addHeader(
                "Authorization",
                "key=${applicationContext.getString(R.string.fcm_server_key)}"
            )
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    Gson().toJson(request)
                )
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                Timber.i("onResponse response:${response.body()?.string()}")
                onSuccess.invoke()
            }
        })
    }
}