package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.CustomAdapter.ChatRoomsRecyclerViewAdapter
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.ChatRooms
import com.example.firebasetestapp.dataClass.ThreadData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chatrooms_fragment.*
import kotlinx.android.synthetic.main.mainthread_fragment.*

class ChatRooms_Fragment : Fragment() {

    private lateinit var customAdapter: ChatRoomsRecyclerViewAdapter
    private val db = FirebaseFirestore.getInstance()
    private var progressDialog: MaterialDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chatrooms_fragment, container, false)
    }

    private fun initialize(){
        initRecyclerView()
        initLayout()
        initData()
    }

    private fun initLayout(){
        title_chatRooms_textView.text = getString(R.string.chatroom_tab_text)

        excute_research_others_imageView.setOnClickListener{
            reserchOthers(FirebaseAuth.getInstance().uid)
        }
        to_friendData_imageView.setOnClickListener{
            context?.let { context -> AllFriend.start(context) }
        }
    }

    private fun initData(){
        chatRoomsShow(FirebaseAuth.getInstance().uid)
    }

    private fun initRecyclerView() {
        activity?.also {
            customAdapter = ChatRoomsRecyclerViewAdapter(it)
        }
        chatRooms_recyclerView.apply {
            adapter = customAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
//            addOnScrollListener(scrollListener)
    }


    private fun reserchOthers(fromId : String?) {
        showProgress()
        val researchWord = edit_research_others_editView.text.toString()

        if (researchWord.isNotEmpty()) {
            db.collection("ChatRooms").whereArrayContains("userList", fromId!!).get()
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    val chatRoomsData = it.result?.toObjects(ChatRooms::class.java)
                    val fetchData = chatRoomsData?.filter {
                        it.userNameMap.filterValues { researchWord in it }.isNotEmpty()
                    }?.toMutableList()
                    edit_research_others_editView.text.clear()
                    if (fetchData != null) {
                        customAdapter.refresh(fetchData)
                    }else showToast(R.string.please_input_text)
                }
        }
        hideProgress()
    }



    private fun chatRoomsShow(fromId : String?) {
        showProgress()
            db.collection("ChatRooms").whereArrayContains("userList", fromId!!)
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) {
                        Log.d("chatRoomsShow", "chatRoomsShow失敗")

                        return@addSnapshotListener
                    }
                    val chatRooms = snapshot.toObjects(ChatRooms::class.java)
                    for (dc in snapshot!!.documentChanges){
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> customAdapter.refresh(chatRooms)
                            DocumentChange.Type.MODIFIED -> customAdapter.refresh(chatRooms)
                        }
                    }
                }
        hideProgress()
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

        fun newInstance(position: Int): ChatRooms_Fragment {
            return ChatRooms_Fragment()
        }
    }
}
