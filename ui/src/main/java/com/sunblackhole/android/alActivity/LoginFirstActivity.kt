/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunblackhole.android.R
import kotlinx.android.synthetic.main.ali_login_first_activity.*

class LoginFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ali_login_first_activity)
        initView()

    }
    fun initView() {

        bt_free_trial.setOnClickListener {
            
            var intent = Intent(this, AliLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","1")
            startActivity(intent)
        }
        bt_sign_in.setOnClickListener {
            var intent = Intent(this, AliLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","2")
            startActivity(intent)
        }
    }
}