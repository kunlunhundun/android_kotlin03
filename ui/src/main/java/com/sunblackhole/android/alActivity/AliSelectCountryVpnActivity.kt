/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunblackhole.android.R
import com.sunblackhole.android.alModel.SelectWireguardEvent
import com.sunblackhole.android.alModel.VpnPointModel
import com.sunblackhole.android.alWidget.adapter.VpnPointItemAdapter
import com.sunblackhole.android.aliData.AppConfigData
import kotlinx.android.synthetic.main.ali_select_country_vpn_activity.*
import org.greenrobot.eventbus.EventBus

class AliSelectCountryVpnActivity : AliBaseActivity() {

    var mListData: ArrayList<VpnPointModel> = ArrayList()
    lateinit var vpnPointAdapter: VpnPointItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }
    override fun getLayoutId(): Int {
        return R.layout.ali_select_country_vpn_activity
    }

    fun initView() {
        setTile("VPN locations")
        var vpnPointModel = VpnPointModel("icon_china_flag","china")
        var vpnPointModel1 = VpnPointModel("icon_china_flag","USA")

      //  mListData.add(vpnPointModel)

        if (AppConfigData.wireguardList != null) {
            for (item in AppConfigData.wireguardList!!) {
                var model = VpnPointModel("icon_china_flag","china")
                when {
                    (item.lineName!!.toLowerCase().contains("usa") == true) -> {
                        model = VpnPointModel("icon_usa_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("indonesia") == true) -> {
                        model = VpnPointModel("icon_indonesia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("india") == true) -> {
                        model = VpnPointModel("icon_india_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("hong kong") == true) -> {
                        model = VpnPointModel("icon_hk_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("china") == true) -> {
                        model = VpnPointModel("icon_china_flag",item.lineName!!)
                    }
                }
                mListData.add(model)
            }
        }
        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_vpn_point.setLayoutManager(manager);
        val addapter =  VpnPointItemAdapter(mListData);
        rv_vpn_point.setAdapter(addapter);
        vpnPointAdapter = addapter
        addapter.onItemClickLister = object : VpnPointItemAdapter.OnItemClickLister {
            override fun onItemClick(position: Int) {
                //refreshAdapter(position)
                val event = SelectWireguardEvent()
                var model = mListData.get(position)
                event.index = position
                event.icon_flag = model.icon
                EventBus.getDefault().post(event)
                finish()
            }
        }
        addapter.notifyDataSetChanged()
    }
}