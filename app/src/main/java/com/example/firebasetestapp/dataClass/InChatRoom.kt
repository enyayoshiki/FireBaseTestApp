package com.example.firebasetestapp.dataClass

import java.util.*

class InChatRoom {
    var userList = mutableListOf<String>()
    var sendMessage: String = ""
    var sendUserId : String = ""
    var sendUserName : String = ""
    var sendUserImage: String = ""
    var sendtoOtherName: String = ""
    var inChatRoomId : String = ""
    var createdAt: Date = Date()
}