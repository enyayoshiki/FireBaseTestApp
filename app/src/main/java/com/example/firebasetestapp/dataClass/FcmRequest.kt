package com.example.firebasetestapp.dataClass

class FcmRequest {
    var to: String = ""
    var data: Data = Data()
    var priority: String = "high"

    class Data {
        var title: String = ""
        var message: String = ""
    }
}