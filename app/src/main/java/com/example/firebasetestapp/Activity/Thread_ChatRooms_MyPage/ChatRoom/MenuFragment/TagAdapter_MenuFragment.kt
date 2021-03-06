package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.In_ChatRoom_Activity
import com.example.firebasetestapp.R
import com.google.android.material.tabs.TabLayout
import timber.log.Timber

class TagAdapter_MenuFragment  (fm: FragmentManager, private val context: Context, private var roomId : String) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {

    private val pageTitle = arrayOf("フレンド申請", "フレンド招待")
    private val pageImage = arrayOf(R.drawable.ic_add_firend, R.drawable.ic_add_chatmember)


    override fun getItem(position: Int): Fragment {

        Timber.i("roomID: "+roomId)

        return when(position){
            0 -> Add_Friend_Fragment.newInstance(position, roomId)
            else-> Add_ChatMember_Fragment.newInstance(position, roomId)
        }

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
        val view = LayoutInflater.from(this.context).inflate(R.layout.tablayout_menufragment, tabLayout, false)
        // タイトル
        val tab = view.findViewById<TextView>(R.id.tab_item_text)
        tab.text = pageTitle[position]
        // アイコン
        val image = view.findViewById<ImageView>(R.id.tab_item_image)
        image.setImageResource(pageImage[position])
        return view
    }
}