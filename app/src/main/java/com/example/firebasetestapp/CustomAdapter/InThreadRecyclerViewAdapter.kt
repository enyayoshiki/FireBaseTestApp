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
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.MessageToThread
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class InThreadRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<MessageToThread>()

    fun refresh(list: MutableList<MessageToThread>) {

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
        return if(items.isEmpty()) 1 else items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        when(viewType){
            EMPTYHOLDER ->
                return EmptyInThreadItemViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.emptyholder, parent, false) as ViewGroup
                )

            else ->
                return InThreadItemViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.one_result_in_thread_activity, parent, false) as ViewGroup
                )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyInThreadItemViewHolder -> onBindViewHolder(holder, position)
            is InThreadItemViewHolder -> onBindViewHolder(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: InThreadItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            sendUserName?.text = data.sendUserName
            message?.text = data.message
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            if (data.sendUserImage.isNotEmpty())
                Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
            else
                holder.sendUserImage?.setImageDrawable(null)
            rootView?.setOnClickListener {
                if (data.sendUserId != FirebaseAuth.getInstance().uid)
                showSendUserInfo(data.sendUserName, data.sendUserId, data.sendUserImage)
                else return@setOnClickListener
            }
        }
    }

    private fun onBindViewHolder(holder: EmptyInThreadItemViewHolder, position: Int){
        holder.emptyText?.setText(R.string.empty_inthread)
    }

    private fun showSendUserInfo(userName: String, userId: String, userImage: String) {
        context?.also {
            MaterialDialog(it).show {
                title(null, "User名 : ${userName}\nこの人とチャットしますか？")
                positiveButton(R.string.positivebtn_materialdialog){
                    In_ChatRoom_Activity.startChatRooms(context, "", userId, userName, userImage)
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

    class InThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.oneResult_inThread_rootView)
        val sendUserName: TextView? = view.findViewById(R.id.oneResult_userName_inThread_textView)
        val message: TextView? = view.findViewById(R.id.oneResult_message_inThread_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_createdAt_inThread_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_userImage_inThread_imageView)
    }
    class EmptyInThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }
}
