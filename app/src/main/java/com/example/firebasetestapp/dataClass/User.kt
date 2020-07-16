package com.example.firebasetestapp.dataClass

import android.net.Uri
import com.google.firebase.storage.UploadTask


class User(val uid: String, val username: String, val userImage: String?) {
    constructor(): this("", "", "")
}