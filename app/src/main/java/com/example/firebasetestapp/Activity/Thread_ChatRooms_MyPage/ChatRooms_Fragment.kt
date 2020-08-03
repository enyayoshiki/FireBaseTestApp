package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firebasetestapp.CustomAdapter.ChatRoomsRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chatrooms_fragment.*

class ChatRooms_Fragment : Fragment() {

    private lateinit var customAdapter: ChatRoomsRecyclerViewAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chatrooms_fragment, container, false)
    }

    private fun initialize(){
        initLayout()
        initData()
    }

    private fun initLayout(){
        excute_research_others_imageView.setOnClickListener{
            reserchOthers()
        }
        to_friendData_imageView.setOnClickListener{

//      startActivity() フレンド画面へ移動
        }
    }

    private fun initData(){
        chatRoomsShow(FirebaseAuth.getInstance().uid)
    }

    private fun reserchOthers(){
        val researchName = edit_research_others_editView.text.toString()
        //dbによる名前検索をいれる
    }
    private fun chatRoomsShow(fromId : String?) {
        Log.d("latestmessage", "latestChatLogShow")
        db.collection("LatestMessage:$fromId")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    Log.d("latestmessage", "Listen failed.", e)
                    return@addSnapshotListener
                }
                val chatRooms = snapshot.toObjects(ChatRooms::class.java)
                for (dc in snapshot!!.documentChanges){
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> customAdapter.refresh(chatRooms)
                        DocumentChange.Type.MODIFIED -> customAdapter.refresh(chatRooms)
                    }
                }
            }
    }

    companion object {
        fun newInstance(position: Int): ChatRooms_Fragment {
            return ChatRooms_Fragment()
        }
        }
    }
