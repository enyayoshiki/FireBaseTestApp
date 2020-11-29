package com.example.firebasetestapp.dataClass

import android.net.Uri
import com.google.firebase.storage.UploadTask


data class User (
    var uid: String = "",
    var userName: String = "",
    var userImage: String? = "",
    var fcmToken: String? = ""
)
