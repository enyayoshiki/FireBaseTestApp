package com.example.firebasetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


//{view clickでlayoutをそれぞれ変更(3type),実行ボタンもそれぞれのtypeで挙動を変更}
//
//Login View 実行   -> farebaseauth.getinstance()で検索  ->true  mailadress,UserId 表示
//　　　　　　　　　　　　　　　　      　　　　　　　　   ->false  return toast「登録がありません」
//resister View 実行-> farebaseauth.getinstance()に登録　
// 　　　　　　　　　　　　　　　->if(同じmailAdress)　return toast「すでに登録されています」
//                           ->同じのは無い　　　　　　登録 toast「登録」
//pass変更　View 実行-> mailAdress登録されている  true -> materialdialog で許可を求めてからpass変更
//                 　　　　　　　　　          null(adressが存在しない) -> return toast「登録がありません」


class MainActivity : AppCompatActivity() {

    var nowLoginType = LoginType.Login
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        initialize()
    }

    private fun initialize() {
        initLayout()
        var user = auth.currentUser
    }

    private fun initLayout() {
        loginTextView.setOnClickListener {
            changeLayout( LoginType.Login)
        }
        registerTextView.setOnClickListener {
            changeLayout(LoginType.Resister)
        }
        passChangeTextView.setOnClickListener {
            changeLayout( LoginType.PassChange)
        }
        excuteBtn.setOnClickListener {
            when (excuteBtn.text) {
                "ログイン" -> login()
                "登録" -> register()
                "登録変更" -> changeRegister()
            }
        }
        closeImageView.setOnClickListener{
            finish()
        }
    }

    private fun changeLayout(loginType: LoginType) {
        nowLoginType = loginType
        excuteBtn.text = getString(excuteBtnString(loginType))
        textViewColorChange(loginTextView, loginType == LoginType.Login)
        textViewColorChange(registerTextView, loginType == LoginType.Resister)
        textViewColorChange(passChangeTextView, loginType == LoginType.PassChange)
    }

    private fun textViewColorChange(textView: TextView,isSelected: Boolean){
    textView.apply {
        setTextColor(ContextCompat.getColor(this@MainActivity, if (isSelected) android.R.color.white else android.R.color.black))
        setBackgroundResource(if (isSelected) R.drawable.selector_button_black_to_black_80 else R.drawable.selector_button_white_to_white_80)
    }
}


    private fun login(){
        val pair = getPair()
        pair?.also {
            auth.signInWithEmailAndPassword(it.first, it.second)
                .addOnCompleteListener {
                    toast(if (it.isSuccessful) R.string.success else R.string.error)
                }
        }

    }

    private fun register(){
        val pair = getPair()
        pair?.also {
            auth.createUserWithEmailAndPassword(it.first, it.second)
                .addOnCompleteListener {
                    toast(if (it.isSuccessful) R.string.success else R.string.error)
                }
        }
    }

    private fun changeRegister(){
        val pair = getPair(true)
        pair?.also {
            auth.sendPasswordResetEmail(pair.first)
                .addOnCompleteListener {
                    toast(if (it.isSuccessful) R.string.success else R.string.error)
                }
        }
    }






    private fun getPair(isOnlyMailAddress: Boolean = false): Pair<String, String>? {
        val mailAddress = editMail.text.toString()
        if (mailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mailAddress).matches()) {
            toast(R.string.warn_mail)
            return null
        }
        val password = editPass.text.toString()
        if (!isOnlyMailAddress && (password.isEmpty() || password.length < 8)){
            toast(R.string.warn_password)
            return null
        }
        return Pair(mailAddress, password)
    }



    private fun excuteBtnString(loginType: LoginType): Int {
        return when (loginType) {
            LoginType.Login -> R.string.excute_login.toString().toInt()
            LoginType.Resister -> R.string.excute_register.toString().toInt()
            LoginType.PassChange -> R.string.excute_change.toString().toInt()
        }
    }

    private fun toast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }
}