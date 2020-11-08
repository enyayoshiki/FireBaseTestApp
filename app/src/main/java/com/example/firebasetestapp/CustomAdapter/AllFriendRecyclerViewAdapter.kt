package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.Friends
import com.example.firebasetestapp.dataClass.UserFriendList
import com.example.firebasetestapp.extention.Visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AllFriendRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Friends>()

    fun refresh(list: MutableList<Friends>) {

        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) EMPTYHOLDER else MAINHOLDER
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MAINHOLDER -> AllFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.one_result_allfriend, parent, false) as ViewGroup
            )
            else -> EmptyAllFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.emptyholder, parent, false) as ViewGroup
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is AllFriendItemViewHolder -> onBindViewHolder(holder, position)
            is EmptyAllFriendItemViewHolder -> onBindViewHolderEmpty(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: AllFriendItemViewHolder, position: Int) {
        val data = items[position]

        holder.apply {
            friendsName?.text = data.friendName
            Picasso.get().load(data.friendImage).into(friendsImage as ImageView)

            toChatRoom?.setOnClickListener {
                In_ChatRoom_Activity.startFromAllFriend(context, data.friendId, "ALLFRIEND")

            }

            rootView?.setOnClickListener {

            }

        }
    }

    private fun onBindViewHolderEmpty(holder: EmptyAllFriendItemViewHolder, position: Int) {
        holder.emptyText?.setText(R.string.empty_friend)
    }



    class AllFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.rootView_allFriend_oneResult)
        val friendsName: TextView? = view.findViewById(R.id.friendName_allFriend_oneResult)
        val friendsImage: CircleImageView? = view.findViewById(R.id.friendImage_allFriend_oneResult)
        val toChatRoom: ImageButton? = view.findViewById(R.id.toChatRoom_allFriend_oneResult)
    }

    companion object {
        const val EMPTYHOLDER = 0
        const val MAINHOLDER = 1
    }
    class EmptyAllFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }

}