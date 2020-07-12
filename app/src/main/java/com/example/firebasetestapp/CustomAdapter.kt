package com.example.firebasetestapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.solver.widgets.ConstraintWidget.GONE
import androidx.constraintlayout.solver.widgets.ConstraintWidget.VISIBLE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.scaleMatrix
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.*

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
        if (items.isEmpty())
            return VIEW_TYPE_EMPTY
        return if (FirebaseAuth.getInstance().uid == items[position].fromId)
            VIEW_TYPE_MINE
        else
            VIEW_TYPE_OTHERS
    }

    override fun getItemCount(): Int = if (items.isEmpty()) 1 else items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MINE -> MyMessageViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_message,
                    parent,
                    false
                )
            )
            VIEW_TYPE_OTHERS -> OthersMessageViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_message,
                    parent,
                    false
                )
            )
            else -> EmptyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_message,
                    parent,
                    false
                )
            )
        }
//        if (viewType == VIEW_TYPE_MINE)
//            return MyMessageViewHolder(
//                LayoutInflater.from(context).inflate(
//                    R.layout.chat_message,
//                    parent,
//                    false
//                )
//            )
//        else
//            return OthersMessageViewHolder(
//                LayoutInflater.from(context).inflate(
//                    R.layout.chat_message,
//                    parent,
//                    false
//                )
//            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyMessageViewHolder)
            onBindViewHolder(holder, position)
        else if (holder is OthersMessageViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: MyMessageViewHolder, position: Int) {

    }

    private fun onBindViewHolder(holder: OthersMessageViewHolder, position: Int) {

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
        } else {
            holder.apply {
                YourMessage_RootView?.visibility = View.VISIBLE
                MyMessage_RootView?.visibility = View.INVISIBLE
                YourMessage?.text = data.text
                Log.d("recyclerView", "yourMessage")
            }
        }
    }

    class MyMessageViewHolder(view: View): RecyclerView.ViewHolder(view)
    class OthersMessageViewHolder(view: View): RecyclerView.ViewHolder(view)

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_MINE = 1
        private const val VIEW_TYPE_OTHERS = 2
    }
}

//            Picasso.get().load(data.userImage).into(holder.UserImage)
class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val MyMessage: TextView? = view.findViewById(R.id.chat_my_massage)
    val YourMessage: TextView? = view.findViewById(R.id.chat_your_message)
    val MyMessage_RootView: ConstraintLayout? = view.findViewById(R.id.my_massage_rootView)
    val YourMessage_RootView: ConstraintLayout? = view.findViewById(R.id.your_massage_rootView)

}


//class YourMessageAdapter(private val context: Context?) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val items = mutableListOf<ChatMessageToFireStore>()
//
//    fun refresh(list: List<ChatMessageToFireStore>) {
//        items.apply {
//            clear()
//            addAll(list)
//        }
//        notifyDataSetChanged()
//    }
//    override fun getItemCount(): Int = items.size
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
//        YourMessageItemViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.chat_your_massage,
//                parent,
//                false
//            )
//        )
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is YourMessageItemViewHolder)
//            onBindViewHolder(holder, position)
//    }
//    private fun onBindViewHolder(holder: YourMessageItemViewHolder, position: Int) {
//        val data = items[position]
//        holder.YourMessage.text = data.text
////            Picasso.get().load(data.userImage).into(holder.UserImage)
//    }
//    class YourMessageItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val YourMessage : TextView = view.findViewById(
//            R.id.chat_your_message
//        )
//    }


class HomeUserCustomAdapter(private val context: Context?) :
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
                R.layout.one_result_homeview,
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
//        if (FirebaseAuth.getInstance().uid == data.uid) {
//            items.removeAt(position)
//            data = items[position]
//        }
        if (data.uid == FirebaseAuth.getInstance().uid){
            Log.d("recyclerView","$items")
            holder.rootView.maxHeight = 0
        }

        holder.UserName.text = data.username
        Picasso.get().load(data.userImage).into(holder.UserImage)
        holder.rootView.setOnClickListener {
            val intent = Intent(it.context, ChatLog::class.java)
            intent.apply {
                putExtra(HomeLogin.USER_NAME, data.username)
                putExtra(HomeLogin.USER_KEY, data.uid)
                putExtra(HomeLogin.USER_IMAGE, data.userImage)
            }
            it.context.startActivity(intent)
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView_Home)
        val UserName: TextView = view.findViewById(R.id.homeViewUserName)
        var UserImage: ImageView = view.findViewById(R.id.homeViewImage)
    }
}





