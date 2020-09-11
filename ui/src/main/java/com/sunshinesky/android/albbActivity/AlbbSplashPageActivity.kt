/*
 */

package com.sunshinesky.android.albbActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.R
import com.sunshinesky.android.albbWidget.AlbbUpdateDialog
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbLoginResponseAlbb
import com.sunshinesky.android.albbData.response.AlbbUpdateResponse
import com.sunshinesky.android.albbUtils.AlbbCountDownTimer
import com.sunshinesky.android.albbUtils.AlbbUtils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent


class AlbbSplashPageActivity : RxAppCompatActivity(), AlbbUpdateDialog.OnClickCancelListener{

    private lateinit var mTimerAlbb: AlbbCountDownTimer
    private var hasPaused: Boolean = false

    private var checkUpdateFinish: Boolean = false  //

    companion object {
        private const val SPLASH_TIME = 3500L  //3.5s
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     //   if ((getIntent().getFlags().and(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) ) != 0) {
            if (AlbbAppConfigData.albbCustomerInfo != null) { // 若已登录，跳转到主页
                intent.setClass(this, AlbbMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                return
            }
        setContentView(R.layout.albb_splash_page_activity)
        initData()
        initListener()

    }

    fun initData() {
        //initTimer(SPLASH_TIME).start()
      /*  Handler().postDelayed({
            gotoNext()
        },3000) */
        checkupVersion()
    }

    fun initListener() {

    }

    private fun initTimer(time: Long): AlbbCountDownTimer {
        mTimerAlbb = object : AlbbCountDownTimer(time , 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                gotoNext()
            }
        }
        return mTimerAlbb
    }

    private var isAutoLogin: Boolean = false

    fun gotoNext() {


        isAutoLogin = if (AlbbAppConfigData.loginToken != null && AlbbAppConfigData.loginToken!!.length > 1) true else false
        if (isAutoLogin) {
            var loginToken = AlbbAppConfigData.loginToken ?: ""
            var username = AlbbAppConfigData.loginName ?: ""
            AlbbApiClient.instance.serviceAlbb.loginByToken(username,loginToken)
                    .compose(AlbbNetworkScheduler.compose())
                    .bindUntilEvent(this, ActivityEvent.DESTROY)
                    .subscribe(object : AlbbApiResponse<AlbbLoginResponseAlbb>(this,false){
                        override fun businessFail(data: AlbbLoginResponseAlbb) {
                            ToastUtils.show(data.message ?: "")
                            gotoLoginAct()
                        }
                        override fun businessSuccess(data: AlbbLoginResponseAlbb) {
                            if (data.data != null) {
                                goLoginSuccess(data)
                            }
                        }
                        override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {

                            ToastUtils.show(albbApiErrorModel.message)
                            gotoLoginAct()

                        }
                    })
            return
        }

      gotoLoginAct()
    }

    private fun gotoLoginAct() {
        var intent = Intent(this@AlbbSplashPageActivity, AlbbLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goLoginSuccess(data:AlbbLoginResponseAlbb) {

        AlbbAppConfigData.initAuthorization(data.data.token,data.data.tokenHead)
        AlbbAppConfigData.albbCustomerInfo = data.data
        AlbbAppConfigData.loginName = data.data.username
        AlbbAppConfigData.password = data.data.password ?: ""
        AlbbAppConfigData.loginToken = data.data.token

        intent.setClass(this,AlbbMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()

    }


    private fun checkupVersion() {

        var v =  AlbbUtils.getVersion()
        var newV = v
        if (v.length > 6) {
            newV =  v.substring(0, v.length - 6)
        }
        AlbbApiClient.instance.serviceAlbb.checkVersion(newV,"1")
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbUpdateResponse>(this,false){
                    override fun businessFail(data: AlbbUpdateResponse) {
                        gotoNext()
                    }
                    override fun businessSuccess(data: AlbbUpdateResponse) {
                        if (data.data != null) {
                            goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        gotoNext()
                    }
                })
    }

    private fun goSuccess(updateResponse: AlbbUpdateResponse) {
        if (updateResponse.data?.mustUpdate == 0) {
           // ToastUtils.show("The current version is already the latest")
            gotoNext()
        } else if (updateResponse.data?.mustUpdate ?:0 > 0) {

            var updateDialog = AlbbUpdateDialog()
            updateDialog.mUpdateObj = updateResponse.data
            updateDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
            updateDialog.show(this?.supportFragmentManager,"updateflag")
            updateDialog.setOnCancelListener(this)
        }
    }

    override fun onClickListenerCancel() {
        checkUpdateFinish = true
        gotoNext()
    }

    override fun onPause() {
        super.onPause()
       // mTimer.cancel()
        hasPaused = true
    }

    override fun onResume() {
        super.onResume()
        if (hasPaused) {
            hasPaused = false
            //initTimer(SPLASH_TIME).start()
        }
    }

}