/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.DialogFragment
import com.facebook.AccessToken
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.R
import com.sunblackhole.android.alWidget.UpdateDialog
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.response.LoginResponse
import com.sunblackhole.android.aliData.response.UpdateResponse
import com.sunblackhole.android.alutils.CountDownTimer
import com.sunblackhole.android.alutils.Utils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent


class SplashPageActivity : RxAppCompatActivity(), UpdateDialog.OnClickCancelListener{

    private lateinit var mTimer: CountDownTimer
    private var hasPaused: Boolean = false

    private var checkUpdateFinish: Boolean = false  //

    companion object {
        private const val SPLASH_TIME = 3500L  //3.5s
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     //   if ((getIntent().getFlags().and(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) ) != 0) {
            if (AppConfigData.customerInfo != null) { // 若已登录，跳转到主页
                intent.setClass(this, AliMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                return
            }
        setContentView(R.layout.ali_splash_page_activity)
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

    private fun initTimer(time: Long): CountDownTimer {
        mTimer = object : CountDownTimer(time , 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                gotoNext()
            }
        }
        return mTimer
    }

    private var isAutoLogin: Boolean = false

    fun gotoNext() {


        isAutoLogin = if (AppConfigData.loginToken != null && AppConfigData.loginToken!!.length > 1) true else false
        if (isAutoLogin) {
            var loginToken = AppConfigData.loginToken ?: ""
            var username = AppConfigData.loginName ?: ""
            ApiClient.instance.service.loginByToken(username,loginToken)
                    .compose(NetworkScheduler.compose())
                    .bindUntilEvent(this, ActivityEvent.DESTROY)
                    .subscribe(object : ApiResponse<LoginResponse>(this,false){
                        override fun businessFail(data: LoginResponse) {
                            ToastUtils.show(data.message ?: "")
                            gotoLoginAct()
                        }
                        override fun businessSuccess(data: LoginResponse) {
                            if (data.data != null) {
                                goLoginSuccess(data)
                            }
                        }
                        override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {

                            ToastUtils.show(apiErrorModel.message)
                            gotoLoginAct()

                        }
                    })
            return
        }

      gotoLoginAct()
    }

    private fun gotoLoginAct() {
        var intent = Intent(this@SplashPageActivity, AliLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goLoginSuccess(data:LoginResponse) {

        AppConfigData.initAuthorization(data.data.token,data.data.tokenHead)
        AppConfigData.customerInfo = data.data
        AppConfigData.loginName = data.data.username
        AppConfigData.password = data.data.password ?: ""
        AppConfigData.loginToken = data.data.token

        intent.setClass(this,AliMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()

    }


    private fun checkupVersion() {

        var v =  Utils.getVersion()
        var newV = v
        if (v.length > 6) {
            newV =  v.substring(0, v.length - 6)
        }
        ApiClient.instance.service.checkVersion(newV,"1")
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<UpdateResponse>(this,false){
                    override fun businessFail(data: UpdateResponse) {
                        gotoNext()
                    }
                    override fun businessSuccess(data: UpdateResponse) {
                        if (data.data != null) {
                            goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        gotoNext()
                    }
                })
    }

    private fun goSuccess(updateResponse: UpdateResponse) {
        if (updateResponse.data?.mustUpdate == 0) {
           // ToastUtils.show("The current version is already the latest")
            gotoNext()
        } else if (updateResponse.data?.mustUpdate ?:0 > 0) {

            var updateDialog = UpdateDialog()
            updateDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
            updateDialog.show(this?.supportFragmentManager,"updateflag")

            updateDialog.updateData(updateResponse.data)
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