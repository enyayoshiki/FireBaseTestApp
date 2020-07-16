package com.example.firebasetestapp.dataClass

class LatestMessage (val text: String,val myId: String, val yourId: String, val yourName: String, val yourImage: String){
    constructor(): this("", "", "", "", "")
}
