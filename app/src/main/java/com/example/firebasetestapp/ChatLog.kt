package com.example.firebasetestapp

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.PointerIconCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_my_massage.view.*
import kotlinx.android.synthetic.main.chat_room.*
import kotlinx.android.synthetic.main.chat_your_massage.view.*

class ChatLog : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var fromId : String? = ""
    var toId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)

        fromId = FirebaseAuth.getInstance().uid
        toId = intent.getStringExtra(HomeLogin.USER_KEY)

        if (fromId == null || toId == null)return

        val username = intent.getStringExtra(HomeLogin.USER_NAME)
        supportActionBar?.title = username

        chat_log_recyclerview.adapter = adapter

        listemMessage()
        sendMassageBtn.setOnClickListener{
            sendMessage()
        }
    }
    private fun listemMessage(){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage !== null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
//                        val currentUser = HomeLogin.currentUser ?: return
                        Log.d("chat", "Listen_MyChat")
//                        val myImage = currentUser.userImage
//                        if (myImage.isEmpty()) return
                        adapter.add(ChatMyItem(chatMessage.text))
                        Log.d("chat", "Listen_MyChat_Add_RecyclerView")}
                    else{
                        Log.d("chat", "Listen_YourChat")
                        val yourImage = intent.getStringExtra(HomeLogin.USER_IMAGE)
                        adapter.add(ChatFromItem(chatMessage.text))}
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }


    private fun sendMessage(){
        val text = edit_chat_massage.text.toString() ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val chatMessage = ChatMessage(ref.key!!, text, fromId!!, toId!!)
        ref.setValue(chatMessage).addOnSuccessListener {
            Log.d("chat", "Send_MyChat")
        }
    }
    


    class ChatMyItem(val text:String) : Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_my_massage
        }
        override fun bind(viewHolder: ViewHolder, position: Int){
            viewHolder.itemView.chat_my_massage.text = text
//            Picasso.get().load(myImage).into(viewHolder.itemView.chat_my_Image)
        }
    }

    class ChatFromItem(val text:String) : Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_your_massage
        }
        override fun bind(viewHolder: ViewHolder, position: Int){
            viewHolder.itemView.chat_your_message.text = text
//            Picasso.get().load(yourImage).into(viewHolder.itemView.chat_your_Image)
        }
    }
}