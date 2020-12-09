package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment.MenuFragment_InChatRoom
import com.example.firebasetestapp.CustomAdapter.InChatRoomRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.FcmRequest
import com.example.firebasetestapp.dataClass.InChatRoom
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.extention.showToast
import com.example.firebasetestapp.helper.FcmSendHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_in__chat_room_.*
import kotlinx.android.synthetic.main.one_result_in_chatroom_other.*
import timber.log.Timber

class In_ChatRoom_Activity : AppCompatActivity() {

    private val customAdapter by lazy { InChatRoomRecyclerViewAdapter(this) }
    private val db = FirebaseFirestore.getInstance()
    private var otherId: String = ""
    private var otherName: String = ""
    private var otherImage: String = ""
    private var chatRoomId: String = ""
    private var fromId: String = ""
    private var myName: String = ""
    private var myImage: String = ""
    private var fcmTkList = mutableListOf<String?>()
    private var handler = Handler()


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
        otherName = intent.getStringExtra(OTHERNAME) ?: ""
        userName_inChatRoom_textView.text = otherName

        initClick()
        initRecyclerView()
    }

    private fun initData() {
        fromId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        otherId = intent.getStringExtra(OTHERID) ?: ""
        chatRoomId = intent.getStringExtra(CHATROOMID) ?: ""
        otherImage = intent.getStringExtra(OTHERIMAGE) ?: ""


        getMyData()
        if (otherId.isEmpty()) getOtherData() else getFcmTkList(mutableListOf(otherId))
        if (chatRoomId.isEmpty()) getChatRoomId() else getMessage()

    }

    private fun initClick() {

        excute_chat_inChatRoom_imageView.setOnClickListener {
            val message = edit_message_inChatRoom_editView.text.toString()
            if (message.isNotEmpty()) {
                if (chatRoomId.isEmpty()) creatChatRooms(message) else sendMessageInChatRoom(message)
            } else showToast(this, R.string.please_input_text)
        }

        openMenu_inChatRoom_imageView.setOnClickListener {
            MenuFragment_InChatRoom.startFriendMenu(this, chatRoomId, otherId)
        }
    }

    private fun initRecyclerView() {
        in_chatRoom_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
    }

    private fun getChatRoomId() {
        db.collection("ChatRooms").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val chatRooms = task.result?.toObjects(ChatRooms::class.java)
                chatRooms?.forEach {
                    if (it.userList.size == 2 && it.userIdList.containsAll(listOf(fromId, otherId))) {
                        chatRoomId = it.roomId
                        getMessage()
                    } else return@forEach
                }
            } else {
                return@addOnCompleteListener
            }
        }
    }

    private fun getMyData() {
        db.collection("Users").document(fromId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result
                    if (user != null) {
                        myImage = user.data?.get("userImage").toString()
                        myName = user.data?.get("userName").toString()
                    }
                }
            }
    }

    private fun getOtherData(){
        db.collection("ChatRooms").document(chatRoomId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val result = task.result?.toObject(ChatRooms::class.java)
                    val idList = result?.userIdList
                    val otherIdList = idList?.filterNot { it.contains(fromId) }
                    Timber.i("otherIdList"+otherIdList)
                    val otherNameMap = result?.userNameMap
                    otherName= otherNameMap?.filterNot { it.key == FirebaseAuth.getInstance().uid }?.values.toString()
                        .removePrefix("[").removeSuffix("]")
                    getFcmTkList(otherIdList)
                }
            }
    }

    private fun getFcmTkList(otherIdList: List<String>?) {

        otherIdList?.forEach {

        db.collection("Users").document(it).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fcmTkList.add(task.result?.get("fcmToken").toString())
                    }
                }
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
        showToast(this, R.string.get_message_text)
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
                showToast(this, R.string.success_sendmessage_to_thread_text)

                sendPushMessage(message)

            }
        hideProgress()
    }


    private fun creatChatRooms(message: String) {
        showProgress()

        chatRoomId = System.currentTimeMillis().toString()
        db.collection("ChatRooms").document(chatRoomId).set(ChatRooms().apply {
            userNameList = mutableListOf(myName, otherName)
            userIdList = mutableListOf(fromId, otherId)
            latestMessage = message
            roomId = chatRoomId
            userList = mutableListOf(User(fromId, myName, myImage,""),User(otherId, otherName, otherImage, ""))
            userNameMap = mutableMapOf(fromId to myName, otherId to otherName)

        })
            .addOnSuccessListener {
                showToast(this, R.string.success)
                sendMessageInChatRoom(message)
            }
            .addOnFailureListener {
                showToast(this, R.string.error)
            }
        hideProgress()
    }

    private fun sendPushMessage(message: String){

        fcmTkList.forEach{
            Timber.i("sendId : "+it)
            if (it == null) return
            FcmSendHelper.sendPush(FcmRequest().apply {
                this.to = it
                data.apply {
                    this.title = "${myName}からメッセージ"
                    this.message = message
                    this.roomId = chatRoomId
                }
            }, { // 成功したとき
                handler.post { Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show() }
            }, { // 失敗したとき
                handler.post { Toast.makeText(this, "失敗", Toast.LENGTH_SHORT).show() }
            })
        }
    }



    private var progressDialog: MaterialDialog? = null

    private fun showProgress() {
        hideProgress()
        progressDialog = this.let {
            MaterialDialog(it).apply {
                cancelable(false)
                setContentView(
                    LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false)
                )
                show()
            }
        }
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }



    companion object {
        const val CHATROOMID = "CHATROOMID"
        const val OTHERID = "OTHERID"
        const val OTHERNAME = "OTHERNAME"
        const val OTHERIMAGE = "OTHERIMAGE"


        fun startChatRooms(activity: Context, roomId: String, otherId: String, otherName: String, otherImage: String) {
            val intent = Intent(activity, In_ChatRoom_Activity::class.java)
            intent.putExtra(CHATROOMID, roomId)
            intent.putExtra(OTHERID, otherId)
            intent.putExtra(OTHERNAME, otherName)
            intent.putExtra(OTHERIMAGE, otherImage)
            activity.startActivity(intent)
        }
    }


//    private fun initOtherData(){
//        whereFrom = intent.getStringExtra(WHEREFROM) ?: ""
//        when(whereFrom){
//            "THREAD" -> getData()
//            "CHATROOMS" -> getDataFromChatRooms()
//            "ALLFRIEND" -> getData()
//            else -> return
//        }
//    }

//    private fun getRoomData(){
//
//        db.collection("Users").whereEqualTo("uid", otherId).get()
//            .addOnCompleteListener{
//                val User = it.result?.toObjects(User::class.java)
//                User?.forEach {
//                    otherName = it.userName
//                    otherImage = it.userImage ?: ""
//                    fcmTkList.add(it.fcmToken)
//                    getRoomId()
//                }
//                userName_inChatRoom_textView.text = otherName
//            }
//    }

//    private fun getDataFromChatRooms(){
//        chatRoomId = intent.getStringExtra(CHATROOMID) ?: ""
//        userName_inChatRoom_textView.text = intent.getStringExtra(OTHERNAME) ?: ""
//        getMessage()
//    }

//    private fun initMyDataSet(){
//        fromId = FirebaseAuth.getInstance().uid ?: ""
//        db.collection("Users").document(fromId).get()
//            .addOnSuccessListener {
//                myName = it["userName"].toString()
//                myImage = it["userImage"].toString()
//            }
//    }

//    private fun getRoomId() {
//                db.collection("ChatRooms").whereArrayContains("userList", fromId).get()
//                    .addOnCompleteListener { it ->
//                        val chatRoomsResult = it.result?.toObjects(ChatRooms::class.java)
//                        chatRoomsResult!!.forEach {
//                            if (it.userList.contains(otherId) && it.memberSize == 2) {
//                                chatRoomId = it.roomId
//                                getMessage()
//                            }
//                        }
//                    }
//                    .addOnFailureListener {
//                        Log.d("inChatRoom", "getroomId error")
//                        return@addOnFailureListener
//                    }
//        }

//    private fun getOtherData(){
//        db.collection("Users").document(otherId).get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    val other = task.result
//                    if (other != null){
//                        otherImage = other.data?.get("myImage").toString()
//                        otherName = other.data?.get("myName").toString()
//                    }
//                } else return@addOnCompleteListener
//            }
//    }



}


