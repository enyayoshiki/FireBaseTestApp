
package com.example.firebasetestapp.Activity.Fragment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main_thread_.*

class HomeFragment_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_thread_)
//        mainThead_ViewPager.addOnPageChangeListener()
        setTabLayout()
    }
    // タブの設定
    private fun setTabLayout() {
        val adapter =
            TagAdapter_HomeFragment(
                supportFragmentManager,
                this
            )
        mainThead_ViewPager.adapter = adapter
        mainThread_Tablayout.setupWithViewPager(mainThead_ViewPager)
        for (i in 0 until adapter.count) {
            val tab: TabLayout.Tab = mainThread_Tablayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(mainThread_Tablayout, i)
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, HomeFragment_Activity::class.java)
            activity.startActivity(intent)
        }
    }
}