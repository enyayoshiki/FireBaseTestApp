package com.example.firebasetestapp

import android.annotation.SuppressLint
import android.app.Activity
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
    private var myUser = mutableListOf<User>()

    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_login)
        Log.d("home","home画面")
        supportActionBar?.title = "HOME"

        uid = FirebaseAuth.getInstance().uid ?: ""
        if (uid.isEmpty()){
            startActivity(Intent(this,ResisterandLogin::class.java))
            return
        }
        fetchData()
    }


    @SuppressLint("LogNotTimber")
    private fun fetchData(){
        Log.d("home","fetchData開始")
        val db = FirebaseFirestore.getInstance()
        db.collection("User").get().addOnSuccessListener {

            val users = it.toObjects(User::class.java)

            val others = users.filterNot { it.uid == uid }

            val newUsers = mutableListOf<User>()

            users.forEach {
                if (it.uid != uid)
                    newUsers.add(it)
            }





            Log.d("home","fetchData実行")
//            Log.d("home","original : $it}")
//            Log.d("home","metadata : ${it.metadata}")
//            Log.d("home","document : ${it.documents}")
//            Log.d("home","query : ${it.query}")
//            Log.d("home","myQuery : ${myRef.result}")
            db.collection("User").whereEqualTo("uid", "${FirebaseAuth.getInstance().uid}").get().addOnSuccessListener {
                myUser = it.toObjects(User::class.java)
            }

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

        fun start(activity: Activity) {
            activity.finishAffinity()
            val intent = Intent(activity, HomeLogin::class.java)
            activity.startActivity(intent)
        }

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

