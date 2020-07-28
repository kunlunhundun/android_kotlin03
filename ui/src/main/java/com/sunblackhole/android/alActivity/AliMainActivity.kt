/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.DialogFragment
import com.hjq.toast.ToastUtils
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import com.sunblackhole.android.R
import com.sunblackhole.android.alFragment.ToolbarFragment
import com.sunblackhole.android.alFragment.TunnelConnectFragment
import com.sunblackhole.android.alWidget.ShareToFriendsDialog
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.aliData.net.ApiErrorModel
import com.sunblackhole.android.aliData.net.ApiResponse
import com.sunblackhole.android.aliData.net.NetworkScheduler
import com.sunblackhole.android.aliData.response.WireguardListResponse

import com.sunblackhole.android.util.SingleInstanceActivity
import kotlinx.android.synthetic.main.ali_main_activity.*

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
                var shareToFriendsDialog = ShareToFriendsDialog()
                shareToFriendsDialog.dialog?.setCancelable(true);
                shareToFriendsDialog.dialog?.setCanceledOnTouchOutside(true)
                shareToFriendsDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyPopupWindow_anim_style);//添加上面创建的style
                shareToFriendsDialog.show(this?.supportFragmentManager,"sharetofriends")
                    // shareToFriendsDialog.setCanceledOnTouchOutside(true)
                   // shareToFriendsDialog.dialog?.setCancelable(true);
                //val aliAppFilterActivity = Intent(this, AliAppFilterActivity::class.java)
               // startActivity(aliAppFilterActivity)
            }
            R.id.update_app -> {
               val aliVersionActivity = Intent(this,AliVersionActivity::class.java)
                startActivity(aliVersionActivity)
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