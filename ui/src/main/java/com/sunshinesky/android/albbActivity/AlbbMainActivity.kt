

package com.sunshinesky.android.albbActivity

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
import com.sunshinesky.android.R
import com.sunshinesky.android.AlbbFragment.AlbbToolbarFragment
import com.sunshinesky.android.AlbbFragment.AlbbTunnelConnectFragment
import com.sunshinesky.android.albbWidget.AlbbShareToFriendsDialog
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbWireguardListResponseAlbb

import com.sunshinesky.android.util.AlbbSingleInstanceActivity
import kotlinx.android.synthetic.main.albb_main_activity.*

class AlbbMainActivity : RxAppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "ShadowsocksMainActivity"
        private const val REQUEST_CONNECT = 1
    }

    internal lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private var albbTunnelConnectFragment: AlbbTunnelConnectFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlbbSingleInstanceActivity.register(this) ?: return
        setContentView(R.layout.albb_main_activity)
        drawer = findViewById(R.id.drawer_layout)
        drawer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        navigation = findViewById(R.id.navigation)
        navigation.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
          //  navigation.menu.findItem(R.id.profiles).isChecked = true
            if (albbTunnelConnectFragment == null) {
                albbTunnelConnectFragment = AlbbTunnelConnectFragment()
            }
            displayFragment(AlbbTunnelConnectFragment())
        }
       // loadData()
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
                    val aliSettingActivity = Intent(this, AlbbSettingActivity::class.java)
                    startActivity(aliSettingActivity)
                }
            }
            drawer.closeDrawers()
            true
        }
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
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        ToastUtils.show(albbApiErrorModel.message)
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


    private fun displayFragment(fragmentAlbb: AlbbToolbarFragment) {
      //  supportFragmentManager.beginTransaction().replace(R.id.activity_main_fl, fragment).commitAllowingStateLoss()

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.activity_main_fl, fragmentAlbb)
        ft.commit()
        drawer.closeDrawers()
    }

    fun initListener() {
        ll_sign_out.setOnClickListener {
            AlbbAppConfigData.loginToken = ""
            AlbbAppConfigData.albbCustomerInfo = null;
            val intent = Intent(this, AlbbLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_friends -> {
                var shareToFriendsDialog = AlbbShareToFriendsDialog()
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
               val aliVersionActivity = Intent(this,AlbbVersionActivity::class.java)
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