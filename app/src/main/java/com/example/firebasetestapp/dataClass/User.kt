package com.example.firebasetestapp.dataClass

import android.net.Uri
import com.google.firebase.storage.UploadTask


class User(val uid: String, val userName: String, val userImage: String?) {
    constructor(): this("", "", "")
}