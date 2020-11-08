package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.CustomAdapter.MainThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.InChatRoom
import com.example.firebasetestapp.dataClass.ThreadData
import com.example.firebasetestapp.extention.Visible
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chatrooms_fragment.*
import kotlinx.android.synthetic.main.activity_in__chat_room_.*
import kotlinx.android.synthetic.main.activity_in_thread_.*
import kotlinx.android.synthetic.main.activity_in_thread_.in_thread_recyclerView
import kotlinx.android.synthetic.main.activity_main_thread_.*
import kotlinx.android.synthetic.main.mainthread_fragment.*
import kotlinx.android.synthetic.main.mainthread_fragment.mainThead_recyclerView_fragment

class Thread_Fragment: Fragment() {

    private lateinit var customAdapter: MainThreadRecyclerViewAdapter
    private var progressDialog: MaterialDialog? = null
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
        initRecyclerView()
        initLayout()
        initData()
    }

    private fun initLayout() {
        title_mainThread_textView.text = getString(R.string.thead_tab_text)

        excute_research_thread_imageView.setOnClickListener {
            Log.d("thread", "research Thread")
            researchThread()
        }


        excute_creat_thread_imageView.setOnClickListener {
            Log.d("thread", "create Thread")
            creatThread()

        }

        all_threadData_ImageView.setOnClickListener{
            showToast(R.string.allthread_text)
            research_thread_editView.text.clear()
            create_thread_editView.text.clear()
            initData()
        }
    }

    private fun initData() {
        showProgress()
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener{ snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED ->
                            customAdapter.refresh(snapshots.toObjects(ThreadData::class.java))

                        DocumentChange.Type.MODIFIED ->
                            customAdapter.refresh(snapshots.toObjects(ThreadData::class.java))

                        DocumentChange.Type.REMOVED ->
                            customAdapter.refresh(snapshots.toObjects(ThreadData::class.java))
                    }
                }
            }
        hideProgress()
    }

        private fun initRecyclerView() {
            activity?.also {
                customAdapter = MainThreadRecyclerViewAdapter(it)
            }
            mainThead_recyclerView_fragment.apply {
                adapter = customAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
            }
        }

    private fun researchThread() {
        val researchWord = research_thread_editView.text.toString()

        if (researchWord.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("rooms")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    val allThreadData = it.result?.toObjects(ThreadData::class.java)
                    val fetchData = allThreadData?.filter { it.name.contains(researchWord) }?.toMutableList()

                    research_thread_editView.text.clear()

                    if (fetchData != null) {

                        customAdapter.refresh(fetchData)
                    }
                }
        }else showToast(R.string.please_input_text)
    }

        private fun creatThread() {
            val threadName = create_thread_editView.text.toString()
            if (threadName.isNotEmpty()) {
                db.collection("rooms").add(ThreadData().apply {
                    name = threadName
                })
                    .addOnCompleteListener {
                        create_thread_editView.text.clear()
                        showToast(R.string.success_createthread_text)
                        initData()
                    }
            }else showToast(R.string.please_input_text)
        }

    private fun showProgress() {
        hideProgress()
        progressDialog = context?.let {
            MaterialDialog(it).apply {
                cancelable(false)
                setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false))
                show()
            }
        }

    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun showToast(textId: Int) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
    }


    companion object {
            fun newInstance(position: Int): Thread_Fragment {
                return Thread_Fragment()
            }
        }
    }
