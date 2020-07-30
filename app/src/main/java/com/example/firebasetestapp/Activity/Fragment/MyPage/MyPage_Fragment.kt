package com.example.firebasetestapp.Activity.Fragment.MyPage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MyPage_Fragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        fun newInstance(position: Int): MyPage_Fragment {
            return MyPage_Fragment()
        }
    }
}