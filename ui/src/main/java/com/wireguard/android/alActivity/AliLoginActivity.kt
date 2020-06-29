/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.text.InputType

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
        initListener()
    }
    override fun getLayoutId(): Int {
        return R.layout.ali_login_activity;
    }

    fun initData() {

        var deviceId = DeviceInfo.getDeviceId()
        var deviceBrand = DeviceInfo.getPhoneBrand()
        AppConfigData.deviceId = deviceId
        AppConfigData.deviceBrand = deviceBrand

        setHasBackArrow(false)
        setTile("login")
        //var value = intent.getStringExtra("PARAM_FROM")
        //pageState = value

    }

    private fun initListener() {

        ck_pwd_show.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                }
                false -> {
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                }
            }
        }

        tv_register.setOnClickListener {
            var intent = Intent(this,AliRegistActivity::class.java)
            startActivity(intent)
        }
        tv_forget_password.setOnClickListener {
            var intent = Intent(this, AliForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        bt_login.setOnClickListener {

            val username = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()


            var isCheckUserName =  Utils.checkStringUsername(username)
            val isCheckPassword =  Utils.isPasswordCorrect(password)
            if (username.length < 3) {
                isCheckUserName = false
            }
            if (!isCheckUserName){
                ToastUtils.show("用户名不符合规则，请重新输入")
            }
            if (!isCheckPassword) {
                Toast.makeText(this, "密码不符合规则，请重新输入", Toast.LENGTH_SHORT)
            }

            if (isCheckUserName && isCheckPassword) {
                login(username,password)
            }
        }



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
                        if (this != null) {
                            ToastUtils.show(apiErrorModel.message)
                        }                    }
                })

    }


    private fun goSuccess(data:LoginResponse) {

        AppConfigData.initAuthorization(data.data.token,data.data.tokenHead)
        AppConfigData.customerInfo = data.data
        AppConfigData.loginName = data.data.username
        AppConfigData.password = data.data.password
        AppConfigData.loginToken = data.data.token

        var intent = intent.setClass(this,AliMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}



