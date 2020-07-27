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

    override fun getItemViewType(position: Int): Int {
        val data = items[position]
        return if (data.fromId == FirebaseAuth.getInstance().uid) {
            VIEWTYPE_MINE
        } else
            VIEWTYPE__OTHERS
        }

    override fun getItemCount(): Int = items.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEWTYPE_MINE -> MyItemViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_message_mine, parent, false))

            VIEWTYPE__OTHERS -> OthersItemViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_message_others, parent, false))

            else -> EmptyItemViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_message_empty, parent, false))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is MyItemViewHolder -> onBindViewHolder(holder, position)
            is OthersItemViewHolder -> onBindViewHolder(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        val data = items[position]
        holder.MyMessage?.text = data.text
        Picasso.get().load(data.myImage).into(holder.MyImage)
    }

    private fun onBindViewHolder(holder: OthersItemViewHolder, position: Int) {
        val data = items[position]

            holder.YourMessage?.text = data.text
            Picasso.get().load(data.myImage).into(holder.YourImage)
        }

    private fun onBindViewHolder(holder: EmptyItemViewHolder, position: Int) {

    }

    companion object {
        const val VIEWTYPE_MINE = 1000
        const val VIEWTYPE__OTHERS = 1001
        const val VIEWTYPE_EMPTY = 0
    }


    class MyItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val MyMessage: TextView? = view.findViewById(R.id.chat_my_massage)
        val MyImage: CircleImageView? = view.findViewById(R.id.chat_my_Image)
    }

    class OthersItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val YourMessage: TextView? = view.findViewById(R.id.chat_your_message)
        val YourImage: CircleImageView? = view.findViewById(R.id.chat_your_Image)
    }

    class EmptyItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val EmptyMessage: TextView? = view.findViewById(R.id.chat_message_empty)
    }
}
