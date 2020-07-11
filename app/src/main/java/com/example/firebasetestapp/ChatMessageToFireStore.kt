package com.example.firebasetestapp

import java.util.*

class ChatMessage(val id : String, val text: String, val fromId : String, val toId : String){
    constructor(): this("","", "","")
}

class ChatMessageToFireStore(val text: String,val fromId: String, val toId: String, val time: String){
    constructor(): this("", "", "","" )
}
