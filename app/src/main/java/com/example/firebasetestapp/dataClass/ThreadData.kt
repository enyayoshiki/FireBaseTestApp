package com.example.firebasetestapp.dataClass

import java.util.*

class ThreadData {
    var name: String = ""
    var roomId: String = "${System.currentTimeMillis()}"
    var createdAt: Date = Date()
}