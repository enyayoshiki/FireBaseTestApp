package com.example.firebasetestapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class FirebaseTestApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        initTimber()
        initFirebase()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }
}