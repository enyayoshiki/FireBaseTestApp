package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity.Companion.CHATROOMID
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.CustomAdapter.AddFriendRecyclerViewAdapter
import com.example.firebasetestapp.CustomAdapter.ChatRoomsRecyclerViewAdapter
import com.example.firebasetestapp.CustomAdapter.InChatRoomRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.dataClass.UserFriendList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chatrooms_fragment.*
import kotlinx.android.synthetic.main.activity_in__chat_room_.*
import kotlinx.android.synthetic.main.add_friend_menufragement.*
import java.text.FieldPosition

//private const val CHATROOMID = "roomId"

class Add_Friend_Fragment: Fragment() {

    private lateinit var customAdapter: AddFriendRecyclerViewAdapter

    private val db = FirebaseFirestore.getInstance()
    private var roomId : String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_friend_menufragement, container, false)
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
            customAdapter = AddFriendRecyclerViewAdapter(it)
        }
        addFriend_menuFragment_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
        }
    }

    private fun initClick() {
        excute_addFriend_menuFragment_btn.setOnClickListener {
//            Log.d("addfriend", "addfriend : excute")
            excuteAddFriend()
        }
    }

    private fun initData() {
        getRoomId()
        // ChatRoomsのuserlistからid取得→Usersをget
        getFriendData()
    }

    private fun getRoomId(){
        arguments?.let {
            roomId = it.getString(CHATROOMID)
        }
//        Log.d("addfriend", "roomId : $roomId")
    }

    private fun getFriendData() {
        val friendList = mutableListOf<UserFriendList>()

        db.collection("ChatRooms").whereEqualTo("roomId", roomId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    Log.d("addfriend", "friendData : success")

                    val result = it.result
                    val allUserData = result?.toObjects(ChatRooms::class.java)
                    allUserData?.forEach { data ->
                        for (i in 0 until data.userList.size) {
                            val userId = data.userList[i]
                            if (userId == FirebaseAuth.getInstance().uid) return@forEach

                            val userName = data.userNameMap[userId].toString()
                            val userImage = data.userImageMap[userId].toString()

                            val friendData = UserFriendList(userId, userName, userImage)
                            friendList.add(friendData)
//                            Log.d("addfriend", "friendData : $i , ${friendData}")
                        }
                    }
                }else{
//                    Log.d("addfriend", "friendData : folse")
                }

//                Log.d("addfriend", "friendlist : $friendList")
                customAdapter.refresh(friendList)
            }
    }

    private fun excuteAddFriend() {
        context?.also {
            MaterialDialog(it).show {
                title(R.string.excute_addfriend_materialdialog )
                positiveButton(R.string.positivebtn_materialdialog){
                    customAdapter.addFriend()
                    showToast(R.string.success)
                }
                negativeButton(R.string.negativebtn_materialdialog){
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
            Add_Friend_Fragment().apply {
                arguments = Bundle().apply {
                    putString("CHATROOMID", roomId)
                }
        }
    }
}
