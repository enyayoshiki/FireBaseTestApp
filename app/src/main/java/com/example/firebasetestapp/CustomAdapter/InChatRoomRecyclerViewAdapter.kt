package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.InChatRoom
import com.example.firebasetestapp.dataClass.MessageToThread
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class InChatRoomRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<InChatRoom>()

    fun refresh(list: MutableList<InChatRoom>) {

        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return if(items.isEmpty()) 1 else items.size
    }

    override fun getItemViewType(position: Int): Int {
        if (items.isEmpty())
            return EMPTYHOLDER

        return if (items[position].sendUserId == FirebaseAuth.getInstance().uid){
            MyMessageViewType
        }else
            OtherMessageViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MyMessageViewType -> MyMessageItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.one_result_in_chatroom_mine, parent, false) as ViewGroup)

            OtherMessageViewType -> OtherMessageItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.one_result_in_chatroom_other, parent, false) as ViewGroup)

            else -> EmptyInChatRoomItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.emptyholder, parent, false) as ViewGroup )

        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MyMessageItemViewHolder -> onBindViewHolderMine(holder, position)
            is OtherMessageItemViewHolder -> onBindViewHolderOther(holder , position)
            is EmptyInChatRoomItemViewHolder -> onBindViewHolderEmpty(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolderMine(holder: MyMessageItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            message?.text = data.sendMessage
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            sendUserName?.text = data.sendUserName
            Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
//                checkSendMessageDialog()
        }
    }

    private fun onBindViewHolderOther(holder: OtherMessageItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            message?.text = data.sendMessage
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            sendUserName?.text = data.sendUserName
            Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
        }
    }

    private fun onBindViewHolderEmpty(holder: EmptyInChatRoomItemViewHolder, position: Int){
        holder.emptyText?.setText(R.string.empty_inchatroom)
    }

    companion object {
        const val MyMessageViewType = 100
        const val OtherMessageViewType = 200
        const val EMPTYHOLDER = 0
    }


    class MyMessageItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val message: TextView? = view.findViewById(R.id.oneResult_mineMessage_inChatRoom_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_mineCreatedAt_inChatRoom_textView)
        val sendUserName : TextView? = view.findViewById(R.id.oneResult_mineName_inChatRoom_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_mineImage_inChatRoom_imageView)
    }

    class OtherMessageItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val message: TextView? = view.findViewById(R.id.oneResult_otherMessage_inChatRoom_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_otherCreatedAt_inChatRoom_textView)
        val sendUserName : TextView? = view.findViewById(R.id.oneResult_otherName_inChatRoom_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_otherImage_inChatRoom_imageView)
    }
    class EmptyInChatRoomItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }
}
