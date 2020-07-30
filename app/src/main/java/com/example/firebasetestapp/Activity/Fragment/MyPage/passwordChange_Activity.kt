package com.example.firebasetestapp.Activity.Fragment.MyPage

import android.app.Activity
import android.content.Intent

class passwordChange_Activity {

    companion object{
        fun start(activity: Activity) {
            val intent = Intent(activity, passwordChange_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}