package com.supersunstars.android.DjiActivity

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hjq.toast.ToastUtils
import com.supersunstars.android.R
import com.supersunstars.android.DjiWidget.UpdateDialog
import com.supersunstars.android.DjiData.net.ApiClient
import com.supersunstars.android.DjiData.net.ApiErrorModel
import com.supersunstars.android.DjiData.net.ApiResponse
import com.supersunstars.android.DjiData.net.NetworkScheduler
import com.supersunstars.android.DjiData.response.UpdateResponse
import com.supersunstars.android.Djiutils.Utils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.dji_version_activity.*

class DjiVersionActivity : DjiBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()
        checkupVersion()
    }

    override fun getLayoutId(): Int {

        return R.layout.dji_version_activity
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
            updateDialog.mUpdateObj = updateResponse.data
            updateDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
            updateDialog.show(this?.supportFragmentManager,"updateflag")
        }
    }

}