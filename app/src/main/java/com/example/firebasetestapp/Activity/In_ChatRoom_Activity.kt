package com.example.firebasetestapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.CustomAdapter.InChatRoomRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.InChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_in__chat_room_.*

class In_ChatRoom_Activity : AppCompatActivity() {

    private val customAdapter by lazy { InChatRoomRecyclerViewAdapter(this) }
    private var progressDialog: MaterialDialog? = null
    private val db = FirebaseFirestore.getInstance()
    private var otherId: String = ""
    private var otherName : String = ""
    private var chatRoomId : String = ""
    private var fromId : String = ""
    private var myName : String = ""
    private var myImage : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in__chat_room_)
        supportActionBar?.hide()


        initialize()
        //        setTabLayout()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        chatRoomName_inChatRoom_textView.text = otherName

        if (edit_message_inChatRoom_editView.text.isNotEmpty())
        else excute_chat_inChatRoom_imageView.setOnClickListener {
            sendMessageInChatRoom()
        }

        initRecyclerView()
    }

    private fun initData() {
        otherId = intent.getStringExtra(In_Thread_Activity.OTHER_ID) ?: ""
        otherName = intent.getStringExtra(In_Thread_Activity.OTHER_NAME) ?: ""
        fromId = FirebaseAuth.getInstance().uid ?: ""
        db.collection("Users").document(fromId).get()
            .addOnSuccessListener {
                myName = it["userName"].toString()
                myImage = it["userImage"].toString()
            }
        db.collection("ChatRooms").whereArrayContains("userList", mutableListOf(otherId, fromId)).get()
            .addOnSuccessListener {
                if (it.isEmpty()) return@addOnSuccessListener
                chatRoomId = it.toObjects(InChatRoom::class.java)[1].toString()
                Log.d("inChatRoom", chatRoomId)
            }.addOnFailureListener{
                return@addOnFailureListener
            }
        getMessage()
            }



private fun initRecyclerView() {
        in_chatRoom_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
//            addOnScrollListener(scrollListener)
        }
    }

    private fun getMessage(){
        db.collection("InChatRoom").whereEqualTo("inChatRoomId", chatRoomId).orderBy("createdAt", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                if (it.isEmpty) return@addOnSuccessListener
                Log.d("inChatRoom", "getMessage成功")
                val messageInChatRoom = it.toObjects(InChatRoom::class.java)
                Log.d("inChatRoom", "$messageInChatRoom")
                customAdapter.refresh(messageInChatRoom)
            }
            .addOnFailureListener {
                Log.d("inChatRoom", "initData失敗")
            }
    }

    private fun sendMessageInChatRoom() {
        val sendMessageinChat = edit_message_inChatRoom_editView.text.toString()
        if (sendMessageinChat.isEmpty()) {
            return showToast(R.string.please_input_text)
        } else {
            creatChatRooms(sendMessageinChat)
            db.collection("InChatRoom").add(InChatRoom().apply {
                userList = mutableListOf(otherId, fromId)
                sendMessage = sendMessageinChat
                sendUserId = fromId
                sendUserName = myName
                sendUserImage = myImage
                sendtoOtherName = otherName
                inChatRoomId = chatRoomId
            })
                .addOnCompleteListener {
                    edit_message_inChatRoom_editView.text.clear()
                    showToast(R.string.success_sendmessage_to_thread_text)
                    getMessage()
                }
        }
    }

    private fun creatChatRooms(latestMessageInChat : String) {
        db.collection("ChatRooms").document(System.currentTimeMillis().toString()).set(ChatRooms().apply {
            if (chatRoomId.isEmpty()) {
                chatRoomId = System.currentTimeMillis().toString()
                roomId = chatRoomId
                userList = mutableListOf(otherId, fromId)
            }
            latestMessage = latestMessageInChat
        })
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }


//    private fun setTabLayout() {
////        fragmentを設置
//        val adapter = TagAdapter_HomeFragment(supportFragmentManager, this)
//        in_thread_recyclerView.adapter = adapter
////        ここで細かいタブ設定(getTabView)
//        mainThread_Tablayout.setupWithViewPager(in_thread_recyclerView)
//        for (i in 0 until adapter.count) {
//            val tab: TabLayout.Tab = mainThread_Tablayout.getTabAt(i)!!
//            tab.customView = adapter.getTabView(mainThread_Tablayout, i)
//        }
//    }

}


