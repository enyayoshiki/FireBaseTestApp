package com.example.firebasetestapp.Activity.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.CustomAdapter.MainThreadRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ThreadData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.mainthread_fragment.*

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

        all_threadData_ImageView.setOnClickListener{
            showToast(R.string.allthread_text)
            edit_Research_TextView.text.clear()
            edit_create_thread_TextView.text.clear()
            initData()
        }

        initRecyclerView()
    }

    private fun initData() {
        showProgress()
        FirebaseFirestore.getInstance()
            .collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    return@addOnCompleteListener
                it.result?.toObjects(ThreadData::class.java)?.also { chatRooms ->
                    customAdapter.refresh(chatRooms)
                }
                hideProgress()
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
        val researchWord = edit_Research_TextView.text.toString()

        if (researchWord.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("rooms")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener


                    val allThreadData = it.result?.toObjects(ThreadData::class.java)
                    val fetchData =
                        allThreadData?.filter { it.name.contains(researchWord) }?.toMutableList()
                    edit_Research_TextView.text.clear()
//                    allThreadData?.forEach {
//                        it.name.contains(researchWord)
//                        val threadData =mutableListOf<ThreadData>()
//                        threadData.add(it)
                    if (fetchData != null) {
                        customAdapter.refresh(fetchData)
                    }
                }
        }else showToast(R.string.please_input_text)
    }

        private fun creatThread() {
            val threadName = edit_create_thread_TextView.text.toString()
            if (threadName.isNotEmpty()) {
                db.collection("rooms").add(ThreadData().apply {
                    name = threadName
                    edit_create_thread_TextView.text.clear()
                    showToast(R.string.success_createthread_text)
                })
                    .addOnCompleteListener {
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
