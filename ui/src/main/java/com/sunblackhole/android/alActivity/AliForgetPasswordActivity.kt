package com.sunblackhole.android.alActivity

import android.os.Bundle
import android.text.InputType
import androidx.core.content.ContextCompat
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.sunblackhole.android.R
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.response.BaseResponseObject
import com.sunblackhole.android.alutils.DeviceInfo
import com.sunblackhole.android.alutils.ToastUtils
import com.sunblackhole.android.alutils.Utils
import com.sunblackhole.android.alutils.CountDownTimer
import kotlinx.android.synthetic.main.ali_forget_password_activity.*


class AliForgetPasswordActivity : AliBaseActivity() {

    private lateinit var mTimer: CountDownTimer

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
        setTile("")
        initTimer(180000)
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

        bt_forget_password.setOnClickListener {
            var email = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()
            var code = et_email_code.text.toString().toString().trim()
            var ischeckEmail = Utils.isEmail(email)
            var ischeckPassword = Utils.isPasswordCorrect(password)
            var ischeckCode = Utils.isCodeCorrect(code)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email address")
            } else if (!ischeckPassword) {
                ToastUtils.show("Use 6 or more characters(combination of letters,numbers)")
            } else if (!ischeckCode) {
                ToastUtils.show("Use 6 numbers code")
            } else if (ischeckEmail && ischeckPassword && ischeckCode) {
                forgetPassword(email,password,code)
            }

        }
    }

    private fun initTimer(time: Long): CountDownTimer {
        mTimer = object : CountDownTimer(time , 1000) {

            override fun onTick(millisUntilFinished: Long) {
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AliForgetPasswordActivity,R.color.text_light_black_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AliForgetPasswordActivity,R.color.text_666_color))
                btn_get_code.isEnabled = false
                var second = millisUntilFinished / 1000
                btn_get_code.text = second.toString() + "S resend"
            }
            override fun onFinish() {
                btn_get_code.text = "Get code"
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AliForgetPasswordActivity,R.color.text_blue_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AliForgetPasswordActivity,R.color.white))
                btn_get_code.isEnabled = true
            }
        }
        return mTimer
    }

    private fun sendEmailCode(email: String) {

        ApiClient.instance.service.sendEmailCode(email,2)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(this,true){
                    override fun businessFail(data: BaseResponseObject) {
                        if (data.code.contains("1001")) {
                            ToastUtils.show("Username does not exist")
                        }else {
                            ToastUtils.show(data.message ?: "")
                        }
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        ToastUtils.show("code alread send ")
                        mTimer.start()
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
                        ToastUtils.show("password is update success")
                        finish()
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this == null)
                            ToastUtils.show(apiErrorModel.message)
                    }
                })
    }

}