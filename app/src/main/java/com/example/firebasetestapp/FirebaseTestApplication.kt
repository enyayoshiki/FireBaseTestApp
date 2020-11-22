package com.example.firebasetestapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class FirebaseTestApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseTestApplication.applicationContext = applicationContext
        initialize()
    }

    private fun initialize() {
        initTimber()
        initFirebase()
        initFcm()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }

    private fun initFcm() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    companion object {
        lateinit var applicationContext: Context
    }
}