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
        val researchWord = edit_research_others_editView.text.toString()

        excute_research_others_imageView.setOnClickListener{
            if (researchWord.isEmpty()) return@setOnClickListener
            reserchOthers(FirebaseAuth.getInstance().uid, researchWord)
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


    private fun reserchOthers(fromId : String?, researchWord: String) {
        showProgress()
        val fetchData: MutableList<ChatRooms> = mutableListOf()

        if (researchWord.isNotEmpty()) {
            db.collection("ChatRooms").whereArrayContains("userIdList", fromId!!).get()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) return@addOnCompleteListener

                    val chatRoomsData = task.result?.toObjects(ChatRooms::class.java)
                    chatRoomsData?.forEach {
                        if (it.userNameList.contains(researchWord)) {
                            fetchData.add(it)
                        } else return@forEach
                    }
                    edit_research_others_editView.text.clear()
                    customAdapter.refresh(fetchData)

//                    for ((index, elem) in chatRoomsData.withIndex()){
//                        val otherName = elem.userList[index].userName
//                        if (otherName.contains())
//                    }
//                    val fetchData = chatRoomsData?.forEach {
//                        otherName = it.userList[].userName
//                        it.userNameMap.filterValues { researchWord in it }.isNotEmpty()
//                    }?.toMutableList()
//                    if (fetchData != null) {
//                        customAdapter.refresh(fetchData)
//                    }else showToast(R.string.please_input_text)
//                }
                }
            hideProgress()
        }
    }



    private fun chatRoomsShow(fromId : String?) {
        showProgress()
            db.collection("ChatRooms").whereArrayContains("userIdList", fromId!!)
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) {
                        Log.d("chatRoomsShow", "chatRoomsShow失敗")

                        return@addSnapshotListener
                    }
                    val chatRooms = snapshot.toObjects(ChatRooms::class.java)
                    for (dc in snapshot.documentChanges){
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> customAdapter.refresh(chatRooms)
                            DocumentChange.Type.MODIFIED -> customAdapter.refresh(chatRooms)
                            else -> return@addSnapshotListener
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



    companion object {

        fun newInstance(position: Int): ChatRooms_Fragment {
            return ChatRooms_Fragment()
        }
    }
}
