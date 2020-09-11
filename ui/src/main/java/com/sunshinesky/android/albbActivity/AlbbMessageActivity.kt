package com.sunshinesky.android.albbActivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.R
import com.sunshinesky.android.albbWidget.adapter.AlbbMessageAdapter
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbMessageInfoResonse
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.albb_message_activity.*

import com.scwang.smartrefresh.layout.header.ClassicsHeader


class AlbbMessageActivity : AlbbBaseActivity() {

    private var adapterAlbb : AlbbMessageAdapter? = null

    private var dataList : ArrayList<AlbbMessageInfoResonse.MessageObj>? = null

    private var refreshTag = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClassicsHeader.REFRESH_HEADER_UPDATE = "'Last update' M-d HH:mm";
        initData()
        initView()
    }

    override fun getLayoutId(): Int {

        return  R.layout.albb_message_activity
    }

    fun initData() {

        dataList = ArrayList<AlbbMessageInfoResonse.MessageObj>()
        //"createTime":"2020-07-08T12:55:30.000+0000"
       // requestData()
    }
    fun initView() {

        smt_refresh_layout.setOnRefreshListener {
            refreshTag = 1
            requestData()
        }
        smt_refresh_layout.setOnLoadMoreListener {
            refreshTag ++
            requestData()
        }
        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_message.setLayoutManager(manager);
        var listData = ArrayList<AlbbMessageInfoResonse.MessageObj>()
         adapterAlbb =  AlbbMessageAdapter(listData);
        rv_message.setAdapter(adapterAlbb)
        adapterAlbb?.notifyDataSetChanged()

        smt_refresh_layout.autoRefresh(1000)

    }

    private fun requestData() {

        AlbbApiClient.instance.serviceAlbb.getMessage(refreshTag,20)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbMessageInfoResonse>(this,true){
                    override fun businessFail(data: AlbbMessageInfoResonse) {
                        smt_refresh_layout.finishLoadMore()
                        smt_refresh_layout.finishRefresh()
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbMessageInfoResonse) {
                        smt_refresh_layout.finishLoadMore()
                        smt_refresh_layout.finishRefresh()

                        if (data.data?.size ?: 0 > 0) {
                            if (refreshTag == 1) {
                                dataList?.clear()
                                dataList?.addAll(data.data!!)
                                refreshData()
                            } else {
                                dataList?.addAll(data.data!!)
                                refreshData()
                            }
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        smt_refresh_layout.finishLoadMore()
                        smt_refresh_layout.finishRefresh()
                        if (this != null) {
                            ToastUtils.show(albbApiErrorModel.message)
                        }                    }
                })
    }

    private  fun refreshData() {
        if (dataList?.size ?:0 > 0 ) {
            adapterAlbb?.setDataList(dataList!!)
        }
    }

}