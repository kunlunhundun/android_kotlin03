package com.supersunstars.android.DjiActivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.supersunstars.android.R
import com.supersunstars.android.DjiWidget.adapter.MessageAdapter
import com.supersunstars.android.DjiData.net.ApiClient
import com.supersunstars.android.DjiData.net.ApiErrorModel
import com.supersunstars.android.DjiData.net.ApiResponse
import com.supersunstars.android.DjiData.net.NetworkScheduler
import com.supersunstars.android.DjiData.response.MessageInfoResonse
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.dji_message_activity.*

import com.scwang.smartrefresh.layout.header.ClassicsHeader


class DjiMessageActivity : DjiBaseActivity() {

    private var adapter : MessageAdapter? = null

    private var dataList : ArrayList<MessageInfoResonse.MessageObj>? = null

    private var refreshTag = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClassicsHeader.REFRESH_HEADER_UPDATE = "'Last update' M-d HH:mm";
        initData()
        initView()
    }

    override fun getLayoutId(): Int {

        return  R.layout.dji_message_activity
    }

    fun initData() {

        dataList = ArrayList<MessageInfoResonse.MessageObj>()
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
        var listData = ArrayList<MessageInfoResonse.MessageObj>()
         adapter =  MessageAdapter(listData);
        rv_message.setAdapter(adapter)
        adapter?.notifyDataSetChanged()

        smt_refresh_layout.autoRefresh(1000)

    }

    private fun requestData() {

        ApiClient.instance.service.getMessage(refreshTag,20)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<MessageInfoResonse>(this,true){
                    override fun businessFail(data: MessageInfoResonse) {
                        smt_refresh_layout.finishLoadMore()
                        smt_refresh_layout.finishRefresh()
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: MessageInfoResonse) {
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
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        smt_refresh_layout.finishLoadMore()
                        smt_refresh_layout.finishRefresh()
                        if (this != null) {
                            ToastUtils.show(apiErrorModel.message)
                        }                    }
                })
    }

    private  fun refreshData() {
        if (dataList?.size ?:0 > 0 ) {
            adapter?.setDataList(dataList!!)
        }
    }

}