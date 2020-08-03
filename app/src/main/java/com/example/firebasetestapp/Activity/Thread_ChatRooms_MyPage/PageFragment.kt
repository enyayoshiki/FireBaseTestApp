package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firebasetestapp.R

class PageFragment: Fragment() {
//    private var mListener : OnFragmentInteractionListener? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        val page = arguments!!.getInt(PAGE, 0)
        // fragment_pageを複製しtextへページを表示
        return inflater.inflate(R.layout.mainthread_fragment, container, false)
    }
    // 親アクティビティとの紐づけ発生イベント
//    override fun onAttach(context: Context) {
//        if (context == null) return
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException()
//        }
//    }
//    override fun onDetach() {
//        super.onDetach()
//        mListener = null
//    }
//    internal interface OnFragmentInteractionListener {
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {
        private val PAGE = "PAGE"
        // PageFragment生成
        fun newInstance(page: Int): PageFragment {
            val pageFragment = PageFragment()
            val bundle = Bundle()
            bundle.putInt(PAGE, page)
            pageFragment.arguments = bundle
            return pageFragment
        }
    }
}