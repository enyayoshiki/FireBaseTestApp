package com.example.firebasetestapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home_login.*
import kotlinx.android.synthetic.main.chat_room.*
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*

class ChatLog : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val customAdapter by lazy { MessageAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)


        val username = intent.getStringExtra(HomeLogin.USER_NAME)
        supportActionBar?.title = username

        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra(HomeLogin.USER_KEY)
        Log.d("chat", "fromId : $fromId")
        Log.d("chat", "toId : $toId")




        listemMessage(fromId, toId)
        sendMassageBtn.setOnClickListener {
//            sendMessage()
            sendMessageToFireStore(fromId, toId)
        }
    }

    private fun listemMessage(fromId: String?, toId: String?) {
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//
//        ref.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val chatMessage = snapshot.getValue(ChatMessage::class.java)
//                if (chatMessage !== null) {
//                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
//                        adapter.add(ChatMyItem(chatMessage.text))
//                        Log.d("chat", "Listen_MyChat_Add_RecyclerView")
//                    } else {
//                        adapter.add(ChatFromItem(chatMessage.text))
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//        })

        db.collection("ChatLogMessage").document("$fromId").collection("$toId")
//            .whereEqualTo("fromId", "$fromId").whereEqualTo("toId", "$toId")
            .orderBy("time", Query.Direction.ASCENDING)
            .get().addOnSuccessListener {
                Log.d("chat", "ListenMessage")

                chat_log_recyclerview.apply {
                    adapter = customAdapter
                    setHasFixedSize(true)
                }
                val chatMessage = it.toObjects(ChatMessageToFireStore::class.java)
                Log.d("chat", "chatId(from/to) : ${fromId}/${toId}")
                Log.d("chat", "chatId(to/from) : ${toId}/${fromId}")


                customAdapter.refresh(chatMessage)
//                }else{
//                    if (checkIdList.contains("${toId}/${fromId}")){
//                        Log.d("chat", "Listen_YOUR_Message")
//                        chat_log_recyclerview.apply {
//                            adapter =
//                            setHasFixedSize(true)
//                        }
//                        myMessageCustomAdapter.refresh(chatMessage)
//                    }
//
//                }
//            }.addOnFailureListener {
//                Log.d("error", "${it}:MyMessage")
//            }

//        db.collection("ChatLogMessage").whereEqualTo("fromId", "$toId")
//            .whereEqualTo("toId", "$fromId").orderBy("time", Query.Direction.ASCENDING).get()
//            .addOnSuccessListener {
//                val chatMessage = it.toObjects(ChatMessageToFireStore::class.java)
//                if (chatMessage !== null) {
//                    chat_log_recyclerview.apply {
//                        adapter = yourMessageCustomAdapter
//                        setHasFixedSize(true)
//                    }
//                    myMessageCustomAdapter.refresh(chatMessage)
//                }
//            }.addOnFailureListener {
//                Log.d("chat", "$it")
//                return@addOnFailureListener
//            }
            }
//    private fun sendMessage(){
//        val text = edit_chat_massage.text.toString() ?: return
//        val fromId = FirebaseAuth.getInstance().uid
//        val toId = intent.getStringExtra(HomeLogin.USER_KEY)
//
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
//        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
//        val chatMessage = ChatMessage(ref.key!!, text, fromId!!, toId!!)
//
//        ref.setValue(chatMessage).addOnSuccessListener {
//            Log.d("chat", "Send_MyChat")
//        }
//        toRef.setValue(chatMessage).addOnSuccessListener{
//            Log.d("chat", "To_Send_MyChat")
//        }
    }

    private fun sendMessageToFireStore(fromId: String?, toId: String?) {
        val text = edit_chat_massage.text.toString()
        val time: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val chatMessage = ChatMessageToFireStore(text, fromId!!, toId!!, time)
        db.collection("ChatLogMessage").document("$fromId").collection("$toId").add(chatMessage)
            .addOnSuccessListener {
                Log.d("chat", "saveUserDatatoFireStore")
                db.collection("ChatLogMessage").document("$toId").collection("$fromId").add(chatMessage)
            }
            .addOnFailureListener {
                Log.d("chat", "$it")
            }

    }

//    class ChatMyItem() : Item<ViewHolder>() {
//
//        private val items = mutableListOf<ChatMessageToFireStore>()
//
//        fun refresh(list: List<ChatMessageToFireStore>) {
//            items.apply {
//                clear()
//                addAll(list)
//            }
//        }
//        override fun getLayout(): Int {
//            return R.layout.chat_my_massage
//        }
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.chat_my_massage.text = items[position].text
////            Picasso.get().load(myImage).into(viewHolder.itemView.chat_my_Image)
//        }
//    }
//
//    class ChatFromItem() : Item<ViewHolder>() {
//
//        private val items = mutableListOf<ChatMessageToFireStore>()
//
//        fun refresh(list: List<ChatMessageToFireStore>) {
//            items.apply {
//                clear()
//                addAll(list)
//            }
//        }
//        override fun getLayout(): Int {
//            return R.layout.chat_your_massage
//        }
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.chat_your_message.text = items[position].text
////            Picasso.get().load(yourImage).into(viewHolder.itemView.chat_your_Image)
//        }
}
