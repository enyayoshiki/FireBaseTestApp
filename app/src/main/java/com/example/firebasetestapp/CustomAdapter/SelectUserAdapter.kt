package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.ChatLog_Activity
import com.example.firebasetestapp.Activity.LatestMessage_Activity
import com.example.firebasetestapp.Activity.SelectUser_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SelectUserAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<User>()

    fun refresh(list: List<User>) {
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
                R.layout.one_result_chooseuser,
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

        if (data.uid == FirebaseAuth.getInstance().uid){
            Log.d("SelectUser_recyclerView", "$items")
            holder.rootView.maxHeight = 0
        }
        holder.UserName.text = data.username
        Picasso.get().load(data.userImage).into(holder.UserImage as ImageView)
        Log.d("SelectUser_recyclerView", "myImage : ${data.userImage}")

        holder.rootView.setOnClickListener {
            val intent = Intent(it.context, ChatLog_Activity::class.java)
            intent.apply {
                putExtra(SelectUser_Activity.USER_NAME, data.username)
                putExtra(SelectUser_Activity.USER_KEY, data.uid)
                putExtra(SelectUser_Activity.USER_IMAGE, data.userImage)
            }
            it.context.startActivity(intent)
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout = view.findViewById(
            R.id.rootView_Home
        )
        val UserName: TextView = view.findViewById(
            R.id.chooseUserName_TextView
        )
        var UserImage: CircleImageView? = view.findViewById(
            R.id.chooseUserImage_ImageView
        )
    }
}