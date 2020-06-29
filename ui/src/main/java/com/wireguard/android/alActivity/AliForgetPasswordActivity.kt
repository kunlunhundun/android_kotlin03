package com.wireguard.android.alActivity

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
import kotlinx.android.synthetic.main.ali_forget_password_activity.*


class AliForgetPasswordActivity : AliBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initListener()
    }

    override fun getLayoutId(): Int {
        return  R.layout.ali_forget_password_activity
    }

    fun initData() {

        var deviceId = DeviceInfo.getDeviceId()
        var deviceBrand = DeviceInfo.getPhoneBrand()
        AppConfigData.deviceId = deviceId
        AppConfigData.deviceBrand = deviceBrand

        setTile("forgot password")

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
                Toast.makeText(this,"请输入正确的的邮箱",Toast.LENGTH_SHORT)
            } else {
                sendEmailCode(email)
            }
        }

        bt_forget_password.setOnClickListener {
            var email = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()
            var code = et_email_code.text.toString().toString().trim()
            var ischeckEmail = Utils.isEmail(email)
            var ischeckPassword = Utils.isPasswordCorrect(password)
            var ischeckCode = Utils.isCodeCorrect(code)
            if (!ischeckEmail) {
                Toast.makeText(this,"请输入正确的的邮箱",Toast.LENGTH_SHORT)
            } else if (!ischeckPassword) {
                Toast.makeText(this, "密码不符合规则，请重新输入", Toast.LENGTH_SHORT)
            } else if (!ischeckCode) {
                Toast.makeText(this, "请输入6位验证码", Toast.LENGTH_SHORT)
            } else if (ischeckEmail && ischeckPassword && ischeckCode) {
                forgetPassword(email,password,code)
            }

        }
    }

    private fun sendEmailCode(email: String) {

        ApiClient.instance.service.sendEmailCode(email,2)
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

    private fun forgetPassword(email: String, password:String, code:String) {

        ApiClient.instance.service.updatePassword(email,Utils.md5Encode(password),code)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(this,true){
                    override fun businessFail(data: BaseResponseObject) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        ToastUtils.show("密码已经更新成功")
                        finish()
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this == null)
                            Toast.makeText(this, apiErrorModel.message, Toast.LENGTH_SHORT)
                    }
                })
    }

}