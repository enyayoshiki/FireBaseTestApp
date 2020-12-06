package com.example.firebasetestapp.CustomAdapter

import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView

import android.widget.TextView
import androidx.core.view.isVisible

import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms

import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.extention.Visible


import com.example.firebasetestapp.extention.showToast

import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

class AddChatMemberRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private var roomDatainAdapter: ChatRooms = ChatRooms()

    fun refresh(list: List<String>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getRoomData(roomData: ChatRooms) {
        roomDatainAdapter = roomData
    }


    fun addMember() {
        db.collection("ChatRooms").document(roomDatainAdapter.roomId)
            .set(roomDatainAdapter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) showToast(context, R.string.success)
                else showToast(context, R.string.error)
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AddChatMemberItemViewHolder(
            LayoutInflater.from(parent.context)
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
        var friendData = User()

        db.collection("Users").document(data).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendData = task.result?.toObject(User::class.java) ?: User()
                    holder.apply {
                        userName?.text = friendData.userName
                        if (friendData.userImage!!.isNotEmpty()) Picasso.get()
                            .load(friendData.userImage).into(userImage as ImageView)
                    }
                }
            }

        holder.apply {
            checkBox?.Visible(true)
            checkBox?.setOnClickListener {
                if (checkBox.isChecked) {
                    roomDatainAdapter.apply {
                        userList.add(friendData)
                        userIdList.add(data)
                        userNameList.add(friendData.userName)
                        userNameMap.put(data, friendData.userName)
                    }
                } else {
                    roomDatainAdapter.apply {
                        userList.remove(friendData)
                        userIdList.remove(data)
                        userNameList.remove(friendData.userName)
                        userNameMap.remove(data)
                    }
                }
            }
        }
    }

    class AddChatMemberItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox? = view.findViewById(R.id.check_oneResult_addFriend_checkBox)
        val userName: TextView? = view.findViewById(R.id.userName_oneResult_addFriend_textView)
        val userImage: CircleImageView? = view.findViewById(R.id.userImage_oneResult_addFriend_imageView)
    }
}


