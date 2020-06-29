/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity

import android.app.ActivityManager
import android.companion.CompanionDeviceManager
import android.content.Context
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.wireguard.android.R
import com.wireguard.android.activity.BaseActivity
import com.wireguard.android.alFragment.ToolbarFragment
import com.wireguard.android.alFragment.TunnelConnectFragment
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.net.ApiClient
import com.wireguard.android.aliData.net.ApiErrorModel
import com.wireguard.android.aliData.net.ApiResponse
import com.wireguard.android.aliData.net.NetworkScheduler
import com.wireguard.android.aliData.response.WireguardListResponse
import com.wireguard.android.aliData.response.WireguardListResponse.VpnServiceObject

import com.wireguard.android.alutils.ToastUtils
import com.wireguard.android.fragment.TunnelDetailFragment
import com.wireguard.android.fragment.TunnelListFragment
import com.wireguard.android.util.SingleInstanceActivity
import kotlinx.android.synthetic.main.ali_main_activity.*
import kotlinx.coroutines.newFixedThreadPoolContext

class AliMainActivity : RxAppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "ShadowsocksMainActivity"
        private const val REQUEST_CONNECT = 1
    }

    internal lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private var tunnelConnectFragment: TunnelConnectFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SingleInstanceActivity.register(this) ?: return
        setContentView(R.layout.ali_main_activity)
        drawer = findViewById(R.id.drawer_layout)
        drawer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        navigation = findViewById(R.id.navigation)
        navigation.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
          //  navigation.menu.findItem(R.id.profiles).isChecked = true
            if (tunnelConnectFragment == null) {
                tunnelConnectFragment = TunnelConnectFragment()
            }
            displayFragment(TunnelConnectFragment())
        }
        loadData()
        initListener()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            moveTaskToBack(true)
           // var am : ActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            //am.moveTaskToFront(taskId,ActivityManager.MOVE_TASK_WITH_HOME)
            false
        } else {
            super.onKeyDown(keyCode, event);
        }
    }


    fun setNavListener() {
        navigation.setNavigationItemSelectedListener {
            item: MenuItem ->
            when (item.itemId) {
                R.id.share_friends -> {
                    val aliSettingActivity = Intent(this, AliSettingActivity::class.java)
                    startActivity(aliSettingActivity)
                }
            }
            drawer.closeDrawers()
            true
        }
    }


    fun loadData() {

        ApiClient.instance.service.getLineWireguard(AppConfigData.loginName!!)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<WireguardListResponse>(this,true){
                    override fun businessFail(data: WireguardListResponse) {
                        ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: WireguardListResponse) {
                        if (data.data != null) {
                           var wireguardList =  data.data.wireguardList
                            AppConfigData.wireguardList = wireguardList;
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        ToastUtils.show(apiErrorModel.message)
                    }
                })
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
                    // menuInflater.inflate(R.menu.main_activity, menu)
                     return true
                 }

                 override fun onOptionsItemSelected(item: MenuItem): Boolean {
                     return when (item.itemId) {
                         android.R.id.home -> {
                             // The back arrow in the action bar should act the same as the back button.
                             onBackPressed()
                             true
                         }
                         R.id.menu_settings -> {
                           //  startActivity(Intent(this, SettingsActivity::class.java))
                             true
                         }
                         else -> super.onOptionsItemSelected(item)
                     }
                 } */


    private fun displayFragment(fragment: ToolbarFragment) {
      //  supportFragmentManager.beginTransaction().replace(R.id.activity_main_fl, fragment).commitAllowingStateLoss()

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.activity_main_fl, fragment)
        ft.commit()
        drawer.closeDrawers()
    }

    fun initListener() {
        ll_sign_out.setOnClickListener {
            AppConfigData.loginToken = ""
            AppConfigData.customerInfo = null;
            val intent = Intent(this, AliLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_friends -> {
                val aliAppFilterActivity = Intent(this, AliAppFilterActivity::class.java)
                startActivity(aliAppFilterActivity)
            }
            //AliAppFilterActivity
            else -> return false
        }
        item.isChecked = true
        return true
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}