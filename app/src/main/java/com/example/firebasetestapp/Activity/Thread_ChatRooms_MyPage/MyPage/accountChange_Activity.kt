package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.app.Activity
import android.content.Intent

class accountChange_Activity {

    companion object{
        fun start(activity: Activity) {
            val intent = Intent(activity, accountChange_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}