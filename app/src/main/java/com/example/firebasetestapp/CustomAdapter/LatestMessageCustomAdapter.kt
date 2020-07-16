package com.example.firebasetestapp.CustomAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.ChatLog_Activity
import com.example.firebasetestapp.Activity.LatestMessage_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.LatestMessage

class LatestMessageCustomAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<LatestMessage>()

    fun refresh(list: List<LatestMessage>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.one_result_leatestmessage,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var data = items[position]
        holder.latestMessageText.text = data.text
        holder.toUserName.text = data.yourName
//        Picasso.get().load(data.userImage).into(holder.UserImage)
        holder.rootView.setOnClickListener {
            val intent = Intent(it.context, ChatLog_Activity::class.java)
            intent.apply {
                putExtra(LatestMessage_Activity.USER_NAME, data.yourName)
                putExtra(LatestMessage_Activity.USER_KEY, data.yourId)
//                putExtra(LatestMessage_Activity.USER_IMAGE, data.userImage)
            }
            it.context.startActivity(intent)
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout = view.findViewById(
            R.id.one_result_homechatroom
        )
        val toUserName: TextView = view.findViewById(
            R.id.latest_message_userName_textView
        )
        var latestMessageText: TextView = view.findViewById(
            R.id.latest_message_chatLog_textView
        )
    }
}