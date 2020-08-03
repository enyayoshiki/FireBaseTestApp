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
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].sendUserId == FirebaseAuth.getInstance().uid){
            MyMessageViewType
        }else
            OtherMessageViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MyMessageViewType -> myMessageItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.one_result_in_chatroom_mine, parent, false) as ViewGroup)
            else -> otherMessageItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.one_result_in_chatroom_other, parent, false) as ViewGroup)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is myMessageItemViewHolder -> onBindViewHolderMine(holder, position)
            is otherMessageItemViewHolder -> onBindViewHolderOther(holder , position)
            else -> return
        }
    }

    private fun onBindViewHolderMine(holder: myMessageItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            message?.text = data.sendMessage
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
//                checkSendMessageDialog()
        }
    }

    private fun onBindViewHolderOther(holder: otherMessageItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            message?.text = data.sendMessage
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
//                checkSendMessageDialog()
        }
    }

//    private fun showSendUserInfo(userName: String, userId: String) {
////        context?.also {
////            MaterialAlertDialogBuilder(context)
////                .setTitle("User名 : $userName")
////                .setMessage(R.string.sendmessage_materialdialog)
////                .setPositiveButton(R.string.positivebtn_materialdialog) { _, _ ->
////                    val intent = Intent()
////                }
////                .setNegativeButton(R.string.negativebtn_materialdialog) { _, _ ->
////                    return@setNegativeButton
////                }
////        }
//        context?.also {
//            MaterialDialog(it).show {
//                title(null, "User名 : ${userName}\nこの人とチャットしますか？")
//                positiveButton(R.string.positivebtn_materialdialog){
//                    val intent = Intent()
//                }
//                negativeButton(R.string.negativebtn_materialdialog){
//                    return@negativeButton
//                }
//            }
//        }
//    }

    companion object{
        const val MyMessageViewType = 100
        const val OtherMessageViewType = 200
    }


    class myMessageItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val message: TextView? = view.findViewById(R.id.oneResult_mineMessage_inChatRoom_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_mineCreatedAt_inChatRoom_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_mineImage_inChatRoom_imageView)
    }

    class otherMessageItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val message: TextView? = view.findViewById(R.id.oneResult_otherMessage_inChatRoom_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_otherCreatedAt_inChatRoom_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_otherImage_inChatRoom_imageView)
    }

}
