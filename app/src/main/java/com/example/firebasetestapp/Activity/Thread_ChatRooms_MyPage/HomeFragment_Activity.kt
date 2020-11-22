
package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.R
import com.example.firebasetestapp.extention.Visible
import com.example.firebasetestapp.extention.sendPush
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main_thread_.*
import okhttp3.*
import java.io.IOException

class HomeFragment_Activity : AppCompatActivity() {

    //    private val fragmentList = arrayListOf(
//        Thread_Fragment(),
//        ChatRoom_Fragment(),
//        MyPage_Fragment()
//    )
    private var progressDialog: MaterialDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_thread_)
//        mainThead_ViewPager.addOnPageChangeListener()
        supportActionBar?.hide()
        setTabLayout()
        sendPushButton.setOnClickListener {
            sendPushA(TARGET_FCM_TOKEN)
        }


    }

    // Fragmentの設定と、タブの設定
    private fun setTabLayout() {
//        fragmentを設置
        showProgress()
        val adapter = TagAdapter_HomeFragment(supportFragmentManager, this)
        in_thread_recyclerView.adapter = adapter
//        ここで細かいタブ設定(getTabView)
        mainThread_Tablayout.setupWithViewPager(in_thread_recyclerView)
        for (i in 0 until adapter.count) {
            val tab: TabLayout.Tab = mainThread_Tablayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(mainThread_Tablayout, i)
        }
        hideProgress()
    }

    private fun showProgress() {
        hideProgress()
        progressDialog = this.let {
            MaterialDialog(it).apply {
                cancelable(false)
                setContentView(
                    LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false)
                )
                show()
            }
        }

    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }


    private fun sendPushA(fcmToken: String = TARGET_FCM_TOKEN) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader(
                "Authorization",
                "key=AAAAYoCkPaY:APA91bFAxP61YE38Sc33dA8uVCzmU0FtIKzNdTjk5LUXkhPkQneTFGjKHpcqlQyaXHPfFkcE7SVVNo0pDZms4fmfIT7reBFopEnPimgvz4229howecP8kmDj2pMfE4UMXXIh0x3XUN-O"
            )
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    "{\"to\":\"$fcmToken\",\"data\":{\"name\":\"123546\",\"message\":\"123456\"}}"
                )
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM_Custom", "失敗")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("FCM_Custom", "成功")
            }
        })
    }

    companion object {
        private const val TARGET_FCM_TOKEN =
            "cbePfwA1QpCiu18BhAXCP_:APA91bGULitYN9iZAhC2BEyh59iMUFDNjc9ZNm214GXct-w5L1T-6TYlBicKBuWzJEa671nVrYr3fZJOXMp848nKeKtNUwGBIxVCdzsHhWiThgUeHEgc0RwvFKpUMKg3W5l3001EXsTT"


        fun start(context: Context) {
            val intent = Intent(context, HomeFragment_Activity::class.java)
            context.startActivity(intent)
        }

    }
}