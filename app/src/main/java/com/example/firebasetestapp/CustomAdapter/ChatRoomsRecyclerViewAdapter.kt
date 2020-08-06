package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.WhereFrom
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChatRoomsRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ChatRooms>()

    fun refresh(list: MutableList<ChatRooms>) {

        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ChatRoomsItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.one_result_chatrooms_fragment, parent, false) as ViewGroup
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatRoomsItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: ChatRoomsItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            val otherImage = data.userImageMap.filterNot { it.key == FirebaseAuth.getInstance().uid }.values.toString().removePrefix("[").removeSuffix("]")
            Log.d("chatrooms", "image : $otherImage")
            Picasso.get().load(otherImage).into(holder.otherImageView as ImageView)

            val otherName = data.userNameMap.filterNot { it.key == FirebaseAuth.getInstance().uid }.values.toString().removePrefix("[").removeSuffix("]")
            Log.d("chatrooms", "name : $otherName")
            otherNameView?.text = otherName

            latestMessageView?.text = data.latestMessage

            holder.rootView?.setOnClickListener {
                val otherId = data.userList.filterNot { it == FirebaseAuth.getInstance().uid }.toString().removePrefix("[").removeSuffix("]")
                Log.d("chatrooms", "id : $otherId")
                Log.d("chatrooms", "fromid : ${data.userList.filter { it == otherId }}}")
                rootView?.setOnClickListener {  //ToDo チャットルームの中へ移動
                    WhereFrom().whereFrom = "1"
                    val intent = Intent(context, In_ChatRoom_Activity::class.java)
                    intent.apply {
                        putExtra(In_Thread_Activity.OTHER_ID, otherId)
                        putExtra(In_Thread_Activity.OTHER_NAME, otherName)
                        putExtra(In_Thread_Activity.OTHER_IMAGE, otherImage)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }


    class ChatRoomsItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.oneResult_chatRooms_rootView)
        val otherImageView: ImageView? = view.findViewById(R.id.user_image_oneResult_chatRooms_imageView)
        val otherNameView: TextView? = view.findViewById(R.id.user_name_oneResult_chatRooms_textView)
        val latestMessageView: TextView? = view.findViewById(R.id.latestMessage_oneResult_textView)

    }
}