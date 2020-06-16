/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD


import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent

import com.wireguard.android.R
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.aliData.request.UsernameLoginRequest
import com.wireguard.android.alutils.DeviceInfo
import com.wireguard.android.alutils.ToastUtils
import com.wireguard.android.alutils.Utils
import com.wireguard.config.Config
import kotlinx.android.synthetic.main.ali_login_activity.*


class AliLoginActivity : AliBaseActivity() {

    companion object {
        private const val PAGE_STATE_FREE_TRIAL = "1"
        private const val PAGE_STATE_SIGN_IN = "2"

    }

    private var pageState:String =  PAGE_STATE_SIGN_IN// 1: free trial 2: sign in


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.ali_login_activity)
        initData()
    }
    override fun getLayoutId(): Int {
        return R.layout.ali_login_activity;
    }

    fun initData() {

        var deviceId = DeviceInfo.getDeviceId()
        var deviceBrand = DeviceInfo.getPhoneBrand()
        AppConfigData.deviceId = deviceId
        AppConfigData.deviceBrand = deviceBrand

        var value = intent.getStringExtra("PARAM_FROM")
        pageState = value
        if (value == PAGE_STATE_FREE_TRIAL) {  //试玩
            setTile("Free trial")
            bt_login.text =  getString(R.string.create_account)
            tv_forget_password.visibility = View.GONE
        } else if (value == PAGE_STATE_SIGN_IN) { //登录
            setTile("Sign in")
            bt_login.text =  getString(R.string.sign_in)
            tv_forget_password.visibility = View.VISIBLE
            val lastUserName = AppConfigData.loginName
            if (lastUserName != null && lastUserName.length > 1 ) {
                et_user_name.setText(lastUserName)
            }
        }

        tv_new_user.setOnClickListener {
            bt_login.text =  getString(R.string.sign_in)
        }
        bt_login.setOnClickListener {

            val username = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()

            val isCheckUserName =  Utils.checkStringUsername(username)
            val isCheckPassword =  Utils.isPasswordCorrect(password)

            if (!isCheckUserName){

                Toast.makeText(this, "用户名不符合规则，请重新输入", Toast.LENGTH_SHORT)
            }
            if (!isCheckPassword) {
                Toast.makeText(this, "密码不符合规则，请重新输入", Toast.LENGTH_SHORT)
            }

            if (isCheckUserName && isCheckPassword) {
                if (value == PAGE_STATE_FREE_TRIAL) {  //试玩
                    register(username,password)
                } else if ( value == PAGE_STATE_SIGN_IN) { //登录
                    login(username,password)
                }
            }


                /* if (username.length < 5) {
                     Toast.makeText(this,"用户名要大于等于5位以上的字母",Toast.LENGTH_SHORT)
                     return@setOnClickListener
                 }
                 if (password.length < 5) {
                     Toast.makeText(this,"密码要大于等于5位以上的字母和数字",Toast.LENGTH_SHORT)
                     return@setOnClickListener
                 } */

        }
    }

    private fun register(username:String, password:String) {

        val request = UsernameLoginRequest()
        request.deviceBrand = AppConfigData.deviceBrand
        request.deviceId = AppConfigData.deviceId
        request.username = username
        request.password = Utils.md5Encode(password)
        ApiClient.instance.service.register(request)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this,ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<LoginResponse>(this,true){
                    override fun businessFail(data: LoginResponse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: LoginResponse) {
                        if (data.data != null) {
                            goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this == null)
                             Toast.makeText(this, apiErrorModel.message, Toast.LENGTH_SHORT)
                    }
                })

    }

    private fun login(username:String, password:String) {

        val request = UsernameLoginRequest()
        request.deviceBrand = AppConfigData.deviceBrand
        request.deviceId = AppConfigData.deviceId
        request.username = username
        request.password = Utils.md5Encode(password)
        ApiClient.instance.service.login(request)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this,ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<LoginResponse>(this,true){
                    override fun businessFail(data: LoginResponse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: LoginResponse) {
                        if (data.data != null) {
                            goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this == null)
                            Toast.makeText(this, apiErrorModel.message, Toast.LENGTH_SHORT)                    }
                })

    }

    private fun loginByToken() {

    }

    private fun goSuccess(data:LoginResponse) {

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
}



