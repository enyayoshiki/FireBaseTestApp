package com.example.firebasetestapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home_login.*
import kotlinx.android.synthetic.main.one_result_homeview.view.*

class HomeLogin : AppCompatActivity() {

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
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                val adapter = GroupAdapter<ViewHolder>()
                Log.d("home", "onDataChange")

                snapshot.children.forEach {
                    Log.d("home", "データ読み込み")
                    val user = it.getValue(User::class.java)
                    if (user != null)
                        adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLog::class.java)
                    intent.apply {
                        putExtra(USER_NAME, userItem.user.username)
                        putExtra(USER_KEY, userItem.user.uid)
                        putExtra(USER_IMAGE, userItem.user.userImage)
                    }

                    startActivity(intent)
                    finish()
                }
                homeView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
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

        var currentUser :User? = null
    }
}

class UserItem(val  user: User) :Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.one_result_homeview
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.homeViewUserName.text = user.username
        Picasso.get().load(user.userImage).into(viewHolder.itemView.homeViewImage)
    }
}
