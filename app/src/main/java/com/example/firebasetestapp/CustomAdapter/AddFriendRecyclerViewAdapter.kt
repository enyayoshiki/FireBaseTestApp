package com.example.firebasetestapp.CustomAdapter

import android.content.Context
import android.text.style.TtsSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.*
import com.example.firebasetestapp.extention.Visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

class AddFriendRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var myData: User = User()
    private val items = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private var addFriendList = mutableListOf<String>()




    fun refresh(list: List<String>) {

        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getMyData(){
        db.collection("Users").document(FirebaseAuth.getInstance().uid ?: "").get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    myData = task.result?.toObject(User::class.java) ?: return@addOnCompleteListener
                    addFriendList = myData.friendList
                }
            }
    }

    fun addFriend() {

        if (addFriendList.isEmpty()) return

        FirebaseAuth.getInstance().uid?.let {
            db.collection("Users").document(it).update("friendList", addFriendList)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) EMPTYHOLDER else MAINHOLDER
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
           MAINHOLDER -> AddFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.one_result_addfriend, parent, false) as ViewGroup
            )
            else -> EmptyAddFriendItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.emptyholder, parent, false) as ViewGroup
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is AddFriendItemViewHolder -> onBindViewHolder(holder, position)
            is EmptyAddFriendItemViewHolder -> onBindViewHolderEmpty(holder, position)
            else -> return
        }
    }

    private fun onBindViewHolder(holder: AddFriendItemViewHolder, position: Int) {
        val data = items[position]

        holder.apply {
            db.collection("Users").document(data).get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        val result = task.result?.toObject(User::class.java)
                        userName?.text = result?.userName
                        val friendList = result?.friendList ?: mutableListOf()
                        addFriendList.contains(data).let {
                            checkBox?.Visible(!it)
                            doneText?.Visible(it)
                        }
                        val friendImage= result?.userImage ?: ""
                        if (friendImage.isEmpty()) return@addOnCompleteListener else Picasso.get().load(friendImage).into(userImage)
                    } else return@addOnCompleteListener
                }
            checkBox?.setOnClickListener {
                if (checkBox.isChecked) {
                    addFriendList.add(data)
                } else {
                    addFriendList.remove(data)
                }
            }
        }

    }

//        holder.apply {
//            userName?.text = data.userName
//            val otherImage = data.userImage ?: ""
//            if (otherImage.isEmpty()) return else Picasso.get().load(data.userImage).into(userImage as ImageView)
//
//            checkBox?.Visible(!myData.friendList.contains(data.uid))
//            doneText?.Visible(myData.friendList.contains(data.uid))
//
//
//
//
////            db.collection("Friends")
////                .whereEqualTo("myId", fromId)
////                .whereEqualTo("friendId", data.userId)
////                .get()
////                .addOnCompleteListener {
//////                Log.d("addfriend", "addfriend doneResist success:")
////                    val result = it.result?.toObjects(Friends::class.java)
////                    result?.forEach {
//////                    Log.d("addfriend", "addfriend doneResist success: ${it.friendName}")
////                    checkBox?.Visible(!true)
////                    doneText?.Visible(true)
////                }
//


    private fun onBindViewHolderEmpty(holder: EmptyAddFriendItemViewHolder, position: Int) {
        holder.emptyText?.setText(R.string.empty_friend)
    }

    class AddFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val doneText: TextView? = view.findViewById(R.id.done_oneResult_addFriend_textView)
        val checkBox: CheckBox? = view.findViewById(R.id.check_oneResult_addFriend_checkBox)
        val userName: TextView? = view.findViewById(R.id.userName_oneResult_addFriend_textView)
        val userImage: CircleImageView? = view.findViewById(R.id.userImage_oneResult_addFriend_imageView)
    }

    companion object {
        const val EMPTYHOLDER = 0
        const val MAINHOLDER = 1
    }
    class EmptyAddFriendItemViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val emptyText: TextView? = view.findViewById(R.id.empty_text)
    }
}