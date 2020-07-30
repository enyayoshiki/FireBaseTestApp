package com.example.firebasetestapp.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetestapp.Activity.Fragment.HomeFragment_Activity
import com.example.firebasetestapp.Activity.Fragment.TagAdapter_HomeFragment
import com.example.firebasetestapp.Activity.Fragment.Thread_Fragment
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main_thread_.*

class In_Thread_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in__thread_)

        setTabLayout()
    }
    private fun setTabLayout() {
//        fragmentを設置
        val adapter = TagAdapter_HomeFragment(supportFragmentManager, this)
        in_thread_recyclerView.adapter = adapter
//        ここで細かいタブ設定(getTabView)
        mainThread_Tablayout.setupWithViewPager(in_thread_recyclerView)
        for (i in 0 until adapter.count) {
            val tab: TabLayout.Tab = mainThread_Tablayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(mainThread_Tablayout, i)
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, In_Thread_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}