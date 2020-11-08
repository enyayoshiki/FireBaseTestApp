package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.CustomAdapter.AddChatMemberRecyclerViewAdapter
import com.example.firebasetestapp.CustomAdapter.AddFriendRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.Friends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_chatmember_menufragment.*
import kotlinx.android.synthetic.main.add_friend_menufragement.*

class Add_ChatMember_Fragment: Fragment() {

    private lateinit var customAdapter: AddChatMemberRecyclerViewAdapter
    private val db = FirebaseFirestore.getInstance()
    private val fromId = FirebaseAuth.getInstance().uid ?: ""
    private var roomId: String? = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_chatmember_menufragment, container, false)
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        initRecyclerView()
        initClick()
    }

    private fun initRecyclerView() {
        activity?.also {
            customAdapter = AddChatMemberRecyclerViewAdapter(it)
        }

        addChatMember_menuFragment_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
        getChatRoomsData()
    }

    private fun getChatRoomsData(){

        arguments?.let {
            roomId = it.getString(In_ChatRoom_Activity.CHATROOMID)
        }
        Log.d("addmember", "roomid :  $roomId ")

        db.collection("ChatRooms").whereEqualTo("roomId", roomId ).get()
            .addOnCompleteListener { task ->
                val result = task.result
                result?.toObjects(ChatRooms::class.java)?.forEach {
                    customAdapter.getChatRoomsData(
                        it.memberSize, roomId, it.userList, it.userNameMap, it.userImageMap)
                }
            }
    }

    private fun initClick() {
        excute_addChatMember_menuFragment_btn.setOnClickListener {
            checkAddMember()
        }
    }


    private fun initData() {
        getAllFriendData()
    }

    private fun getAllFriendData() {
        db.collection("Friends").whereEqualTo("myId", fromId).get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
//                    Log.d("addmember", "getAllFriendData snapshot:  $snapshot ")
                    val friendsData = snapshot.result?.toObjects(Friends::class.java)
                    if (friendsData != null) {
//                        Log.d("addmember", "getAllFriendData friendsData:  $friendsData ")
                        customAdapter.refresh(friendsData)
                    }
                }
            }
    }

    private fun checkAddMember() {
        context?.also {
            MaterialDialog(it).show {
                title(R.string.check_addmember_text)
                positiveButton(R.string.positivebtn_materialdialog) {
                    customAdapter.addMember()
                    showToast(R.string.success)
                    activity?.finish()
                }
                negativeButton(R.string.negativebtn_materialdialog) {
                    return@negativeButton
                }
            }
        }
    }

    private fun showToast(textId: Int) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(position: Int, roomId: String) =
            Add_ChatMember_Fragment().apply {
                arguments = Bundle().apply {
                    putString("CHATROOMID", roomId)
                }
            }
    }
}