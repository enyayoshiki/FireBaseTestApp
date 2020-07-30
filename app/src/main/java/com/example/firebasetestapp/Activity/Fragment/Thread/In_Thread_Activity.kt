package com.example.firebasetestapp.Activity.Fragment.Thread

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.SelectUser_Activity
import com.example.firebasetestapp.CustomAdapter.MainThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_in_thread_.*
import kotlinx.android.synthetic.main.mainthread_fragment.*

class In_Thread_Activity : AppCompatActivity() {

    private lateinit var customAdapter: MainThreadRecyclerViewAdapter
    private var progressDialog: MaterialDialog? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_thread_)
        supportActionBar?.hide()

        initialize()
        //        setTabLayout()
    }
    private fun initialize(){
        initLayout()
        initData()
    }

    private fun initLayout(){
        initRecyclerView()

        excute_chat_toThread_imageView.setOnClickListener{
            sendMessagetoThread()
        }

    }
    private fun initData(){
        val roomId = intent.getStringExtra(Thread_Fragment.ROOM_ID)
        db.collection("message").whereEqualTo("roomId", roomId).get()
            .addOnSuccessListener {
                val message = it.toObjects()
            }

    }

    private fun initRecyclerView(){
            this.also {
                customAdapter = MainThreadRecyclerViewAdapter(it)
            }
            mainThead_recyclerView_fragment.apply {
                adapter = customAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
//            addOnScrollListener(scrollListener)
            }
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

//    companion object {
//        val CHATROOM_ID = "CHATROOM_ID"
//
//        fun start(activity: Activity, roomId: String) {
//            val intent = Intent(activity, In_Thread_Activity::class.java)
//            intent.putExtra(CHATROOM_ID, roomId)
//            activity.startActivity(intent)
//        }
//    }
}