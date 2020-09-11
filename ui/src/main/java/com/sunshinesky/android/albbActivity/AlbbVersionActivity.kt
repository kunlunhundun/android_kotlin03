package com.sunshinesky.android.albbActivity

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.R
import com.sunshinesky.android.albbWidget.AlbbUpdateDialog
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbUpdateResponse
import com.sunshinesky.android.albbUtils.AlbbUtils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.albb_version_activity.*

class AlbbVersionActivity : AlbbBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()
        checkupVersion()
    }

    override fun getLayoutId(): Int {

        return R.layout.albb_version_activity
    }


    private fun initData() {

        var v =  AlbbUtils.getVersion()
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
        var v =  AlbbUtils.getVersion()
        var newV = v
        if (v.length > 6) {
            newV =  v.substring(0, v.length - 6)
        }
        AlbbApiClient.instance.serviceAlbb.checkVersion(newV,"1")
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbUpdateResponse>(this,true){
                    override fun businessFail(data: AlbbUpdateResponse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbUpdateResponse) {
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

    private fun goSuccess(updateResponse: AlbbUpdateResponse) {
        if (updateResponse.data?.mustUpdate == 0) {
            ToastUtils.show("The current version is already the latest")
        } else if (updateResponse.data?.mustUpdate ?:0 > 0) {
           var updateDialog = AlbbUpdateDialog()
            updateDialog.mUpdateObj = updateResponse.data
            updateDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
            updateDialog.show(this?.supportFragmentManager,"updateflag")
        }
    }

}