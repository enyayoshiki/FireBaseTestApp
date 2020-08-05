package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.CustomAdapter.InThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.MessageToThread
import com.google.firebase.auth.FirebaseAuth
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

        roomId = intent.getStringExtra(Thread_Fragment.THREAD_ID)

        initialize()
        //        setTabLayout()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        threadName_inThread_textView.text = intent.getStringExtra(Thread_Fragment.THREAD_NAME)

        initRecyclerView()

        excute_chat_toThread_imageView.setOnClickListener {
            sendMessagetoThread()
        }
    }

    private fun initData() {

        db.collection("MessageToThread").whereEqualTo("threadId", roomId).orderBy("createdAt", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                Log.d("inThread", "initData成功")
                val message = it.toObjects(MessageToThread::class.java)
                Log.d("inThread", "$message")
                customAdapter.refresh(message)
                in_thread_recyclerView.scrollToPosition(customAdapter.itemCount - 1)
            }
            .addOnFailureListener {
                Log.d("inThread", "initData失敗")
            }
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
                            hideProgress()
                        }
                }
        }else showToast(R.string.please_input_text)
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
        val OTHER_ID = "OTHER_ID"
        val OTHER_NAME = "OTHER_NAME"
        val OTHER_IMAGE = "OTHER_IMAGE"

        fun start(activity: Activity, roomId: String) {
            val intent = Intent(activity, In_Thread_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}