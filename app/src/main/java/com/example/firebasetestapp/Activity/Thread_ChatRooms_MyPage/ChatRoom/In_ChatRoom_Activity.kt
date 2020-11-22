package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment.MenuFragment_InChatRoom
import com.example.firebasetestapp.CustomAdapter.InChatRoomRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.InChatRoom
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.extention.sendPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_in__chat_room_.*

class In_ChatRoom_Activity : AppCompatActivity() {

    private val customAdapter by lazy { InChatRoomRecyclerViewAdapter(this) }
    private val db = FirebaseFirestore.getInstance()
    private var whereFrom: String = ""
    private var otherId: String = ""
    private var otherName : String = ""
    private var otherImage : String = ""
    private var chatRoomId : String = ""
    private var fromId : String = ""
    private var myName : String = ""
    private var myImage : String = ""
    private var fcmTkList = mutableListOf<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in__chat_room_)
        supportActionBar?.hide()

        initialize()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        initClick()
        initRecyclerView()
    }

    private fun initData() {
        initOtherData()
        initMyDataSet()
    }

    private fun initClick(){

            excute_chat_inChatRoom_imageView.setOnClickListener {
                creatChatRooms()
        }

        openMenu_inChatRoom_imageView.setOnClickListener{
            val intent = Intent(this, MenuFragment_InChatRoom::class.java)
            intent.putExtra(CHATROOMID, chatRoomId)

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        in_chatRoom_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
    }

    private fun initOtherData(){
        whereFrom = intent.getStringExtra(WHEREFROM) ?: ""
        when(whereFrom){
            "THREAD" -> getData()
            "CHATROOMS" -> getDataFromChatRooms()
            "ALLFRIEND" -> getData()
            else -> return
        }
    }

    private fun getData(){
        otherId = intent.getStringExtra(OTHERID) ?: ""

        db.collection("Users").whereEqualTo("uid", otherId).get()
            .addOnCompleteListener{
                val User = it.result?.toObjects(User::class.java)
                User?.forEach {
                    otherName = it.userName
                    otherImage = it.userImage ?: ""
                    fcmTkList.add(it.fcmToken)
                    getRoomId()
                }
                userName_inChatRoom_textView.text = otherName
            }
    }

    private fun getDataFromChatRooms(){
        chatRoomId = intent.getStringExtra(CHATROOMID) ?: ""
        userName_inChatRoom_textView.text = intent.getStringExtra(OTHERNAME) ?: ""
        getMessage()
    }

    private fun initMyDataSet(){
        fromId = FirebaseAuth.getInstance().uid ?: ""
        db.collection("Users").document(fromId).get()
            .addOnSuccessListener {
                myName = it["userName"].toString()
                myImage = it["userImage"].toString()
            }
    }

    private fun getRoomId() {
                db.collection("ChatRooms").whereArrayContains("userList", fromId).get()
                    .addOnCompleteListener { it ->
                        val chatRoomsResult = it.result?.toObjects(ChatRooms::class.java)
                        chatRoomsResult!!.forEach {
                            if (it.userList.contains(otherId) && it.memberSize == 2) {
                                chatRoomId = it.roomId
                                getMessage()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d("inChatRoom", "getroomId error")
                        return@addOnFailureListener
                    }
        }



    private fun getMessage() {
            db.collection("InChatRoom").whereEqualTo("inChatRoomId", chatRoomId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED ->
                                customAdapter.refresh(snapshots.toObjects(InChatRoom::class.java))

                            DocumentChange.Type.MODIFIED ->
                                customAdapter.refresh(snapshots.toObjects(InChatRoom::class.java))

                            DocumentChange.Type.REMOVED ->
                                customAdapter.refresh(snapshots.toObjects(InChatRoom::class.java))
                        }
                        in_chatRoom_recyclerView.scrollToPosition(customAdapter.itemCount - 1)
                    }
                }
            showToast(R.string.get_message_text)
        }

    private fun sendMessageInChatRoom(message: String) {
        showProgress()

        db.collection("InChatRoom").add(InChatRoom().apply {
            sendMessage = message
            sendUserId = fromId
            sendUserName = myName
            sendUserImage = myImage
            inChatRoomId = chatRoomId
        })
            .addOnCompleteListener {
                edit_message_inChatRoom_editView.text.clear()
                showToast(R.string.success_sendmessage_to_thread_text)
                getMessage()
                for (i in fcmTkList){
                    sendPush(i, message, chatRoomId )
                }
            }
        hideProgress()
    }


    private fun creatChatRooms() {
        showProgress()
        val sendMessageInChat = edit_message_inChatRoom_editView.text.toString()

        if (sendMessageInChat.isEmpty()) {
            hideProgress()
            return showToast(R.string.please_input_text)
        }
        if (chatRoomId.isNotEmpty()) {
            db.collection("ChatRooms").document(chatRoomId)
                .update(mapOf(
                    "latestMessage" to sendMessageInChat)
                )
            sendMessageInChatRoom(sendMessageInChat)
        }
         else {
            chatRoomId = System.currentTimeMillis().toString()
            db.collection("ChatRooms").document(chatRoomId).set(ChatRooms().apply {
                memberSize = 2
                latestMessage = sendMessageInChat
                roomId = chatRoomId
                userList = mutableListOf(otherId, fromId)
                userNameMap = mutableMapOf(otherId to otherName, fromId to myName)
                userImageMap = mutableMapOf(otherId to otherImage, fromId to myImage)
                frontImage = R.drawable.sample_frontimage.toString()

                sendMessageInChatRoom(sendMessageInChat)
            })
                .addOnSuccessListener {
                    showToast(R.string.success)
                }
                .addOnFailureListener {
                    showToast(R.string.error)
                }
        }
        hideProgress()
    }


    private var progressDialog: MaterialDialog? = null

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

    companion object{
        const val CHATROOMID = "CHATROOMID"
        const val OTHERID = "OTHERID"
        const val OTHERNAME = "OTHERNAME"
        const val WHEREFROM = "WHEREFROM"

        fun  startFromThread(activity: Context, otherId : String, whereFrom: String){
            val intent = Intent(activity, In_ChatRoom_Activity::class.java)
            intent.putExtra(OTHERID, otherId)
            intent.putExtra(WHEREFROM, whereFrom)
            activity.startActivity(intent)
        }

        fun startFromChatRooms(activity: Context, roomId : String, whereFrom: String, otherName: String){
            val intent = Intent(activity, In_ChatRoom_Activity::class.java)
            intent.putExtra(CHATROOMID, roomId)
            intent.putExtra(WHEREFROM, whereFrom)
            intent.putExtra(OTHERNAME, otherName)
            activity.startActivity(intent)
        }

        fun  startFromAllFriend(activity: Context, otherId : String, whereFrom: String){
            val intent = Intent(activity, In_ChatRoom_Activity::class.java)
            intent.putExtra(OTHERID, otherId)
            intent.putExtra(WHEREFROM, whereFrom)
            activity.startActivity(intent)
        }
    }
}


