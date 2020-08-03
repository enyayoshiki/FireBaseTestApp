package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetestapp.R
import kotlinx.android.synthetic.main.activity_setting.*

class setting_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.title = getString(R.string.setting_text)

        initialize()
    }
    private fun initialize(){
        initilayout()
    }

    private fun initilayout(){
        profile_setting_textView.setOnClickListener{
            profileChange_Activity.start(this)
        }
        account_setting_textView.setOnClickListener{
            accountChange_Activity.start(this)
        }
        password_setting_textView.setOnClickListener{
            passwordChange_Activity.start(this)
        }
    }

    companion object{
        fun start(activity: Activity) {
            val intent = Intent(activity, setting_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}