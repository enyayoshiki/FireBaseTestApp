package com.example.firebasetestapp

import java.util.*

class ChatRoomData {
    var name: String = ""
    var roomId: String = "${System.currentTimeMillis()}"
    var createdAt: Date = Date()
}