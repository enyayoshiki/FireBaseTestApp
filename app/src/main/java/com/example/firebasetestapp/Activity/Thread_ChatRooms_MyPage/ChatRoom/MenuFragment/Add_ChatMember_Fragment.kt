package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.ChatRoom.MenuFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.HomeFragment_Activity
import com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.Thread.Thread_Fragment
import com.example.firebasetestapp.R

class Add_ChatMember_Fragment: Fragment()  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mainthread_fragment, container, false)
    }


    companion object {
        fun newInstance(position: Int): Thread_Fragment {
            return Thread_Fragment()
        }
    }
}