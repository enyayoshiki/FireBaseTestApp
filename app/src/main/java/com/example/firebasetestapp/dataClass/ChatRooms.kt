package com.example.firebasetestapp.dataClass


import java.util.*
class ChatRooms {
    var userNameList: MutableList<String> = mutableListOf()
    var userIdList : MutableList<String> = mutableListOf()
    var userNameMap: MutableMap<String, String> = mutableMapOf()
    var userList : MutableList<User> = mutableListOf()
    var roomId : String = ""
    var createdAt: Date = Date()
    var latestMessage : String = ""
    var frontImage : String = ""
}
