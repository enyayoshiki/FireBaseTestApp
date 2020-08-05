package com.example.firebasetestapp.CustomAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.firebasetestapp.Activity.ChatLog_Activity
import com.example.firebasetestapp.Activity.In_ChatRoom_Activity
import com.example.firebasetestapp.Activity.LatestMessage_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.MessageToThread
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        InThreadItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.one_result_in_thread_activity, parent, false) as ViewGroup
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InThreadItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: InThreadItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            sendUserName?.text = data.sendUserName
            message?.text = data.message
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            Picasso.get().load(data.sendUserImage).into(sendUserImage as ImageView)
            rootView?.setOnClickListener {
                if (data.sendUserId != FirebaseAuth.getInstance().uid)
                showSendUserInfo(data.sendUserName, data.sendUserId, data.sendUserImage)
                else return@setOnClickListener
            }
//                checkSendMessageDialog()
        }
    }

    private fun showSendUserInfo(userName: String, userId: String, userImage : String) {
//        context?.also {
//            MaterialAlertDialogBuilder(context)
//                .setTitle("User名 : $userName")
//                .setMessage(R.string.sendmessage_materialdialog)
//                .setPositiveButton(R.string.positivebtn_materialdialog) { _, _ ->
//                    val intent = Intent()
//                }
//                .setNegativeButton(R.string.negativebtn_materialdialog) { _, _ ->
//                    return@setNegativeButton
//                }
//        }
        context?.also {
            MaterialDialog(it).show {
                title(null, "User名 : ${userName}\nこの人とチャットしますか？")
                positiveButton(R.string.positivebtn_materialdialog){
                    val intent = Intent(it.context, In_ChatRoom_Activity::class.java)
                    intent.apply {
                        putExtra(In_Thread_Activity.OTHER_ID, userId)
                        putExtra(In_Thread_Activity.OTHER_NAME, userName)
                        putExtra(In_Thread_Activity.OTHER_IMAGE, userImage)
                    }
                    it.context.startActivity(intent)
                }
                negativeButton(R.string.negativebtn_materialdialog){
                    return@negativeButton
                }
            }
        }
    }


    class InThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.oneResult_inThread_rootView)
        val sendUserName: TextView? = view.findViewById(R.id.oneResult_userName_inThread_textView)
        val message: TextView? = view.findViewById(R.id.oneResult_message_inThread_textView)
        val createdAtThread: TextView? = view.findViewById(R.id.oneResult_createdAt_inThread_textView)
        val sendUserImage: CircleImageView? = view.findViewById(R.id.oneResult_userImage_inThread_imageView)
    }
}
