package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage.ChangeChatRoomsFrontImage
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.User
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

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) EMPTYHOLDER else MAINHOLDER
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            MAINHOLDER -> ChatRoomsItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.one_result_chatrooms_fragment, parent, false) as ViewGroup
            )
            else -> EmptyChatRoomsItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.emptyholder, parent, false) as ViewGroup
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when(holder){
            is ChatRoomsItemViewHolder -> onBindViewHolder(holder, position)
            is EmptyChatRoomsItemViewHolder -> onBindViewHolderEmpty(holder, position)
            else -> return
        }

    }

    private fun onBindViewHolder(holder: ChatRoomsItemViewHolder, position: Int) {
        val data = items[position]
        var frontImageChange: Boolean = (data.frontImage == R.drawable.sample_frontimage.toString())
        holder.apply {
            val otherName =
                data.userNameMap.filterNot { it.key == FirebaseAuth.getInstance().uid }.values.toString()
                    .removePrefix("[").removeSuffix("]")
            otherNameView?.text = otherName

            latestMessageView?.text = data.latestMessage

            rootView?.apply {
                setOnClickListener {
                    In_ChatRoom_Activity.startChatRooms(context, data.roomId, "", otherName, "")
                }
                setOnLongClickListener {
                    changeFrontImage(otherName, data.roomId, data.frontImage)
                    return@setOnLongClickListener it.isLongClickable
                }
            }

            var otherImage: String = ""
            if (data.userList.size == 2 ) data.userList.forEach{
                if (it.uid == FirebaseAuth.getInstance().uid) return@forEach else  otherImage = it.userImage ?: ""
            } else return

            if (otherImage.isEmpty())return else Picasso.get().load(otherImage).into(otherImageView)

//            when(data.memberSize){
//                2 ->
//                    if (frontImageChange){
//                    val otherImage =
//                        data.userImageMap.filterNot { it.key == FirebaseAuth.getInstance().uid }.values.toString()
//                            .removePrefix("[").removeSuffix("]")
////                    Picasso.get().load(otherImage).into(otherImageView)
//                } else{
////                    Picasso.get().load(data.frontImage).into(otherImageView)
//                }
//                else ->
//                    if (frontImageChange){
////                    Picasso.get().load(data.frontImage.toInt()).into(otherImageView)
//                } else{
//                        if (data.frontImage.isEmpty()) return
////                    Picasso.get().load(data.frontImage).into(otherImageView)
//                }
//            }
        }
    }

    private fun onBindViewHolderEmpty(holder: EmptyChatRoomsItemViewHolder, position: Int){
        holder.emptyText?.setText(R.string.empty_chatrooms)
    }

    private fun changeFrontImage(name: String, roomId: String, roomImage: String){
        context?.also {
            MaterialDialog(it).show {
                title(null, "ChatRoom名 : ${name}\nこのルームのアイコンを変更しますか？")
                positiveButton(R.string.positivebtn_materialdialog){
                    ChangeChatRoomsFrontImage.start(context, roomId, roomImage)
                }
                negativeButton(R.string.negativebtn_materialdialog){
                    return@negativeButton
                }
            }
        }
    }

    companion object{
        const val EMPTYHOLDER = 0
        const val MAINHOLDER = 1
    }

    class ChatRoomsItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.oneResult_chatRooms_rootView)
        val otherImageView: ImageView? = view.findViewById(R.id.user_image_oneResult_chatRooms_imageView)
        val otherNameView: TextView? = view.findViewById(R.id.user_name_oneResult_chatRooms_textView)
        val latestMessageView: TextView? = view.findViewById(R.id.latestMessage_oneResult_textView)
    }

    class EmptyChatRoomsItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }

}