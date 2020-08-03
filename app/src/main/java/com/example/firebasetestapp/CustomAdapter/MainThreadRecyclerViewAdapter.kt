package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ThreadData

class MainThreadRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ThreadData>()

    fun refresh(list: MutableList<ThreadData>) {

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
        MainThreadItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.one_result_mainthread, parent, false) as ViewGroup
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainThreadItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: MainThreadItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            threadName?.text = data.name
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            rootView?.setOnClickListener {
                val intent = Intent(context, In_Thread_Activity::class.java)
                intent.putExtra(Thread_Fragment.THREAD_ID, data.roomId)
                intent.putExtra(Thread_Fragment.THREAD_NAME, data.name)
                context.startActivity(intent)
            }
//                checkSendMessageDialog()
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


    class MainThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.one_result_rootView)
        val threadName: TextView? = view.findViewById(R.id.one_result_threadName_mainThread)
        val createdAtThread: TextView? =
            view.findViewById(R.id.one_result_createdAtThread_mainThread)

    }

}
