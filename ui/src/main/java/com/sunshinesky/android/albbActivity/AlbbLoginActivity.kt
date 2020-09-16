
package com.sunshinesky.android.albbActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Base64
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.githang.statusbar.StatusBarCompat
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.request.AlbbUsernameLoginRequest
import com.sunshinesky.android.albbData.response.AlbbLoginResponseAlbb
import com.sunshinesky.android.albbInterface.AlbbGetUserCallback
import com.sunshinesky.android.albbUtils.AlbbDeviceInfo
import com.sunshinesky.android.albbUtils.AlbbLogUtils
import com.sunshinesky.android.albbUtils.AlbbUtils
import com.sunshinesky.android.R
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.albb_login_activity.*
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class AlbbLoginActivity : AlbbBaseActivity() , AlbbGetUserCallback.IGetUserResponse{

    companion object {
        private const val PAGE_STATE_FREE_TRIAL = "1"
        private const val PAGE_STATE_SIGN_IN = "2"
    }

    private var pageState:String =  PAGE_STATE_SIGN_IN// 1: free trial 2: sign in
    private var callbackManager: CallbackManager? = null
    private val EMAIL = "email"
    private val AUTH_TYPE = "rerequest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()
        initView()
        facebooksigin()

    }
    override fun getLayoutId(): Int {
        return R.layout.albb_login_activity;
    }

    fun initView() {

        mToolbar?.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.white));
    }

    fun initData() {

        try {
            val info = packageManager.getPackageInfo(
                    "com.sunshinesky.android",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                AlbbLogUtils.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
        } catch (e: Exception) {
        }

        ToastUtils.setGravity(Gravity.CENTER, 0, -300)

        var deviceId = AlbbDeviceInfo.getDeviceId()
        var deviceBrand = AlbbDeviceInfo.getPhoneBrand()
        AlbbAppConfigData.deviceId = deviceId
        AlbbAppConfigData.deviceBrand = deviceBrand
        setHasBackArrow(false)
        setTile("")
        //var value = intent.getStringExtra("PARAM_FROM")
        //pageState = value
    }

    private fun facebooksigin() {
        try {
            val packageName: String = this.getApplicationContext().getPackageName()
            AlbbLogUtils.e("packageName------>" + packageName)
            val info = packageManager.getPackageInfo(
                    "com.sunshinesky.android",  //Insert your own package name.
                    PackageManager.GET_SIGNATURES)

            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                AlbbLogUtils.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }


    private fun getFacebookUserinfo() {

        makeUserRequest(AlbbGetUserCallback(this).callback)
    }

    private val ME_ENDPOINT = "/me"

    fun makeUserRequest(callback: GraphRequest.Callback?) {
        val params = Bundle()
        params.putString("fields", "picture,name,id,email,permissions")
        val request = GraphRequest(
                AccessToken.getCurrentAccessToken(), ME_ENDPOINT, params, HttpMethod.GET, callback)
        request.executeAsync()
    }


    @Override
    override fun onCompleted(user: AlbbGetUserCallback.User?) {

        var username = user?.name
        var email = user?.email ?: ""
        var id = user?.id
        AlbbLogUtils.e("name:" + username + "email:" + email + "id:" + id)
        //ToastUtils.show("name:" + username + "email:" + email + "id:" + id)
        requestForLoginFacebook(email)
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
            var intent = Intent(this,AlbbRegistActivity::class.java)
            startActivity(intent)
        }
        tv_forget_password.setOnClickListener {
            var intent = Intent(this, AlbbForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        bt_login.setOnClickListener {

            val username = et_user_name.text.toString().toLowerCase().trim()
            val password = et_password.text.toString().toLowerCase().trim()

            var isCheckUserName =  AlbbUtils.checkStringUsername(username)
            val isCheckPassword =  AlbbUtils.isPasswordCorrect(password)
            if (username.length < 3) {
                isCheckUserName = false
            }
            if (!isCheckUserName){
                ToastUtils.show("username is invalid")
            }
            else if (!isCheckPassword) {
                ToastUtils.show("Use 6 or more characters(combination of letters,numbers)")
            }

            if (isCheckUserName && isCheckPassword) {
                login(username,password)
            }
        }
        callbackManager = CallbackManager.Factory.create();

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {

        }

        login_facebook_button.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL,"public_profile"));
            LoginManager.getInstance().setAuthType(AUTH_TYPE)
           // loginByFaceBook()
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                AlbbLogUtils.d("DjiLoginActivity login success" + loginResult?.accessToken)
               // ToastUtils.show("facebook login success")
                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                if (!isLoggedIn) {
                   return
                   // login_button_fb.unregisterCallback(callbackManager)
                }
                getFacebookUserinfo()
            }

            override fun onCancel() {
                AlbbLogUtils.d("DjiLoginActivity login cancel" )
                // App code
            }
            override fun onError(exception: FacebookException) {

                AlbbLogUtils.d("DjiLoginActivity login error" + exception )

                // App code
            }
        })


    }


    private fun login(username:String, password:String) {

        val request = AlbbUsernameLoginRequest()
        request.deviceBrand = AlbbAppConfigData.deviceBrand
        request.deviceId = AlbbAppConfigData.deviceId
        request.username = username
        request.password = AlbbUtils.md5Encode(password)
        AlbbApiClient.instance.serviceAlbb.login(request)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this,ActivityEvent.DESTROY)
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
                        }                    }
                })

    }

    private fun requestForLoginFacebook(username:String) {

        AlbbApiClient.instance.serviceAlbb.loginByFacebook(username)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this,ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbLoginResponseAlbb>(this,true){
                    override fun businessFail(data: AlbbLoginResponseAlbb) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbLoginResponseAlbb) {
                        if (data.data != null) {
                            Handler().postDelayed({
                                goSuccess(data)
                            },1500)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }                    }
                })

    }



    private fun goSuccess(data:AlbbLoginResponseAlbb) {

        AlbbAppConfigData.initAuthorization(data.data.token,data.data.tokenHead)
        AlbbAppConfigData.albbCustomerInfo = data.data
        AlbbAppConfigData.loginName = data.data.username
        AlbbAppConfigData.password = data.data.password ?: ""
        AlbbAppConfigData.loginToken = data.data.token

        var intent = intent.setClass(this,AlbbMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}



