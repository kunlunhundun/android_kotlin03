package com.sunshinesky.android.albbActivity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.core.content.ContextCompat
import com.githang.statusbar.StatusBarCompat
import com.hjq.toast.ToastUtils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.sunshinesky.android.R
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.request.AlbbUsernameLoginRequest
import com.sunshinesky.android.albbData.response.AlbbBaseResponseObject
import com.sunshinesky.android.albbData.response.AlbbLoginResponseAlbb
import com.sunshinesky.android.albbUtils.AlbbCountDownTimer
import com.sunshinesky.android.albbUtils.AlbbDeviceInfo
import com.sunshinesky.android.albbUtils.AlbbLogUtils
import com.sunshinesky.android.albbUtils.AlbbUtils
import kotlinx.android.synthetic.main.albb_regist_activity.*

class AlbbRegistActivity : AlbbBaseActivity() {

    private lateinit var mTimerAlbb: AlbbCountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()
        initView()
    }

    override fun getLayoutId(): Int {
        return R.layout.albb_regist_activity;
    }

    fun initView() {

        mToolbar?.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.white));
        mToolbarIcon?.setImageResource(R.mipmap.icon_black_back)
    }


    fun initd() {

        var deviceId = AlbbDeviceInfo.getDeviceId()
        var deviceBrand = AlbbDeviceInfo.getPhoneBrand()
        AlbbAppConfigData.deviceId = deviceId
        AlbbAppConfigData.deviceBrand = deviceBrand
        setTile("")
        initTimer(180000L)
    }


    fun initData() {

        var deviceId = AlbbDeviceInfo.getDeviceId()
        var deviceBrand = AlbbDeviceInfo.getPhoneBrand()
        AlbbAppConfigData.deviceId = deviceId
        AlbbAppConfigData.deviceBrand = deviceBrand
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
            var ischeckEmail = AlbbUtils.isEmail(email)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email ")

            } else {
                sendEmailCode(email)
            }
        }

        cl_email_regist_code.setOnClickListener {

            var email = et_user_name.text.toString().toLowerCase().trim()
            var ischeckEmail = AlbbUtils.isEmail(email)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email ")

            } else {
                sendEmailCode(email)
            }
        }

        bt_register.setOnClickListener {
            var email = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()
            var code = et_email_code.text.toString().toString().trim()
            var ischeckEmail = AlbbUtils.isEmail(email)
            var ischeckPassword = AlbbUtils.isPasswordCorrect(password)
            var ischeckCode = AlbbUtils.isCodeCorrect(code)
            if (!ischeckEmail) {
                ToastUtils.show("invalid email ")
            } else if (!ischeckPassword) {
                ToastUtils.show("Password must be at least 6 characters and include numbers and letters")
            } else if (!ischeckCode) {
                ToastUtils.show("Enter your verification code")
            } else if (ischeckEmail && ischeckPassword && ischeckCode) {
                register(email,password,code)
            }

        }
    }
    private fun initTimer(time: Long): AlbbCountDownTimer {
        mTimerAlbb = object : AlbbCountDownTimer(time , 1000) {

            override fun onTick(millisUntilFinished: Long) {
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AlbbRegistActivity,R.color.text_light_black_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AlbbRegistActivity,R.color.text_666_color))
                btn_get_code.isEnabled = false
                var second = millisUntilFinished / 1000
                btn_get_code.text = second.toString() + "S resend"
                AlbbLogUtils.e(second.toString() + "s resend")
            }
            override fun onFinish() {
                btn_get_code.text = "Get code"
                btn_get_code.setBackgroundColor(ContextCompat.getColor(this@AlbbRegistActivity,R.color.text_blue_color))
                btn_get_code.setTextColor(ContextCompat.getColor(this@AlbbRegistActivity,R.color.white))
                btn_get_code.isEnabled = true
            }
        }
        return mTimerAlbb
    }


    private fun sendEmailCode(email: String) {

        AlbbApiClient.instance.serviceAlbb.sendEmailCode(email,1)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbBaseResponseObject>(this,true){
                    override fun businessFail(data: AlbbBaseResponseObject) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbBaseResponseObject) {
                        ToastUtils.show("We sent you a verification code to your email")
                        mTimerAlbb.start()
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }
                    }
                })

    }

    private fun register(email: String, password: String, code: String) {

        val request = AlbbUsernameLoginRequest()
        request.deviceBrand = AlbbAppConfigData.deviceBrand
        request.deviceId = AlbbAppConfigData.deviceId
        request.username = email
        request.password = AlbbUtils.md5Encode(password)
        request.code = code

        AlbbApiClient.instance.serviceAlbb.register(request)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbLoginResponseAlbb>(this,true){
                    override fun businessFail(data: AlbbLoginResponseAlbb) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbLoginResponseAlbb) {
                        if (data.data != null) {
                            goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }
                    }
                })
    }

    private fun goSuccess(data:AlbbLoginResponseAlbb) {

        AlbbAppConfigData.initAuthorization(data.data.token,data.data.tokenHead)
        AlbbAppConfigData.albbCustomerInfo = data.data
        AlbbAppConfigData.loginName = data.data.username
        AlbbAppConfigData.password = data.data.password
        AlbbAppConfigData.loginToken = data.data.token

        var intent = intent.setClass(this,AlbbMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}