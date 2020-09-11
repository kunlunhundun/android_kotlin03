/*
 */

package com.sunshinesky.android.albbActivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.R
import com.sunshinesky.android.albbModel.AlbbSelectWireguardEvent
import com.sunshinesky.android.albbModel.AlbbVpnPointModel
import com.sunshinesky.android.albbWidget.adapter.AlbbVpnPointItemAdapter
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbWireguardListResponseAlbb
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.albb_select_country_vpn_activity.*
import org.greenrobot.eventbus.EventBus

class AlbbSelectCountryVpnActivity : AlbbBaseActivity() {

    var mListData: ArrayList<AlbbVpnPointModel> = ArrayList()
    lateinit var albbVpnPointAdapter: AlbbVpnPointItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        loadData()
    }
    override fun getLayoutId(): Int {
        return R.layout.albb_select_country_vpn_activity
    }

    fun initView() {
        setTile("VPN Locations")
        var vpnPointModel = AlbbVpnPointModel("icon_new_china_flag","china")
      //  mListData.add(vpnPointModel)

        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_vpn_point.setLayoutManager(manager);
        val addapter =  AlbbVpnPointItemAdapter(mListData);
        rv_vpn_point.setAdapter(addapter);
        albbVpnPointAdapter = addapter
        addapter.onItemClickLister = object : AlbbVpnPointItemAdapter.OnItemClickLister {
            override fun onItemClick(position: Int) {
                //refreshAdapter(position)
                val event = AlbbSelectWireguardEvent()
                var model = mListData.get(position)
                event.index = position
                event.icon_flag = model.icon
                EventBus.getDefault().post(event)
                finish()
            }
        }
        addapter.notifyDataSetChanged()
    }

    fun refreshData() {

        if (AlbbAppConfigData.wireguardList != null) {

            for (item in AlbbAppConfigData.wireguardList!!) {
                var model = AlbbVpnPointModel("icon_new_usa_flag",item.lineName!!)

                when {
                    (item.lineName!!.toLowerCase().contains("tik") == true) -> {
                        model = AlbbVpnPointModel("icon_new_tiktok",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("usa") == true) -> {
                        model = AlbbVpnPointModel("icon_new_usa_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("indonesia") == true) -> {
                        model = AlbbVpnPointModel("icon_new_indonesia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("hong kong") == true) -> {
                        model = AlbbVpnPointModel("icon_new_hk_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("japan") == true) -> {
                        model = AlbbVpnPointModel("icon_new_japan_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("uk") == true) -> {
                        model = AlbbVpnPointModel("icon_new_england_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("germany") == true) -> {
                        model = AlbbVpnPointModel("icon_new_germany_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("india") == true) -> {
                        model = AlbbVpnPointModel("icon_new_india_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("korea") == true) -> {
                        model = AlbbVpnPointModel("icon_new_korea_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("france") == true) -> {
                        model = AlbbVpnPointModel("icon_new_french_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("singapore") == true) -> {
                        model = AlbbVpnPointModel("icon_new_singapore_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("russia") == true) -> {
                        model = AlbbVpnPointModel("icon_new_russian_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("sweden") == true) -> {
                        model = AlbbVpnPointModel("icon_new_sweden_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("switzerland") == true) -> {
                        model = AlbbVpnPointModel("icon_new_switzerland_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("turkey") == true) -> {
                        model = AlbbVpnPointModel("icon_new_turkey_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("italy") == true) -> {
                        model = AlbbVpnPointModel("icon_new_italy_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("finland") == true) -> {
                        model = AlbbVpnPointModel("icon_new_finland_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("luxembourg") == true) -> {
                        model = AlbbVpnPointModel("icon_new_luxembourg_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("taiwan") == true) -> {
                        model = AlbbVpnPointModel("icon_new_china_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("saudiarabia") == true) -> {
                        model = AlbbVpnPointModel("icon_new_saudiarabia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("australia") == true) -> {
                        model = AlbbVpnPointModel("icon_new_australia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("canada") == true) -> {
                        model = AlbbVpnPointModel("icon_new_canada_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("netherlands") == true ||
                            item.lineName!!.toLowerCase().contains("holland") == true ) -> {
                        model = AlbbVpnPointModel("icon_new_netherlands_flag",item.lineName!!)
                    }
                    //
                    (item.lineName!!.toLowerCase().contains("china") == true) -> {
                        model = AlbbVpnPointModel("icon_new_china_flag",item.lineName!!)
                    }
                    //SaudiArabia
                }
                mListData.add(model)
            }
        }
        albbVpnPointAdapter.mList = mListData
        albbVpnPointAdapter.notifyDataSetChanged()
    }

    fun loadData() {

        AlbbApiClient.instance.serviceAlbb.getLineWireguard(AlbbAppConfigData.loginName!!)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbWireguardListResponseAlbb>(this,true){
                    override fun businessFail(data: AlbbWireguardListResponseAlbb) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbWireguardListResponseAlbb) {
                        if (data.data != null) {
                            var wireguardList =  data.data.wireguardList
                            AlbbAppConfigData.wireguardList = wireguardList;
                            refreshData()
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        ToastUtils.show(albbApiErrorModel.message)
                    }
                })
    }

}