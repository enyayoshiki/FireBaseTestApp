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
import kotlinx.android.synthetic.main.chat_room.*

class In_ChatRoom_Activity : AppCompatActivity() {

    private val customAdapter by lazy { InChatRoomRecyclerViewAdapter(this) }
    private var progressDialog: MaterialDialog? = null
    private val db = FirebaseFirestore.getInstance()
    private var otherId: String = ""
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
        chatRoomName_inChatRoom_textView.text = intent.getStringExtra(In_Thread_Activity.OTHER_NAME) ?: ""

        if (edit_message_inChatRoom_editView.text.isNotEmpty())
        else excute_chat_inChatRoom_imageView.setOnClickListener {
            sendMessageInChatRoom()
        }
        initRecyclerView()
    }

    private fun initData() {
        otherId = intent.getStringExtra(In_Thread_Activity.OTHER_ID) ?: ""

        myDataSet()
        getRoomId()
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

                val messageInChatRoom = it.toObjects(InChatRoom::class.java)
                Log.d("inChatRoom", "getMessage : $messageInChatRoom")
                customAdapter.refresh(messageInChatRoom)
                in_chatRoom_recyclerView.scrollToPosition(customAdapter.itemCount - 1)
            }
            .addOnFailureListener {
                Log.d("inChatRoom", "getMessage失敗")
            }
    }

    private fun sendMessageInChatRoom() {
        val sendMessageinChat = edit_message_inChatRoom_editView.text.toString()

        if (sendMessageinChat.isEmpty()) {
            return showToast(R.string.please_input_text)
        }
        else {
            creatChatRooms(sendMessageinChat)
            db.collection("InChatRoom").add(InChatRoom().apply {
                sendMessage = sendMessageinChat
                sendUserId = fromId
                sendUserName = myName
                sendUserImage = myImage
                inChatRoomId = chatRoomId
            })
                .addOnCompleteListener {
                    edit_message_inChatRoom_editView.text.clear()
                    showToast(R.string.success_sendmessage_to_thread_text)
                    getMessage()
                }
        }
    }



    private fun myDataSet(){
        fromId = FirebaseAuth.getInstance().uid ?: ""
        db.collection("Users").document(fromId).get()
            .addOnSuccessListener {
                myName = it["userName"].toString()
                myImage = it["userImage"].toString()
            }
    }

    private fun getRoomId(){
        Log.d("inChatRoom", " getRoomId")
        Log.d("inChatRoom", "fromId : $fromId")
        db.collection("ChatRooms").whereArrayContains("userList", fromId).get()
            .addOnCompleteListener { it ->
                val result = it.result
                val chatRoomsResult = result?.toObjects(ChatRooms::class.java)
                Log.d("inChatRoom", "chatRoomId取得 : $chatRoomsResult")
                chatRoomsResult!!.forEach {
                    Log.d("inChatRoom", "chatRoomId取得 : $it")
                        if (it.userList.contains(otherId)) {
                            Log.d("inChatRoom", "chatRoomId取得　true : ${it.roomId}")
                            chatRoomId = it.roomId

                            getMessage()
                        }
                    else Log.d("inChatRoom", "chatRoomId取得　false : ${it.roomId}")
                    }
                }
            .addOnFailureListener {
                Log.d("inChatRoom", "chatRoomId失敗 : $it")
                return@addOnFailureListener
            }
        if (chatRoomId.isEmpty())
            chatRoomId =  System.currentTimeMillis().toString()

    }

    private fun creatChatRooms(latestMessageInChat : String) {
        db.collection("ChatRooms").document(chatRoomId).set(ChatRooms().apply {
            latestMessage = latestMessageInChat
            roomId = chatRoomId
            userList = mutableListOf(otherId, fromId)
            })
            .addOnSuccessListener {
                showToast(R.string.success)
            }
            .addOnFailureListener{
                showToast(R.string.error)
            }
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


