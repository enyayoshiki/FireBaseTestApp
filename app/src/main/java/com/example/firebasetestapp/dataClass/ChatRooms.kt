package com.example.firebasetestapp.dataClass

import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ChatRooms {
    var userList = mutableListOf<String>()
    var userNameMap = mutableMapOf<String, String>()
    var userImageMap = mutableMapOf<String, String>()
    var roomId : String = ""
    var createdAt: Date = Date()
    var latestMessage : String = ""
}