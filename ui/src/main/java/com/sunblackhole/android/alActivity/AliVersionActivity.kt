package com.sunblackhole.android.alActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.R
import com.sunblackhole.android.alWidget.UpdateDialog
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.response.UpdateResponse
import com.sunblackhole.android.alutils.Utils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.ali_version_activity.*

class AliVersionActivity : AliBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()
        checkupVersion()
    }

    override fun getLayoutId(): Int {

        return R.layout.ali_version_activity
    }


    private fun initData() {

        var v =  Utils.getVersion()
        var newV = v
        if (v.length > 6) {
            newV =  v.substring(0, v.length - 6)
        }
        tv_version_name.text = "v" + newV

    }

    private fun initListener() {

        cl_update_version.setOnClickListener {

            checkupVersion()
        }

    }

    private fun checkupVersion() {
        var v =  Utils.getVersion()
        var newV = v
        if (v.length > 6) {
            newV =  v.substring(0, v.length - 6)
        }
        ApiClient.instance.service.checkVersion(newV,"1")
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<UpdateResponse>(this,true){
                    override fun businessFail(data: UpdateResponse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: UpdateResponse) {
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

    private fun goSuccess(updateResponse: UpdateResponse) {
        if (updateResponse.data?.mustUpdate == 0) {
            ToastUtils.show("The current version is already the latest")
        } else if (updateResponse.data?.mustUpdate ?:0 > 0) {
           var updateDialog = UpdateDialog()
            updateDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
            updateDialog.show(this?.supportFragmentManager,"updateflag")
            updateDialog.updateData(updateResponse.data)
        }
    }

}