package com.example.firebasetestapp.Activity.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasetestapp.CustomAdapter.MainThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ThreadData
import com.example.firebasetestapp.dataClass.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.mainthread_fragment.*

class Thread_Fragment: Fragment() {

    private lateinit var customAdapter: MainThreadRecyclerViewAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mainthread_fragment, container, false)
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        excute_research_ImageView.setOnClickListener {
            researchThread()

        }

        excute_creat_thread_ImageView.setOnClickListener {
            creatThread()
        }
        initRecyclerView()
    }

    private fun initData() {
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .get()
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    return@addOnCompleteListener
                it.result?.toObjects(ThreadData::class.java)?.also { chatRooms ->
                    customAdapter.refresh(chatRooms)
                }

            }
    }

        private fun initRecyclerView() {
            activity?.also {
                customAdapter = MainThreadRecyclerViewAdapter(it)
            }
            mainThead_RecyclerView_Fragment.apply {
                adapter = customAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
//            addOnScrollListener(scrollListener)
            }
        }

        private fun researchThread() {

        }

        private fun creatThread() {
            val threadName = edit_create_thread_TextView.text.toString()
            db.collection("rooms").add(ThreadData().apply {
                name = threadName
            })
                .addOnCompleteListener {
                    initData()
                }
        }

        companion object {
            fun newInstance(position: Int): Thread_Fragment {
                return Thread_Fragment()
            }
        }
    }
}