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
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.Friends
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.extention.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_chatmember_menufragment.*
import timber.log.Timber

class Add_ChatMember_Fragment: Fragment() {

    private lateinit var customAdapter: AddChatMemberRecyclerViewAdapter
    private val db = FirebaseFirestore.getInstance()
    private val fromId = FirebaseAuth.getInstance().uid ?: ""
    private var roomId: String? = ""
    private var idList = mutableListOf<String>()


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
        Timber.i("Add_ChatMember")
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
    }


    private fun initClick() {
        excute_addChatMember_menuFragment_btn.setOnClickListener {
            checkAddMember()
        }
    }


    private fun initData() {
        Timber.i("initData")

        arguments?.let {
            roomId = it.getString(In_ChatRoom_Activity.CHATROOMID)
        }
        getRoomsIdList(roomId)
    }

    private fun getRoomsIdList(roomId: String?){
        Timber.i("getRoomsIdList")

        db.collection("ChatRooms").document(roomId ?: "").get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val chatRoomData = result?.toObject(ChatRooms::class.java) ?: return@addOnCompleteListener
                        customAdapter.getRoomData(chatRoomData)
                        idList = chatRoomData.userIdList
                } else return@addOnCompleteListener
                getFriendData()
            }
    }

    private fun getFriendData() {
        db.collection("Users").document(fromId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userData = task.result?.toObject(User::class.java)
                    val allFriendList = userData?.friendList
                    if (allFriendList is MutableList<String>) {
                        val result = allFriendList.removeAll(idList)
                        Timber.i("allFriendList:"+allFriendList)
                        customAdapter.refresh(allFriendList)
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
                    showToast(context, R.string.success)
                    activity?.finish()
                }
                negativeButton(R.string.negativebtn_materialdialog) {
                    return@negativeButton
                }
            }
        }
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