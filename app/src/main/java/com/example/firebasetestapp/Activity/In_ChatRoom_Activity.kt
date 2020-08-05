package com.example.firebasetestapp.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRooms_Fragment
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
    private var otherName : String = ""
    private var otherImage : String = ""
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

        if (edit_message_inChatRoom_editView.text.isNotEmpty())
        else excute_chat_inChatRoom_imageView.setOnClickListener {
            sendMessageInChatRoom()
        }
        initRecyclerView()
    }

    private fun initData() {
        initOtherData()
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

    private fun initOtherData(){
            otherId = intent.getStringExtra(In_Thread_Activity.OTHER_ID) ?: ""
        if (otherId.isEmpty())
            otherId = intent.getStringExtra(ChatRooms_Fragment.OTHER_ID)!!

            otherName = intent.getStringExtra(In_Thread_Activity.OTHER_NAME) ?: ""
        if (otherName.isEmpty())
            otherName = intent.getStringExtra(ChatRooms_Fragment.OTHER_NAME)!!

            otherImage = intent.getStringExtra(In_Thread_Activity.OTHER_IMAGE) ?: ""
        if (otherImage.isEmpty())
            otherImage = intent.getStringExtra(ChatRooms_Fragment.OTHER_IMAGE)!!

        chatRoomName_inChatRoom_textView.text = otherName
    }

    private fun getMessage(){
        showProgress()
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
        hideProgress()
        showToast(R.string.get_message_text)
    }

    private fun sendMessageInChatRoom() {
        showProgress()
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
        hideProgress()
    }



    private fun myDataSet(){
        fromId = FirebaseAuth.getInstance().uid ?: ""
        db.collection("Users").document(fromId).get()
            .addOnSuccessListener {
                myName = it["userName"].toString()
                myImage = it["userImage"].toString()
            }
    }

    @SuppressLint("LogNotTimber")
    private fun getRoomId(){
        db.collection("ChatRooms").whereArrayContains("userList", fromId).get()
            .addOnCompleteListener { it ->
                val result = it.result
                val chatRoomsResult = result?.toObjects(ChatRooms::class.java)
                chatRoomsResult!!.forEach {
                        if (it.userList.contains(otherId)) {
                            chatRoomId = it.roomId

                            getMessage()
                        }
                    }
                }
            .addOnFailureListener {
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
            userNameMap = mutableMapOf(otherId to otherName, fromId to myName)
            userImageMap = mutableMapOf(otherId to otherImage, fromId to myImage)
            })
            .addOnSuccessListener {
                showToast(R.string.success)
            }
            .addOnFailureListener{
                showToast(R.string.error)
            }
    }

    private fun showProgress() {
        hideProgress()
        progressDialog = this.let {
            MaterialDialog(it).apply {
                cancelable(false)
                setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false))
                show()
            }
        }

    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
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


