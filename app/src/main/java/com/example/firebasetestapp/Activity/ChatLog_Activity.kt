package com.example.firebasetestapp.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintSet.GONE
import com.example.firebasetestapp.dataClass.ChatMessageToFireStore
import com.example.firebasetestapp.CustomAdapter.MessageAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.LatestMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.chat_room.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLog_Activity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val customAdapter by lazy { MessageAdapter(this) }

    var username = ""
    var toId = ""
    var yourImage = ""
    val fromId = FirebaseAuth.getInstance().uid
    var userImage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)
         username = intent.getStringExtra(SelectUser_Activity.USER_NAME)
        if (username.isEmpty()) username = intent.getStringExtra(LatestMessage_Activity.USER_NAME)

         toId = intent.getStringExtra(SelectUser_Activity.USER_KEY)
        if (toId.isEmpty()) username = intent.getStringExtra(LatestMessage_Activity.USER_KEY)

        yourImage = intent.getStringExtra(SelectUser_Activity.USER_IMAGE)
        if (yourImage.isEmpty()) yourImage = intent.getStringExtra(LatestMessage_Activity.USER_IMAGE)

        supportActionBar?.title = username

        Log.d("chat", "fromId : $fromId")
        Log.d("chat", "toId : $toId")

        db.collection("User").document("$fromId").get()
            .addOnSuccessListener {
                userImage = it["userImage"].toString()
            }

        listemMessage()

        sendMassageBtn.setOnClickListener {
            sendMessageToFireStore()
        }
        chat_log_recyclerview.scrollToPosition(customAdapter.itemCount - 1)

    }


    private fun listemMessage() {

        db.collection("ChatLogMessage").document("$fromId").collection("$toId")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    Log.d("chat", "Listen failed.", e)
                    return@addSnapshotListener
                }
                chat_log_recyclerview.apply {
                    adapter = customAdapter
                    setHasFixedSize(true)
                }
                val chatMessage = snapshot.toObjects(ChatMessageToFireStore::class.java)
                Log.d("chat", "chatId(from/to) : ${fromId}/${toId}")
                Log.d("chat", "chatId(to/from) : ${toId}/${fromId}")
                customAdapter.refresh(chatMessage)
                chat_log_recyclerview.scrollToPosition(customAdapter.itemCount - 1)
            }
    }

    private fun sendMessageToFireStore() {
        var username = intent.getStringExtra(SelectUser_Activity.USER_NAME)
        val text = edit_chat_massage.text.toString()
        val time: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val chatMessage = ChatMessageToFireStore(text, fromId!!, toId!!, time, userImage)
        var latestMessage = LatestMessage(text, fromId!!, toId!!, username, yourImage )

        if (text.isEmpty()) return
        db.collection("ChatLogMessage").document("$fromId").collection("$toId").add(chatMessage)
            .addOnSuccessListener {
                chat_log_recyclerview.scrollToPosition(customAdapter.itemCount - 1)
                edit_chat_massage.text.clear()
                Log.d("chat", "saveUserDatatoFireStore")

                db.collection("ChatLogMessage").document("$toId").collection("$fromId").add(chatMessage)

                db.collection("LatestMessage:$fromId").document("${fromId}and${toId}").set(latestMessage)
                    .addOnSuccessListener {
                        db.collection("User").document("$fromId").get()
                            .addOnSuccessListener {
                               latestMessage = LatestMessage(text, toId!!, fromId!!, it["username"] as String, userImage)
                    db.collection("LatestMessage:$toId").document("${toId}and${fromId}").set(latestMessage)
                    }
                }
            }
            .addOnFailureListener {
                Log.d("chat", "$it")
            }
    }
}
