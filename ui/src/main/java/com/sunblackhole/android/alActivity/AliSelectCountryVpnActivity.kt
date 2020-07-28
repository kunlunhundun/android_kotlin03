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
        setTile("VPN Locations")
        var vpnPointModel = VpnPointModel("icon_china_flag","china")
      //  mListData.add(vpnPointModel)

        if (AppConfigData.wireguardList != null) {
            for (item in AppConfigData.wireguardList!!) {
                var model = VpnPointModel("icon_usa_flag",item.lineName!!)
                when {
                    (item.lineName!!.toLowerCase().contains("usa") == true) -> {
                        model = VpnPointModel("icon_usa_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("indonesia") == true) -> {
                        model = VpnPointModel("icon_indonesia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("hong kong") == true) -> {
                        model = VpnPointModel("icon_hk_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("japan") == true) -> {
                        model = VpnPointModel("icon_japan_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("uk") == true) -> {
                        model = VpnPointModel("icon_england_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("germany") == true) -> {
                        model = VpnPointModel("icon_germany_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("india") == true) -> {
                        model = VpnPointModel("icon_india_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("korea") == true) -> {
                        model = VpnPointModel("icon_korea_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("france") == true) -> {
                        model = VpnPointModel("icon_french_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("singapore") == true) -> {
                        model = VpnPointModel("icon_singapore_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("russia") == true) -> {
                        model = VpnPointModel("icon_russian_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("sweden") == true) -> {
                        model = VpnPointModel("icon_sweden_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("switzerland") == true) -> {
                        model = VpnPointModel("icon_switzerland_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("turkey") == true) -> {
                        model = VpnPointModel("icon_turkey_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("italy") == true) -> {
                        model = VpnPointModel("icon_italy_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("finland") == true) -> {
                        model = VpnPointModel("icon_finland_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("luxembourg") == true) -> {
                        model = VpnPointModel("icon_luxembourg_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("taiwan") == true) -> {
                        model = VpnPointModel("icon_china_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("saudiarabia") == true) -> {
                        model = VpnPointModel("icon_saudiarabia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("australia") == true) -> {
                        model = VpnPointModel("icon_australia_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("canada") == true) -> {
                        model = VpnPointModel("icon_canada_flag",item.lineName!!)
                    }
                    (item.lineName!!.toLowerCase().contains("netherlands") == true ||
                            item.lineName!!.toLowerCase().contains("holland") == true ) -> {
                        model = VpnPointModel("icon_netherlands_flag",item.lineName!!)
                    }
                    //
                    (item.lineName!!.toLowerCase().contains("china") == true) -> {
                        model = VpnPointModel("icon_china_flag",item.lineName!!)
                    }
                    //SaudiArabia
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