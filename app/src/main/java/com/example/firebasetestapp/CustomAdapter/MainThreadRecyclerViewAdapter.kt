package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ThreadData

class MainThreadRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ThreadData>()

    fun refresh(list: List<ThreadData>) {
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
            LayoutInflater.from(context).inflate(R.layout.one_result_mainthread, parent, false) as ViewGroup
        )



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainThreadItemViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: MainThreadItemViewHolder, position: Int) {
        val data = items[position]
        holder.threadName?.text = data.name

    }

    class MainThreadItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val threadName : TextView? = view.findViewById(R.id.one_result_threadName_mainThread)

    }

}