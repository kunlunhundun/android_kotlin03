package com.wireguard.android.alActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wireguard.android.R
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.response.BaseResponseObject
import com.wireguard.android.alutils.ToastUtils
import kotlinx.android.synthetic.main.ali_custom_toolbar.view.*
import kotlinx.android.synthetic.main.ali_feed_back_activity.*

class AliFeedBackActivity : AliBaseActivity() {

    private var type = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initListener()
    }

    override fun getLayoutId(): Int {
        return R.layout.ali_feed_back_activity
    }

    private fun initData() {

        setHasRightLogo(true)


    }

    private fun initListener() {

        tool_bar.iv_right_logo.setOnClickListener {
            var intent = Intent(this, AliMessageActivity::class.java)
            startActivity(intent)
        }

        cl_func_item.setOnClickListener {
            type = 0
            ck_func_item.isChecked = true
            ck_optimization_item.isChecked = false
            ck_other_item.isChecked = false
        }
        cl_optimization_item.setOnClickListener {
            type = 1
            ck_func_item.isChecked = false
            ck_optimization_item.isChecked = true
            ck_other_item.isChecked = false
        }
        cl_other_item.setOnClickListener {
            type = 2
            ck_func_item.isChecked = false
            ck_optimization_item.isChecked = false
            ck_other_item.isChecked = true
        }

        btn_feed_back.setOnClickListener {
            var content = et_feedback_content.text.toString().trim()
            if (content.length < 5) {
                ToastUtils.show("please input more 5 character")
            } else {
                //sendFeedBack
                ApiClient.instance.service.sendFeedBack(type,content)
                        .compose(NetworkScheduler.compose())
                        .bindUntilEvent(this, ActivityEvent.DESTROY)
                        .subscribe(object : ApiResponse<BaseResponseObject>(this,true){
                            override fun businessFail(data: BaseResponseObject) {
                                ToastUtils.show(data.message ?: "")
                            }
                            override fun businessSuccess(data: BaseResponseObject) {
                                ToastUtils.show("send success!")
                            }
                            override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                                if (this != null) {
                                    ToastUtils.show(apiErrorModel.message)
                                }                    }
                        })
            }
        }

    }


}