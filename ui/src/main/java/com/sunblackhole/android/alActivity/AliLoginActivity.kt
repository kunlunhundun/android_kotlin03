/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.view.Gravity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.R
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.request.UsernameLoginRequest
import com.sunblackhole.android.aliData.response.LoginResponse
import com.sunblackhole.android.alutils.DeviceInfo
import com.sunblackhole.android.alutils.LogUtils
import com.sunblackhole.android.alutils.Utils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.ali_login_activity.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.concurrent.timer


class AliLoginActivity : AliBaseActivity() {

    companion object {
        private const val PAGE_STATE_FREE_TRIAL = "1"
        private const val PAGE_STATE_SIGN_IN = "2"

    }

    private var pageState:String =  PAGE_STATE_SIGN_IN// 1: free trial 2: sign in

    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.ali_login_activity)
        initData()
        initListener()
        facebooksigin()
    }
    override fun getLayoutId(): Int {
        return R.layout.ali_login_activity;
    }

    fun initData() {

        ToastUtils.setGravity(Gravity.CENTER, 0, -300)

        var deviceId = DeviceInfo.getDeviceId()
        var deviceBrand = DeviceInfo.getPhoneBrand()
        AppConfigData.deviceId = deviceId
        AppConfigData.deviceBrand = deviceBrand

        setHasBackArrow(false)
        setTile("")
        //var value = intent.getStringExtra("PARAM_FROM")
        //pageState = value

    }

    private fun facebooksigin() {
        try {
            val info = packageManager.getPackageInfo(
                    "com.sunblackhole.android",  //Insert your own package name.
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                LogUtils.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

    }

    private fun loginByFaceBook() {
        // Callback registration
        login_button_fb.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                LogUtils.d("AliLoginActivity login success" + loginResult?.accessToken)
            }

            override fun onCancel() {

                LogUtils.d("AliLoginActivity login cancel" )
                // App code
            }

            override fun onError(exception: FacebookException) {

                LogUtils.d("AliLoginActivity login error" + exception )

                // App code
            }
        })
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
                ToastUtils.show("username is invalid")
            }
            if (!isCheckPassword) {
                ToastUtils.show("Use 6 or more characters(combination of letters,numbers)")
            }

            if (isCheckUserName && isCheckPassword) {
                login(username,password)
            }
        }

        callbackManager = CallbackManager.Factory.create();

        login_button_fb.setOnClickListener {

            loginByFaceBook()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}



