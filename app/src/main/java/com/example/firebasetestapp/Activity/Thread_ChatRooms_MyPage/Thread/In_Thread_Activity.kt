package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Login_Resister_PassChange.Login_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.CustomAdapter.InThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.InChatRoom
import com.example.firebasetestapp.dataClass.MessageToThread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_in__chat_room_.*
import kotlinx.android.synthetic.main.activity_in_thread_.*

class In_Thread_Activity : AppCompatActivity() {

    private val customAdapter by lazy { InThreadRecyclerViewAdapter(this) }
    private var progressDialog: MaterialDialog? = null
    private val db = FirebaseFirestore.getInstance()
    private var roomId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_thread_)
        supportActionBar?.hide()

        roomId = intent.getStringExtra(THREAD_ID) ?: ""
        Log.d("inThread", roomId)
        initialize()
        //        setTabLayout()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        threadName_inThread_textView.text = intent.getStringExtra(THREAD_NAME) ?: ""

        initRecyclerView()

        excute_chat_toThread_imageView.setOnClickListener {
            sendMessagetoThread()
        }
    }

    private fun initData() {
        db.collection("MessageToThread").whereEqualTo("threadId", roomId).orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener{snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED ->
                            customAdapter.refresh(snapshots.toObjects(MessageToThread::class.java))


                        DocumentChange.Type.MODIFIED ->
                            customAdapter.refresh(snapshots.toObjects(MessageToThread::class.java))

                        DocumentChange.Type.REMOVED ->
                            customAdapter.refresh(snapshots.toObjects(MessageToThread::class.java))
                    }
                    in_thread_recyclerView.scrollToPosition(customAdapter.itemCount - 1)
                }
            }
        showToast(R.string.get_message_text)
    }

    private fun initRecyclerView() {
        in_thread_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
//            addOnScrollListener(scrollListener)
        }
    }

    private fun sendMessagetoThread() {
        showProgress()
        val fromId = FirebaseAuth.getInstance().uid
        val sendMessage = edit_message_toThread_editView.text.toString()
        if (sendMessage.isNotEmpty()) {
            db.collection("Users").document("$fromId").get()
                .addOnSuccessListener {

                    db.collection("MessageToThread").add(MessageToThread().apply {
                        message = sendMessage
                        threadId = roomId
                        sendUserId = fromId!!
                        sendUserName = it["userName"].toString()
                        sendUserImage = it["userImage"].toString()
                    })
                        .addOnCompleteListener {
                            edit_message_toThread_editView.text.clear()
                            showToast(R.string.success_sendmessage_to_thread_text)
                            initData()
                            in_thread_recyclerView.scrollToPosition(customAdapter.itemCount - 1)
                        }
                }
        }else showToast(R.string.please_input_text)
        hideProgress()
    }

    private fun showToast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
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

    companion object {
        const val THREAD_ID = "THREAD_ID"
        const val THREAD_NAME = "THREAD_NAME"

        fun start(activity: Context, threadId: String, threadName: String) {
            val intent = Intent(activity, In_Thread_Activity::class.java)
            intent.apply {
                putExtra(THREAD_ID, threadId)
                putExtra(THREAD_NAME, threadName)
            }
            activity.startActivity(intent)
        }
    }
}