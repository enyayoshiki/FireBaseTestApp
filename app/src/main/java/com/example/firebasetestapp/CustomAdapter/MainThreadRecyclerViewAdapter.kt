package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.text.Layout
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.Activity.Login_Resister_PassChange.Resister_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.In_Thread_Activity
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

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty())  EMPTYHOLDER else MAINHOLDER

    }

    override fun getItemCount(): Int {
        return if(items.isEmpty()) 1 else items.size

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            EMPTYHOLDER ->
                return EmptyThreadItemViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.emptyholder, parent, false) as ViewGroup
                )

            else ->
                return MainThreadItemViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.one_result_mainthread, parent, false) as ViewGroup
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is EmptyThreadItemViewHolder -> onBindViewHolder(holder, position)
            is MainThreadItemViewHolder-> onBindViewHolder(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: MainThreadItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            threadName?.text = data.name
            createdAtThread?.text = DateFormat.format("yyyy/MM/dd hh:mm:ss", data.createdAt)
            rootView?.setOnClickListener {
                In_Thread_Activity.start(context, data.roomId, data.name)
            }
//                checkSendMessageDialog()
        }
    }

    private fun onBindViewHolder(holder: EmptyThreadItemViewHolder, position: Int){
        holder.emptyText?.setText(R.string.empty_thread)
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

    companion object{
        const val EMPTYHOLDER = 0
        const val MAINHOLDER = 1
    }

    class MainThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout? = view.findViewById(R.id.one_result_rootView)
        val threadName: TextView? = view.findViewById(R.id.one_result_threadName_mainThread)
        val createdAtThread: TextView? =
            view.findViewById(R.id.one_result_createdAtThread_mainThread)
    }

    class EmptyThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }
}
