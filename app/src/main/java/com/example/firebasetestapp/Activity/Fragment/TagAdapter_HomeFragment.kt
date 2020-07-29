package com.example.firebasetestapp.Activity.Fragment

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.firebasetestapp.Activity.Fragment.PageFragment
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout

class TagAdapter_HomeFragment (fm: FragmentManager, private val context: Context) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {
    private val pageTitle = arrayOf("スレッド一覧", "チャットルーム", "マイページ")
    private val pageImage = arrayOf(R.drawable.ic_settings,R.drawable.ic_chatroom,R.drawable.ic_mypage)
    //
    override fun getItem(position: Int): Fragment {
        // 要求時 新しい Fragment を生成して返す
        return PageFragment.newInstance(position + 1)
    }
    // タブの名前
    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitle[position]
    }
    // タブの個数
    override fun getCount(): Int {
        return pageTitle.size
    }
    // タブの変更
    fun getTabView(tabLayout: TabLayout, position: Int): android.view.View? {
        // tab_item.xml を複数
        val view = LayoutInflater.from(this.context).inflate(R.layout.tablayout_homefragment, tabLayout, false)
        // タイトル
        val tab = view.findViewById<TextView>(R.id.teb_item_text)
        tab.text = pageTitle[position]
        // アイコン
        val image = view.findViewById<ImageView>(R.id.teb_item_image)
        image.setImageResource(pageImage[position])
        return view
    }
}