package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage.MyPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.firebasetestapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.Activity.Login_Resister_PassChange.Login_Activity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_setting.*


class MyPage_Fragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_setting, container, false)
    }

    private fun initialize() {
        initLayout()
    }

    private fun initLayout(){
        profile_setting_textView.setOnClickListener{
            val intent = Intent(it.context, ProfileChange_Activity::class.java)
            context?.startActivity(intent)
        }
        password_setting_textView.setOnClickListener{
            val intent = Intent(it.context, PasswordChange_myPage_Activity::class.java)
            context?.startActivity(intent)
        }
        logOut_textView.setOnClickListener{
            checkLogout()
        }
    }

    private fun checkLogout() {
        context?.also {
            MaterialDialog(it).show {
                title(null, "ログアウトしますか？")
                positiveButton(R.string.positivebtn_materialdialog) {
                    val intent = Intent(it.context, Login_Activity::class.java)
                    activity?.finishAffinity()
                    FirebaseAuth.getInstance().signOut()
                    it.context.startActivity(intent)
                }
                negativeButton(R.string.negativebtn_materialdialog) {
                    return@negativeButton
                }
            }
        }
    }

    companion object {
        fun newInstance(position: Int): MyPage_Fragment {
            return MyPage_Fragment()
        }
    }
}