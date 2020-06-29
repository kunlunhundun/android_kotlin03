/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wireguard.android.R
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.alutils.CountDownTimer
import com.wireguard.android.alutils.ToastUtils
import com.wireguard.config.Config
import java.math.BigDecimal
import java.text.DecimalFormat

class SplashPageActivity : RxAppCompatActivity() {

    private lateinit var mTimer: CountDownTimer
    private var hasPaused: Boolean = false

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
        gotoNext()

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
                    .subscribe(object : ApiResponse<LoginResponse>(this,true){
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
        AppConfigData.password = data.data.password
        AppConfigData.loginToken = data.data.token

        intent.setClass(this,AliMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()

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