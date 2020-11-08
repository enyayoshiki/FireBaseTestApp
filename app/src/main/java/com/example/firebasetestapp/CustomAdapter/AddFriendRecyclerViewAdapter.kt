package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.graphics.Insets.add
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment.Add_Friend_Fragment
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.*
import com.example.firebasetestapp.extention.Visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_friend_menufragement.*

class AddFriendRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<UserFriendList>()
    private val db = FirebaseFirestore.getInstance()
    private val fromId = FirebaseAuth.getInstance().uid ?: ""
    private val addFriendList = mutableListOf<UserFriendList>()


    fun refresh(list: MutableList<UserFriendList>) {

        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addFriend(){
//        Log.d("addfriend", "addfriend infunction: ")

        if (addFriendList.isEmpty()) return
//        Log.d("addfriend", "addfriend infunction: $addFriendList")

        for (i in addFriendList){
//            Log.d("addfriend", "addfriend : $addFriendList")
            db.collection("Friends")
                .add(Friends().apply {
                    myId = fromId
                    friendId = i.userId
                    friendName = i.userName
                    friendImage = i.userImage
                })
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) EMPTYHOLDER else MAINHOLDER
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
           MAINHOLDER -> AddFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.one_result_addfriend, parent, false) as ViewGroup
            )
            else -> EmptyAddFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.emptyholder, parent, false) as ViewGroup
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is AddFriendItemViewHolder -> onBindViewHolder(holder, position)
            is EmptyAddFriendItemViewHolder -> onBindViewHolderEmpty(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: AddFriendItemViewHolder, position: Int) {
        val data = items[position]

        holder.apply {
            userName?.text = data.userName
            Picasso.get().load(data.userImage).into(userImage as ImageView)

            db.collection("Friends")
                .whereEqualTo("myId", fromId)
                .whereEqualTo("friendId", data.userId)
                .get()
                .addOnCompleteListener {
//                Log.d("addfriend", "addfriend doneResist success:")
                    val result = it.result?.toObjects(Friends::class.java)
                    result?.forEach {
//                    Log.d("addfriend", "addfriend doneResist success: ${it.friendName}")
                    checkBox?.Visible(!true)
                    doneText?.Visible(true)
                }

            checkBox?.setOnClickListener {
                if (checkBox.isChecked) {
                    addFriendList.add(data)
//                    Log.d("addfriend", "addfriend addData ischeck: $addFriendList")
                } else {
                    addFriendList.remove(data)
//                    Log.d("addfriend", "addfriend addData false: $addFriendList")
                }
            }
        }
    }
}
    private fun onBindViewHolderEmpty(holder: EmptyAddFriendItemViewHolder, position: Int) {
        holder.emptyText?.setText(R.string.empty_friend)
    }

    class AddFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val doneText: TextView? = view.findViewById(R.id.done_oneResult_addFriend_textView)
        val checkBox: CheckBox? = view.findViewById(R.id.check_oneResult_addFriend_checkBox)
        val userName: TextView? = view.findViewById(R.id.userName_oneResult_addFriend_textView)
        val userImage: CircleImageView? = view.findViewById(R.id.userImage_oneResult_addFriend_imageView)
    }

    companion object {
        const val EMPTYHOLDER = 0
        const val MAINHOLDER = 1
    }
    class EmptyAddFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }
}