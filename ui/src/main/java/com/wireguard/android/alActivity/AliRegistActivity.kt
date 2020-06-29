package com.wireguard.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wireguard.android.R
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.request.UsernameLoginRequest
import com.wireguard.android.aliData.response.BaseResponseObject
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.alutils.DeviceInfo
import com.wireguard.android.alutils.ToastUtils
import com.wireguard.android.alutils.Utils
import kotlinx.android.synthetic.main.ali_regist_activity.*

class AliRegistActivity : AliBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initListener()
    }

    override fun getLayoutId(): Int {
        return R.layout.ali_regist_activity;
    }

    fun initData() {

        var deviceId = DeviceInfo.getDeviceId()
        var deviceBrand = DeviceInfo.getPhoneBrand()
        AppConfigData.deviceId = deviceId
        AppConfigData.deviceBrand = deviceBrand
        setTile("register")
    }

    fun initListener() {

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

        cl_email_regist_code.setOnClickListener {

            var email = et_user_name.text.toString().toLowerCase().trim()
            var ischeckEmail = Utils.isEmail(email)
            if (!ischeckEmail) {
                ToastUtils.show("请输入正确的的邮箱")

            } else {
                sendEmailCode(email)
            }
        }

        bt_register.setOnClickListener {
            var email = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()
            var code = et_email_code.text.toString().toString().trim()
            var ischeckEmail = Utils.isEmail(email)
            var ischeckPassword = Utils.isPasswordCorrect(password)
            var ischeckCode = Utils.isCodeCorrect(code)
            if (!ischeckEmail) {
                ToastUtils.show("请输入正确的的邮箱")
            } else if (!ischeckPassword) {
                ToastUtils.show("密码不符合规则，请重新输入")
            } else if (!ischeckCode) {
                ToastUtils.show("请输入6位验证码")
            } else if (ischeckEmail && ischeckPassword && ischeckCode) {
                register(email,password,code)
            }

        }
    }

    private fun sendEmailCode(email: String) {

        ApiClient.instance.service.sendEmailCode(email,1)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(this,true){
                    override fun businessFail(data: BaseResponseObject) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        ToastUtils.show("验证码已经发送")
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(apiErrorModel.message)
                        }
                    }
                })

    }

    private fun register(email: String, password: String, code: String) {

        val request = UsernameLoginRequest()
        request.deviceBrand = AppConfigData.deviceBrand
        request.deviceId = AppConfigData.deviceId
        request.username = email
        request.password = Utils.md5Encode(password)
        request.code = code

        ApiClient.instance.service.register(request)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
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
                        }
                    }
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