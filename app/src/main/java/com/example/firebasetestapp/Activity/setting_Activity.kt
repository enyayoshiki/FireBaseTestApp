package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.LatestMessage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home_login.*

class setting_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }
}