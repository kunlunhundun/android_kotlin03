package com.sunblackhole.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.hjq.toast.ToastUtils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.sunblackhole.android.R
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.request.UsernameLoginRequest
import com.sunblackhole.android.aliData.response.BaseResponseObject
import com.sunblackhole.android.aliData.response.LoginResponse
import com.sunblackhole.android.alutils.CountDownTimer
import com.sunblackhole.android.alutils.DeviceInfo
import com.sunblackhole.android.alutils.LogUtils
import com.sunblackhole.android.alutils.Utils
import kotlinx.android.synthetic.main.ali_regist_activity.*

class AliRegistActivity : AliBaseActivity() {

    private lateinit var mTimer: CountDownTimer

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
        setTile("")
        initTimer(180000L)
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

        btn_get_code.setOnClickListener {

            var email = et_user_name.text.toString().toLowerCase().trim()
            var ischeckEmail = Utils.isEmail(email)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email address")

            } else {
                sendEmailCode(email)
            }
        }

        cl_email_regist_code.setOnClickListener {

            var email = et_user_name.text.toString().toLowerCase().trim()
            var ischeckEmail = Utils.isEmail(email)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email address")

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
                ToastUtils.show("invalid email address")
            } else if (!ischeckPassword) {
                ToastUtils.show("Enter a combination of at least 6 numbers and letters.")
            } else if (!ischeckCode) {
                ToastUtils.show("Please input verification code")
            } else if (ischeckEmail && ischeckPassword && ischeckCode) {
                register(email,password,code)
            }

        }
    }
    private fun initTimer(time: Long): CountDownTimer {
        mTimer = object : CountDownTimer(time , 1000) {

            override fun onTick(millisUntilFinished: Long) {
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AliRegistActivity,R.color.text_light_black_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AliRegistActivity,R.color.text_666_color))
                btn_get_code.isEnabled = false
                var second = millisUntilFinished / 1000
                btn_get_code.text = second.toString() + "S resend"
                LogUtils.e(second.toString() + "s resend")
            }
            override fun onFinish() {
                btn_get_code.text = "Get code"
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AliRegistActivity,R.color.text_blue_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AliRegistActivity,R.color.white))
                btn_get_code.isEnabled = true
            }
        }
        return mTimer
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
                        ToastUtils.show("We've sent a verification code to your email address.\n ")
                        mTimer.start()
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