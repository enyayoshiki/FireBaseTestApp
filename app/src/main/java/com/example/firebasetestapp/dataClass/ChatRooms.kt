package com.example.firebasetestapp.dataClass

import java.util.*

class ChatRooms {
    var userList = mutableListOf<String>()
    var roomId : String = ""
    var createdAt: Date = Date()
    var latestMessage : String = ""
}