package com.wireguard.android.alActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wireguard.android.R
import com.wireguard.android.alWidget.adapter.MessageAdapter
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.response.MessageInfoResonse
import com.wireguard.android.alutils.ToastUtils
import kotlinx.android.synthetic.main.ali_message_activity.*

class AliMessageActivity : AliBaseActivity() {

    private var adapter : MessageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
    }

    override fun getLayoutId(): Int {

        return  R.layout.ali_message_activity
    }

    fun initData() {

        ApiClient.instance.service.getMessage(1,20)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<MessageInfoResonse>(this,true){
                    override fun businessFail(data: MessageInfoResonse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: MessageInfoResonse) {
                        ToastUtils.show("send success!")
                        refreshData(data)
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                            ToastUtils.show(apiErrorModel.message)
                        }                    }
                })


    }
    fun initView() {

        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_message.setLayoutManager(manager);
        var listData = ArrayList<MessageInfoResonse.MessageObj>()
         adapter =  MessageAdapter(listData);
        rv_message.setAdapter(adapter)
        adapter?.notifyDataSetChanged()
    }

    private  fun refreshData(body:MessageInfoResonse) {

        if (body.data?.size ?: 0 > 0 ) {
            adapter?.setDataList(body.data!!)
        }
    }


}