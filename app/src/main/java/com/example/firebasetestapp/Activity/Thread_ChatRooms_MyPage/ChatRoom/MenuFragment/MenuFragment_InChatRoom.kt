package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main_thread_.*
import kotlinx.android.synthetic.main.activity_menu_fragment__in_chat_room.*

class MenuFragment_InChatRoom : AppCompatActivity() {

    private var progressDialog: MaterialDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_fragment__in_chat_room)
        supportActionBar?.hide()
        setTabLayout()
    }
    // Fragmentの設定と、タブの設定
    private fun setTabLayout() {
//        fragmentを設置
        showProgress()
        val adapter =
            TagAdapter_MenuFragment(
                supportFragmentManager,
                this
            )
        in_thread_recyclerView.adapter = adapter
//        ここで細かいタブ設定(getTabView)
        menuFragment_tabLayout.setupWithViewPager(menuFragment_recyclerView)
        for (i in 0 until adapter.count) {
            val tab: TabLayout.Tab = menuFragment_tabLayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(menuFragment_tabLayout, i)
        }
        hideProgress()
    }

    private fun showProgress() {
        hideProgress()
        progressDialog = this.let {
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
        fun start(activity: Activity) {
            val intent = Intent(activity, HomeFragment_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}
