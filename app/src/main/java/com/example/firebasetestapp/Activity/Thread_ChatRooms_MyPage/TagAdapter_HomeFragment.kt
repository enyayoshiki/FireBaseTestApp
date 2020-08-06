package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.ChatRooms_Fragment
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage.MyPage_Fragment
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout

class TagAdapter_HomeFragment (fm: FragmentManager, private val context: Context) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {
    private val pageTitle = arrayOf("掲示板", "チャットルーム", "マイページ")
    private val pageImage = arrayOf(R.drawable.ic_settings,R.drawable.ic_chatroom,R.drawable.ic_mypage)
    //
    override fun getItem(position: Int): Fragment {

        return when(position){
            0 -> Thread_Fragment.newInstance(position)
            1 -> ChatRooms_Fragment.newInstance(position)
            else -> MyPage_Fragment.newInstance(position)
        }
        // 要求時 新しい Fragment を生成して返す
        return Thread_Fragment.newInstance(position + 1)
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