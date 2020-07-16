package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatMessageToFireStore
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ChatMessageToFireStore>()

    fun refresh(list: List<ChatMessageToFireStore>) {
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
                R.layout.chat_message,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = items[position]

        if (FirebaseAuth.getInstance().uid == data.fromId) {
            holder.apply {
                YourMessage_RootView?.visibility = View.INVISIBLE
                MyMessage_RootView?.visibility = View.VISIBLE
                MyMessage?.text = data.text
                Log.d("recyclerView", "MyMessage")
                Log.d("recyclerView", "fromId : ${data.fromId}")
                Log.d("recyclerView", "toId : ${data.toId}")
            }
            Picasso.get().load(R.drawable.ic_settings).into(holder.MyImage)
            Log.d("recyclerView", "myImage : ${data.myImage}")
        } else {
            holder.apply {
                YourMessage_RootView?.visibility = View.VISIBLE
                MyMessage_RootView?.visibility = View.INVISIBLE
                YourMessage?.text = data.text
                Log.d("recyclerView", "yourMessage")
            }

//            holder.YourImage?.setImageResource(R.drawable.ic_settings)
            Picasso.get().load(R.drawable.ic_settings).into(holder.YourImage)
            Log.d("recyclerView", "myImage(your) : ${data.myImage}")
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val MyMessage: TextView? = view.findViewById(R.id.chat_my_massage)
        val MyImage : CircleImageView? = view.findViewById(R.id.chat_my_Image)
        val YourMessage: TextView? = view.findViewById(R.id.chat_your_message)
        val YourImage : CircleImageView? = view.findViewById(R.id.chat_your_Image)
        val MyMessage_RootView: ConstraintLayout? = view.findViewById(R.id.my_massage_rootView)
        val YourMessage_RootView: ConstraintLayout? = view.findViewById(R.id.your_massage_rootView)
    }
}