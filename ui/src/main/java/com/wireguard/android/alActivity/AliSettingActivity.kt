/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity
import android.os.Bundle
import android.widget.Toast
import com.wireguard.android.R
import kotlinx.android.synthetic.main.ali_setting_activity.*

class AliSettingActivity : AliBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.ali_setting_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTile("设置")
        initView()
    }

    private fun initView() {
        bt_set_profile.setOnClickListener {

            Toast.makeText(this,"dkdkkd",Toast.LENGTH_SHORT).show()
        }
    }

}