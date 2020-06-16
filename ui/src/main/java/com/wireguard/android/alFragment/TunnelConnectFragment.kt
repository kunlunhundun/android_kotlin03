/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.wireguard.android.Application
import com.wireguard.android.Application.Companion.CACHE_ALLOW_APP_FLAG
import com.wireguard.android.R
import com.wireguard.android.alActivity.AliMainActivity
import com.wireguard.android.alActivity.AliSelectCountryVpnActivity
import com.wireguard.android.alInterface.OnListenerObservableTunnel
import com.wireguard.android.alModel.ReconnectTunnelEvent
import com.wireguard.android.alModel.SelectWireguardEvent
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.response.WireguardListResponse
import com.wireguard.android.alutils.LogUtils
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.android.model.ObservableTunnel
import com.wireguard.android.util.ErrorMessages
import com.wireguard.config.Config
import kotlinx.android.synthetic.main.ali_custom_toolbar.*
import kotlinx.android.synthetic.main.ali_tunnel_connect_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets


class TunnelConnectFragment : ToolbarFragment(),OnListenerObservableTunnel{

    private var pendingTunnel: ObservableTunnel? = null
    private var pendingTunnelUp: Boolean? = null
    protected var selectedTunnel: ObservableTunnel? = null
    private  var currentConfig: Config? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.ali_tunnel_connect_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        (activity as AliMainActivity).setSupportActionBar(toolbar)
        val actionBar: androidx.appcompat.app.ActionBar? = (activity as AliMainActivity).getSupportActionBar()
//        //菜单按钮可用
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false);
        toolbar_title.text = "";
        toolbar_icon.visibility = View.GONE;
        //toolbar_icon.setImageResource(R.drawable.ic_navigation_menu);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_menu)
        toolbar.setNavigationOnClickListener { (activity as AliMainActivity).drawer.openDrawer(GravityCompat.START) }
       // toolbar.inflateMenu(R.menu.about_menu)
       // toolbar.setOnMenuItemClickListener(this)
        EventBus.getDefault().register(this)
        setListener()
        initView()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun initView() {
        tv_expire_days.text = "Free Trial expires in " + AppConfigData.customerInfo?.expireDay + " days"
    }

    private fun setListener() {

        btn_tunnel_connect.setOnClickListener {
            if (selectedTunnel == null) {
                return@setOnClickListener
            }
            val checked = if( selectedTunnel?.getDataState() == Tunnel.State.UP) false else true
            if (checked) tv_connect_state.text = "connecting...."
            setTunnelState(checked)
        }
        cl_select_line.setOnClickListener {

            val intent = Intent(context,AliSelectCountryVpnActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initWireguardData(event:SelectWireguardEvent) {

      /*  val configText = "[Interface]\nPrivateKey = 0HvBmNS79bH8DTehScAsBznDlxRMDNKgShTIN6tYemU=\n" +
                "Address = 10.77.77.2/32\nDNS = 8.8.8.8\nMTU = 1420\n" +
                "[Peer]\nPublicKey = fShlhFxOtwwBP5wL8RfvLFloiQL4WkZ6e3e1RYqrAnQ=\nEndpoint = 121.196.120.24:33649\n" +
                "AllowedIPs = 0.0.0.0/0, ::0/0\nPersistentKeepalive = 25" */
        val checked = if( selectedTunnel?.getDataState() == Tunnel.State.UP) false else true
        if (checked == false) {
            setTunnelState(checked)
        }
        var wireguardData = AppConfigData.wireguardList?.get(event.index)
        if (wireguardData == null) {
            return
        }

        tv_line_name.text = wireguardData.lineName
        val icon = requireContext().resources.getIdentifier(event.icon_flag, "mipmap", requireContext().packageName)
        iv_country_flag.setImageResource(icon)
        if (selectedTunnel == null) {
            tv_connect_state.setText("connecting....")
        } else {
            tv_connect_state.setText("disconnecting")
        }
        Handler().postDelayed(Runnable {
            if (selectedTunnel != null) {
                Application.getTunnelManager().delete(selectedTunnel!!)
                selectedTunnel = null
            }
            tv_connect_state.setText("connecting....")
            startConnectWireGuard(wireguardData)

        },2000)

    }

    private fun startConnectWireGuard(wireguardData: WireguardListResponse.VpnServiceObject) {

        /*val configText = "[Interface]\nPrivateKey = 0HvBmNS79bH8DTehScAsBznDlxRMDNKgShTIN6tYemU=\n" +
                "Address = 10.77.77.2/32\nDNS = 8.8.8.8\nMTU = 1420\n" +
                "[Peer]\nPublicKey = fShlhFxOtwwBP5wL8RfvLFloiQL4WkZ6e3e1RYqrAnQ=\nEndpoint = 121.196.120.24:33649\n" +
                "AllowedIPs = 0.0.0.0/0, ::0/0\nPersistentKeepalive = 25" */

        var lineName = wireguardData?.lineName ?: ""
        var vpnModel = wireguardData?.wireguards?.get(0);
        var configText = "[Interface]\n" + "PrivateKey = " + vpnModel?.privatekey + "\n" +
                "Address = " + vpnModel?.address + "\n" +"DNS = " + vpnModel?.dns +  "\n" +
                "MTU = " + vpnModel?.mtu + "\n" +
                "[Peer]\n" + "PublicKey = " + vpnModel?.publickey + "\n" +
                "Endpoint = " + vpnModel?.endpoint + "\n" + "AllowedIPs = " + vpnModel?.allowedIps + "\n" +
                "PersistentKeepalive = " + vpnModel?.persistentKeepalive

        val config = Config.parse(ByteArrayInputStream(configText.toByteArray(StandardCharsets.UTF_8)))
        currentConfig = config
        var random = (Math.random()*9+1)*1000
        val name = "tianya" + random.toLong()
        Application.getTunnelManager().create(name, config).whenComplete { tunnel, throwable ->
            if (tunnel != null) {
                setCurrentTunnul(tunnel)
                setConfigApp()
                btn_tunnel_connect.performClick()
            } else {
                findTunnelByTunnelName(name)
            }
        }

    }

    private  fun findTunnelByTunnelName(tunnelName: String) {
        Application.getTunnelManager()
                .tunnels
                .thenAccept {
                    setCurrentTunnul(it[tunnelName])
                }
    }


    private fun setCurrentTunnul(tunnel: ObservableTunnel?) {
        selectedTunnel = tunnel
        if (currentConfig != null) {
            selectedTunnel?.setConfigAsync(currentConfig!!)
        }
        selectedTunnel?.setOnListenerCallBack(this)
    }

    override fun onStateChanged(newState: Tunnel.State?) {
        LogUtils.d("tunnelconnectfragment: onstatechanged:" + newState)
        if (newState == Tunnel.State.DOWN) {
            tv_connect_state.text = "disconnect"
            btn_tunnel_connect.setBackgroundResource(R.mipmap.icon_vpn_connect)
            cl_connect_head.setBackgroundColor(resources.getColor(R.color.bg_blue_color) )
            cl_free_trial.setBackgroundColor(resources.getColor(R.color.bg_light_blue_color) )
        } else if (newState == Tunnel.State.UP) {
            tv_connect_state.text = "connected"
            btn_tunnel_connect.setBackgroundResource(R.mipmap.icon_vpn_connected)
            cl_connect_head.setBackgroundColor(resources.getColor(R.color.bg_green_color) )
            cl_free_trial.setBackgroundColor(resources.getColor(R.color.bg_light_green_color) )
        }
    }

   private fun setConfigApp() {
       var applicationsSet:MutableSet<String> =  mutableSetOf()
       val appFlag =  Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"
       if (appFlag.toInt() == 2) {
           var excludeApps = Application.getExcludeAppList()
           excludeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               currentConfig?.`interface`
              // val builder = configTunnel?.`interface`.
               currentConfig?.`interface`?.excludedApplications?.addAll(applicationsSet)
           }
       }
       if (appFlag.toInt() == 3) {
           var includeApps = Application.getIncludeAppList()
           includeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               currentConfig?.`interface`?.includedApplications?.addAll(applicationsSet)
               var size = currentConfig?.`interface`?.includedApplications?.size
               LogUtils.d("size:--->" + size)
           }
       }
   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_VPN_PERMISSION) {
            if (pendingTunnel != null && pendingTunnelUp != null) setTunnelStateWithPermissionsResult(pendingTunnel!!, pendingTunnelUp!!)
            pendingTunnel = null
            pendingTunnelUp = null
        }

    }

    fun setTunnelState(checked: Boolean) {
        val tunnel = selectedTunnel
        Application.getBackendAsync().thenAccept { backend: Backend? ->
            if (backend is GoBackend) {
                val intent = GoBackend.VpnService.prepare(this.context)
                if (intent != null) {
                    pendingTunnel = tunnel
                    pendingTunnelUp = checked
                    startActivityForResult(intent, REQUEST_CODE_VPN_PERMISSION)
                    return@thenAccept
                }
            }
            setTunnelStateWithPermissionsResult(tunnel!!, checked)
        }
    }

    private fun setTunnelStateWithPermissionsResult(tunnel: ObservableTunnel, checked: Boolean) {
        tunnel.setStateAsync(Tunnel.State.of(checked)).whenComplete { _, throwable ->
            if (throwable == null) return@whenComplete
            val error = ErrorMessages[throwable]
            val messageResId = if (checked) R.string.error_up else R.string.error_down
            val message = requireContext().getString(messageResId, error)
         //   Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            LogUtils.e(TAG, message)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event : SelectWireguardEvent) {
        val handler = Handler(Looper.getMainLooper())
        var runnable = Runnable {
            initWireguardData(event)
        }
        //btn_tunnel_connect.setText("connecting")
        handler.postDelayed(runnable,1000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event : ReconnectTunnelEvent) {
        val handler = Handler()
        var runnable = Runnable {
            if (Application.get().isNeedConnectByModifyAppFlag) {
                if( selectedTunnel?.getDataState() == Tunnel.State.UP) {
                    setTunnelState(false) // 停止
                    Handler().postDelayed( Runnable {
                        setConfigApp()
                        if (selectedTunnel?.getDataState() != Tunnel.State.UP) {
                            setTunnelState(true)
                        }
                    },1500)

                }
            }
        }
        handler.postDelayed(runnable,2000)
        //finish()
    }


    companion object {
        private const val REQUEST_CODE_VPN_PERMISSION = 23491
        private const val TAG = "WireGuard/tunnelConnectFragment"

    }
}