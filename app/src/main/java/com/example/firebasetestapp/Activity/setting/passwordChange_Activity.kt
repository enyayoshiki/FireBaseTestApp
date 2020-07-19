package com.example.firebasetestapp.Activity.setting

import android.app.Activity
import android.content.Intent
import com.example.firebasetestapp.Activity.SelectUser_Activity

class passwordChange_Activity {

    companion object{
        fun start(activity: Activity) {
            val intent = Intent(activity, passwordChange_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}