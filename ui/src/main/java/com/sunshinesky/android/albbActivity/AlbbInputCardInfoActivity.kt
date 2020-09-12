package com.sunshinesky.android.albbActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.R
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbMessageInfoResonse
import com.sunshinesky.android.albbData.response.AlbbUpdateResponse
import com.sunshinesky.android.albbUtils.AlbbUtils
import com.sunshinesky.android.albbWidget.adapter.AlbbMessageAdapter
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent

class AlbbInputCardInfoActivity : AlbbBaseActivity() {

    private var adapterAlbb : AlbbMessageAdapter? = null

    private var dataList : ArrayList<AlbbMessageInfoResonse.MessageObj>? = null

    private var refreshTag = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albb_input_card_info)

        initData()
    }

    override fun getLayoutId(): Int {
        return  R.layout.activity_albb_input_card_info;
    }

    fun initData() {

        dataList = ArrayList<AlbbMessageInfoResonse.MessageObj>()

        AlbbApiClient.instance.serviceAlbb.getMessage(refreshTag,20)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbMessageInfoResonse>(this,true){
                    override fun businessFail(data: AlbbMessageInfoResonse) {

                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbMessageInfoResonse) {


                        if (data.data?.size ?: 0 > 0) {
                            if (refreshTag == 1) {
                                dataList?.clear()
                                dataList?.addAll(data.data!!)

                            } else {
                                dataList?.addAll(data.data!!)

                            }
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {

                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }                    }
                })

        var v =  AlbbUtils.getVersion()
        var newV = v
        return
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

                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }
                    }
                })

    }

}