package com.example.firebasetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.firebasetestapp.Activity.Fragment.HomeFragment_Activity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val runnable = Runnable {


        HomeFragment_Activity.start(this)
//        if (auth.currentUser !== null) {
//            LatestMessage_Activity.start(
//                this
//            )
//        }

//        val intent = Intent(this, ResisterandLogin_Activity::class.java)
//        startActivity(intent)

        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        handler.postDelayed(runnable, 1000)
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacks(runnable)
    }
}