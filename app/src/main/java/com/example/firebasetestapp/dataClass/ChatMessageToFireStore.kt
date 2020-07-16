package com.example.firebasetestapp.dataClass

class ChatMessageToFireStore(val text: String,val fromId: String, val toId: String, val time: String, val myImage: String){
    constructor(): this("", "", "","" , "")
}