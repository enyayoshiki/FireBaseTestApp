package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.CustomAdapter.AllFriendRecyclerViewAdapter
import com.example.firebasetestapp.CustomAdapter.InChatRoomRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.Friends
import com.example.firebasetestapp.extention.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_all_friend.*
import kotlinx.android.synthetic.main.activity_in__chat_room_.*

class AllFriend : AppCompatActivity() {

    private val customAdapter by lazy { AllFriendRecyclerViewAdapter(this) }
    private val db = FirebaseFirestore.getInstance()
    private var allFriends : MutableList<Friends>? = mutableListOf()
    private var fromId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_friend)
        supportActionBar?.hide()

        initialize()
    }

    private fun initialize(){
        initLayout()
        initData()
    }

    private fun initLayout(){
        initClick()
        initRecyclerView()
    }

    private fun initClick(){
        excute_researchFriend_allFriend_imageView.setOnClickListener{
            reseachFriend()
        }
        allFriendData_allFrined_imageView.setOnClickListener{
            initData()
        }
    }

    private fun initRecyclerView(){
        allFriend_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
    }

    private fun initData(){
        fromId = FirebaseAuth.getInstance().uid ?: ""
        db.collection("Friends").whereEqualTo("myId", fromId).get()
            .addOnCompleteListener {
                val result = it.result
                allFriends = result?.toObjects(Friends::class.java)
                if (allFriends != null) {
                    customAdapter.refresh(allFriends as MutableList<Friends>)
                }
            }
    }

    private fun reseachFriend() {
        val reserchName = edit_researchFriend_allFriend_editView.text

        if (reserchName.isNotEmpty()) {
            val researchFriend = allFriends?.filter { it.friendName.contains(reserchName)}?.toMutableList()
            if (researchFriend != null) {
                customAdapter.refresh(researchFriend)
                reserchName.clear()
                showToast(this, R.string.success)
            } else
                reserchName.clear()
                showToast(this, R.string.success)
        }
        else
            showToast(this,R.string.researchfriend_hint)
    }




    companion object {

        fun start(activity: Context) {
            val intent = Intent(activity, AllFriend::class.java)
            activity.startActivity(intent)
        }
    }
}