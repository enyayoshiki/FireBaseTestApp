
package com.example.firebasetestapp.Activity.Thread_ChatRooms_MyPage

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.firebasetestapp.R
import com.example.firebasetestapp.dataClass.FcmRequest
import com.example.firebasetestapp.dataClass.User
import com.example.firebasetestapp.helper.FcmSendHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_thread_.*
import okhttp3.*
import java.io.IOException

class HomeFragment_Activity : AppCompatActivity() {

    private var handler = Handler()
    private val db = FirebaseFirestore.getInstance()


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
//        sendPushButton.setOnClickListener {
//            FcmSendHelper.sendPush(FcmRequest().apply {
//                this.to = "cFaWJHIBS6KPTQjuEbFPfp:APA91bFrWujzk7fh98oTvfAMpNtxWH7cPPL2AUinLngd2r_NN0hHTxTYYE84v4EESD2BM0LfD6U_KbySXnx4aMXYeomBlVc14hZPqXno4wRb0k8t16_T0Ky5w-ZnSzDGLxRdz25Jq2b-"
////                this.to = "fMrqcDtwTbyuV6QFgC_BtD:APA91bHyszZKqQHP4pGedK4EJFjD5jb-86gg6BqT6igws8OrMb-6-KsA0jSCtOBiqr341i9tr_dRSVleynaIa3aPWneW0W3sDSIpyxxwe7A5ExUo5dUk51Mo8_YiFPJ_H0fiDTwAj5IQ"
//                data.apply {
//                    this.title = "タイトル"
//                    this.message = "メッセージ"
//                    this.roomId = ""
//                }
//            }, { // 成功したとき
//                handler.post { Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show() }
//            }, { // 失敗したとき
//                handler.post { Toast.makeText(this, "失敗", Toast.LENGTH_SHORT).show() }
//            })
//        }

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

    private fun sendPushA() {
        FcmSendHelper.sendPushAll(FcmRequest.Data().apply {
            this.title = "AllDevicesTitle"
            this.message = "AllDevicesMessage"
        }, { // 成功したとき
            handler.post { Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show() }
        }, { // 失敗したとき
            handler.post { Toast.makeText(this, "失敗", Toast.LENGTH_SHORT).show() }
        })
    }

    companion object {
//        private const val TARGET_FCM_TOKEN =
//            "dG0RiRWNQxiqlqhwriZeWP:APA91bG-qh9TolH3GXBNoI-ggYFxPlIgdD-7zoWKiKAR4i6cDyCH9-A77BFpbNckpNmI9jDy5uXDjg6kixZmNwrSmcsx6B-AQm0ZCse6ppqT_iMuSSjTpJzAbRNc3ESZFvj0n7jmIi8G"
////            "cbePfwA1QpCiu18BhAXCP_:APA91bGULitYN9iZAhC2BEyh59iMUFDNjc9ZNm214GXct-w5L1T-6TYlBicKBuWzJEa671nVrYr3fZJOXMp848nKeKtNUwGBIxVCdzsHhWiThgUeHEgc0RwvFKpUMKg3W5l3001EXsTT"


        fun start(context: Context) {
            val intent = Intent(context, HomeFragment_Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }
}