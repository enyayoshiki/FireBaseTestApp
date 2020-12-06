package com.example.firebasetestapp.extention

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.google.firebase.messaging.RemoteMessage
import okhttp3.*
import java.io.IOException

fun View.Visible(isVisible : Boolean){
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}


fun showToast(context: Context, textId: Int) {
    Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
}