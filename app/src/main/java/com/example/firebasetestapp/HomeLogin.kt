package com.example.firebasetestapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_login.*

class HomeLogin : AppCompatActivity() {

    private val customAdapter by lazy { HomeUserCustomAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_login)
        Log.d("home","home画面")
        supportActionBar?.title = "HOME"

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this,ResisterandLogin::class.java)
        }
        fetchData()
    }


    private fun fetchData(){
        Log.d("home","fetchData開始")


        val db = FirebaseFirestore.getInstance()
        db.collection("User").get().addOnSuccessListener {
            Log.d("home","fetchData実行")
            homeView_recyclerView.apply {
                adapter = customAdapter
                setHasFixedSize(true)
            }
            val user = it.toObjects(User::class.java)
            customAdapter.refresh(user)

//            adapter.setOnItemClickListener {
//                    val userItem = item as UserItem
//
//                    val intent = Intent(view.context, ChatLog::class.java)
//                    intent.apply {
//                        putExtra(USER_NAME, userItem.user.username)
//                        putExtra(USER_KEY, userItem.user.uid)
//                        putExtra(USER_IMAGE, userItem.user.userImage)
//                    }
//
//                    startActivity(String())
//                    finish()
//                }
        }
            .addOnFailureListener{
                Log.d("home","$it")
            }

//        val ref = FirebaseDatabase.getInstance().getReference("/users")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener{
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                currentUser = snapshot.getValue(User::class.java)
//                val adapter = GroupAdapter<ViewHolder>()
//                Log.d("home", "onDataChange")
//
//                snapshot.children.forEach {
//                    Log.d("home", "データ読み込み")
//                    val user = it.getValue(User::class.java)
//                    if (user != null)
//                        adapter.add(UserItem(user))
//                }
//
//                adapter.setOnItemClickListener { item, view ->
//                    val userItem = item as UserItem
//
//                    val intent = Intent(view.context, ChatLog::class.java)
//                    intent.apply {
//                        putExtra(USER_NAME, userItem.user.username)
//                        putExtra(USER_KEY, userItem.user.uid)
//                        putExtra(USER_IMAGE, userItem.user.userImage)
//                    }
//
//                    startActivity(intent)
//                    finish()
//                }
//                homeView.adapter = adapter
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_homelogin,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("home","メニュー表示")
        when(item?.itemId) {
            R.id.newMessage_menu -> {
            }
            R.id.signOut_menu -> {
                val intent = Intent(this,ResisterandLogin::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        val USER_NAME = "USER_NAME"
        val USER_KEY = "USER_KEY"
        val USER_IMAGE = "USER_IMAGE"

    }
}

//class UserItem(val  user: MutableList<User>) :Item<ViewHolder>() {
//    override fun getLayout(): Int {
//        return R.layout.one_result_homeview
//    }
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        val item = user[position]
//        viewHolder.itemView.homeViewUserName.text = item.username
//        Picasso.get().load(item.userImage).into(viewHolder.itemView.homeViewImage)
//        viewHolder.itemView.rootView_Home.setOnClickListener{
//
//            val intent = Intent(it.context, ChatLog::class.java)
//            intent.apply {
//                putExtra(HomeLogin.USER_NAME, item.username)
//                putExtra(HomeLogin.USER_KEY, item.uid)
//                putExtra(HomeLogin.USER_IMAGE, item.userImage)
//            }
//            it.context.startActivity(intent)
//        }
//    }
//}

