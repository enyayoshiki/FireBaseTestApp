package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
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

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ChatRoomsItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.one_result_chatrooms_fragment, parent, false) as ViewGroup
        )



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatRoomsItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: ChatRoomsItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            otherName?.text = data.otherName
            latestMessage?.text =data.latestMessage
            Picasso.get().load(data.otherImage).into(holder.otherImage as ImageView)
//            holder.rootView?.setOnClickListener {
//            rootView?.setOnClickListener{  //ToDo チャットルームの中へ移動
//                val intent = Intent(context, In_Thread_Activity::class.java)
//                ContextCompat.startActivity(context, intent, null)
//            }
//                checkSendMessageDialog()
        }
    }
}

//    private fun checkSendMessageDialog(){
//        context?.also {
//            MaterialDialog(it).show {
//                title(R.layout.enter_chat_room_name)
//                input(inputType = InputType.TYPE_CLASS_TEXT) { _, text ->
//                    makeRoom("$text")
//                }
//                positiveButton(R.string.ok)
//                negativeButton(R.string.cancel)
//            }
//        }
//    }


class ChatRoomsItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
    val rootView : ConstraintLayout? = view.findViewById(R.id.oneResult_chatRooms_rootView)
    val otherImage : TextView? = view.findViewById(R.id.user_image_oneResult_chatRooms_imageView)
    val otherName: TextView? = view.findViewById(R.id.user_name_oneResult_chatRooms_textView)
    val latestMessage : TextView? = view.findViewById(R.id.latestMessage_oneResult_textView)

}