package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.Friends
import com.example.firebasetestapp.dataClass.UserFriendList
import com.example.firebasetestapp.extention.Visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AddChatMemberRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Friends>()
    private val db = FirebaseFirestore.getInstance()
    private var addSize: Int = 0
    private var addFriendId = ""
    private var getRoomId: String? = ""
    private val addMemberList = mutableListOf<Friends>()
    private var chatRoomsUserList = mutableListOf<String>()
    private var chatRoomsNameMap = mutableMapOf<String, String>()
    private var chatRoomsImageMap = mutableMapOf<String, String>()

    fun refresh(list: MutableList<Friends>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getChatRoomsData(
        size: Int,
        roomId: String?,
        userList: MutableList<String>,
        nameMap: MutableMap<String, String>,
        imageMap: MutableMap<String, String>
    ) {
        addSize = size
        getRoomId = roomId
        chatRoomsUserList = userList
        chatRoomsNameMap = nameMap
        chatRoomsImageMap = imageMap
        Log.d("addmember", "getChatRoomsData : $chatRoomsUserList : $chatRoomsNameMap")
    }

    fun addMember() {
        //        Log.d("addfriend", "addfriend infunction: ")
        if (addMemberList.isEmpty()) return
//        Log.d("addfriend", "addfriend infunction: $addFriendList")

        for (i in 0 until addMemberList.size) {
            addSize++
            addFriendId = addMemberList[i].friendId

            chatRoomsUserList.add(addFriendId)
            Log.d("addmember", "userList : $chatRoomsUserList ")

            chatRoomsNameMap.put(addFriendId, addMemberList[i].friendName)
            Log.d("addmember", "nameMap : $chatRoomsNameMap ")

            chatRoomsImageMap.put(addFriendId, addMemberList[i].friendImage)

            getRoomId?.let {
                db.collection("ChatRooms").document(it).set(ChatRooms().apply {
                    memberSize = addSize
                    latestMessage = ""
                    roomId = it
                    userList = chatRoomsUserList
                    userNameMap = chatRoomsNameMap
                    userImageMap = chatRoomsImageMap
                    frontImage = R.drawable.sample_frontimage.toString()
                })
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AddChatMemberItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.one_result_addfriend, parent, false) as ViewGroup
        )


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddChatMemberItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: AddChatMemberItemViewHolder, position: Int) {
        val data = items[position]

        holder.apply {
            userName?.text = data.friendName
            Picasso.get().load(data.friendImage).into(userImage as ImageView)

            if (chatRoomsUserList.contains(data.friendId)) {
                checkBox?.Visible(false)
                doneText?.Visible(true)
            }else{
                checkBox?.Visible(true)
                doneText?.Visible(false)
            }

            checkBox?.setOnClickListener {
                if (checkBox.isChecked) {
                    addMemberList.add(data)
                    Log.d("addmember", "addmember addData ischeck: $addMemberList")
//                    Log.d("addmember", "getroomid :  $getRoomId ")
//                    Log.d("addmember", "addmember List[0]: ${addMemberList[0].friendId}")

                } else {
                    addMemberList.remove(data)
//                    Log.d("addmember", "addmember addData false: $addMemberList")
                }
            }
        }
    }

    class AddChatMemberItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val doneText: TextView? = view.findViewById(R.id.done_oneResult_addFriend_textView)
        val checkBox: CheckBox? = view.findViewById(R.id.check_oneResult_addFriend_checkBox)
        val userName: TextView? = view.findViewById(R.id.userName_oneResult_addFriend_textView)
        val userImage: CircleImageView? = view.findViewById(R.id.userImage_oneResult_addFriend_imageView)
    }
}
