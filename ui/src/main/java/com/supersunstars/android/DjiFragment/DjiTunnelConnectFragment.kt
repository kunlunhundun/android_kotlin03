/*
 */

package com.supersunstars.android.DjiFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.githang.statusbar.StatusBarCompat
import com.hjq.toast.ToastUtils
import com.ljoy.chatbot.sdk.ELvaChatServiceSdk
import com.supersunstars.android.Application
import com.supersunstars.android.Application.Companion.CACHE_ALLOW_APP_FLAG
import com.supersunstars.android.R
import com.supersunstars.android.DjiActivity.DjiAppFilterActivity
import com.supersunstars.android.DjiActivity.DjiAppFilterActivity.Companion.FILTER_RESULT_OK
import com.supersunstars.android.DjiActivity.DjiFeedBackActivity
import com.supersunstars.android.DjiActivity.DjiMainActivity
import com.supersunstars.android.DjiActivity.DjiSelectCountryVpnActivity
import com.supersunstars.android.DjiInterface.OnListenerObservableTunnel
import com.supersunstars.android.DjiModel.AppPackageModel
import com.supersunstars.android.DjiModel.ReconnectTunnelEvent
import com.supersunstars.android.DjiModel.SelectWireguardEvent
import com.supersunstars.android.DjiWidget.adapter.HomeAppAdapter
import com.supersunstars.android.DjiData.AppConfigData
import com.supersunstars.android.DjiData.net.ApiClient
import com.supersunstars.android.DjiData.net.ApiErrorModel
import com.supersunstars.android.DjiData.net.ApiResponse
import com.supersunstars.android.DjiData.net.NetworkScheduler
import com.supersunstars.android.DjiData.response.BaseResponseObject
import com.supersunstars.android.DjiData.response.QueryReplyCountResponse
import com.supersunstars.android.DjiData.response.WireguardListResponse
import com.supersunstars.android.Djiutils.FrameAnimation
import com.supersunstars.android.Djiutils.LogUtils
import com.supersunstars.android.backend.Backend
import com.supersunstars.android.backend.GoBackend
import com.supersunstars.android.backend.Tunnel
import com.supersunstars.android.model.ObservableTunnel
import com.supersunstars.android.util.ErrorMessages
import com.supersunstars.config.Config
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets


class DjiTunnelConnectFragment : DjiToolbarFragment(),OnListenerObservableTunnel, FrameAnimation.AnimationListener {

    private var pendingTunnel: ObservableTunnel? = null
    private var pendingTunnelUp: Boolean? = null
    protected var selectedTunnel: ObservableTunnel? = null
    private var currentConfig: Config? = null

    private var frameAnimation: FrameAnimation? = null
    private var disconnectFrameAnimation: FrameAnimation? = null
    private var isConnecting: Boolean = false
    private var isLocked: Boolean = false

    private var curWireguardData: WireguardListResponse.VpnServiceObject? = null
    private var homeAppAdapter: HomeAppAdapter? = null

    private var toolbarTitle: TextView? = null
    private var toolbarIcon: ImageView? = null

    private var rv_app_home_item: RecyclerView? = null
    private var cl_select_app: ConstraintLayout? = null
    private var btn_tunnel_connect: Button? = null
    private var iv_logo_animation: ImageView? = null
    private var cl_select_line: ConstraintLayout? = null
    private var img_msg: ImageView? = null
    private var tv_line_name: TextView? = null
    private var iv_country_flag: ImageView? = null
    private var tv_all_app_filter: TextView? = null
    private var tv_select_app_filter: TextView? = null
    private var iv_flag_lock: ImageView? = null
    private var iv_app_lock: ImageView? = null
    private var iv_home_connect_toast: ImageView? = null
    private var tv_red_message: TextView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dji_tunnel_connect_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        (activity as DjiMainActivity).setSupportActionBar(toolbar)
        val actionBar: androidx.appcompat.app.ActionBar? = (activity as DjiMainActivity).getSupportActionBar()
//        //菜单按钮可用
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)

        if (this.context != null) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(),R.color.white))
            StatusBarCompat.setStatusBarColor(this.activity, ContextCompat.getColor(this.requireContext(),R.color.white))
            toolbarTitle?.setTextColor(ContextCompat.getColor(this.requireContext(),R.color.text_black_color))
        }
        toolbarIcon = toolbar.findViewById(R.id.toolbar_icon)

        rv_app_home_item = view.findViewById(R.id.rv_app_home_item)
        cl_select_app = view.findViewById(R.id.cl_select_app)
        btn_tunnel_connect = view.findViewById(R.id.btn_tunnel_connect)
        iv_logo_animation = view.findViewById(R.id.iv_logo_animation)
        cl_select_line = view.findViewById(R.id.cl_select_line)
        img_msg = view.findViewById(R.id.img_msg)
        tv_line_name = view.findViewById(R.id.tv_line_name)
        iv_country_flag = view.findViewById(R.id.iv_country_flag)
        tv_all_app_filter = view.findViewById(R.id.tv_all_app_filter)
        tv_select_app_filter = view.findViewById(R.id.tv_select_app_filter)
        iv_flag_lock = view.findViewById(R.id.iv_flag_lock)
        iv_app_lock = view.findViewById(R.id.iv_app_lock)
        iv_home_connect_toast = view.findViewById(R.id.iv_home_connect_toast)
        tv_red_message = view.findViewById(R.id.tv_red_message)

        toolbarTitle?.text = "Syrinx VPN"
        toolbarIcon?.visibility = View.GONE
        toolbar.setNavigationIcon(R.mipmap.icon_home_more)
        toolbar.setNavigationOnClickListener { (activity as DjiMainActivity).drawer.openDrawer(GravityCompat.START) }
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


    override fun onResume() {
        super.onResume()

        queryReplyCount()
    }

    private fun initView() {

        var manager = GridLayoutManager(context,6)
        rv_app_home_item?.layoutManager = manager
        var appList = ArrayList<AppPackageModel>()
        homeAppAdapter = HomeAppAdapter(appList)
        rv_app_home_item?.setAdapter(homeAppAdapter)

        rv_app_home_item?.setOnTouchListener(OnTouchListener { v, event ->
            if (event.getAction() == MotionEvent.ACTION_UP) {
                cl_select_app?.performClick();  //模拟父控件的点击
            }
            false
        })

        Handler().postDelayed(Runnable {
            configAPPItem()
        },3000)
    }


    private fun setListener() {

        btn_tunnel_connect?.setOnClickListener {

            if (selectedTunnel == null) {
                ToastUtils.show("please select line to connect")
                return@setOnClickListener
            }
            startAndStopWireGuard()

        }
        cl_select_line?.setOnClickListener {
            if (isLocked == true) {
                return@setOnClickListener
            }
            val intent = Intent(context,DjiSelectCountryVpnActivity::class.java)
            startActivity(intent)
        }
        cl_select_app?.setOnClickListener {

            if (isLocked == true) {
                return@setOnClickListener
            }
            val intent = Intent(context, DjiAppFilterActivity::class.java)
            startActivityForResult(intent, FILTER_RESULT_OK);
        }
        img_msg?.setOnClickListener {


            try {
                setInitCallback()
                // Init AIHelp SDK
                ELvaChatServiceSdk.init(this.requireActivity(),
                        "NOBB_app_f58cca0224a446b986c21b29cb7fb9ab",
                        "nobb@aihelp.net",
                        "nobb_platform_81637864-c68f-44a8-8431-d253bba5e448")
            } catch (e: java.lang.Exception) {
                LogUtils.e( "invalid init params : ")
            }

         //   var intent = Intent(context, DjiFeedBackActivity::class.java)
          //  startActivity(intent)
        }

    }

    // Before Init, set initializaiton callback method
    fun setInitCallback() {
        ELvaChatServiceSdk.setOnInitializedCallback(object : ELvaChatServiceSdk.OnInitializationCallback {
            override fun onInitialized() {
                println("AIHelp elva Initialization Done!")
            }
        })
    }

    private fun startAndStopWireGuard() {

        val checked = if( selectedTunnel?.getDataState() == Tunnel.State.UP) false else true
        if (isConnecting == true) {
            ToastUtils.show("it is connecting, please wait ")
            return
        }
        if (checked) {
            startConnectAnimation()
            isConnecting = true
            connectedRequest()
        } else {
            disconnectAnimation()
        }
        setTunnelState(checked)

    }


    private fun initWireguardData(event:SelectWireguardEvent) {

        var wireguardData = AppConfigData.wireguardList?.get(event.index)
        if (wireguardData == null) {
            return
        }
        curWireguardData = wireguardData
        tv_line_name?.text = wireguardData.lineName
        val icon = requireContext().resources.getIdentifier(event.icon_flag, "mipmap", requireContext().packageName)
        iv_country_flag?.setImageResource(icon)

        // 断开重连的那种
        val checked = if( selectedTunnel?.getDataState() == Tunnel.State.UP) false else true
        if (checked == false) { //已经连接，需先断开
            startAndStopWireGuard()
            Handler().postDelayed(Runnable {
                if (selectedTunnel != null) {
                    Application.getTunnelManager().delete(selectedTunnel!!)
                    selectedTunnel = null
                    startConnectAnimation()
                }
                startConnectWireGuard(wireguardData)

            },3500)
        } else { // 之前已经断开 或者 第一次连接
            startConnectAnimation()
            startConnectWireGuard(wireguardData)
        }

    }


    private fun startConnectWireGuard(wireguardData: WireguardListResponse.VpnServiceObject) {

        /*val configText = "[Interface]\nPrivateKey = 0HvBmNS79bH8DTehScAsBznDlxRMDNKgShTIN6tYemU=\n" +
                "Address = 10.77.77.2/32\nDNS = 8.8.8.8\nMTU = 1420\n" +
                "[Peer]\nPublicKey = fShlhFxOtwwBP5wL8RfvLFloiQL4WkZ6e3e1RYqrAnQ=\nEndpoint = 121.196.120.24:33649\n" +
                "AllowedIPs = 0.0.0.0/0, ::0/0\nPersistentKeepalive = 25" */

        var lineName = wireguardData?.lineName ?: ""
        var vpnModel = wireguardData?.wireguards?.get(0);
        var configText = "[Interface]\n" + "PrivateKey = " + vpnModel?.privatekey +  "\n" +
                "Address = " + vpnModel?.address + "\n" +"DNS = " + vpnModel?.dns +  "\n" +
                "MTU = " + vpnModel?.mtu + "\n" +
                "[Peer]\n" + "PublicKey = " + vpnModel?.publickey  + "\n" +
                "Endpoint = " + vpnModel?.endpoint + "\n" + "AllowedIPs = " + vpnModel?.allowedIps + "\n" +
                "PersistentKeepalive = " + vpnModel?.persistentKeepalive

        LogUtils.e( "linename---->" + configText)

        val config = Config.parse(ByteArrayInputStream(configText.toByteArray(StandardCharsets.UTF_8)))
        currentConfig = config
        var random = (Math.random()*9+1)*1000
        val name = "tianya" + random.toLong()
        Application.getTunnelManager().create(name, config).whenComplete { tunnel, throwable ->
            if (tunnel != null) {
                setCurrentTunnul(tunnel)
                setConfigApp()
                connectedRequest()
                isConnecting = true
                setTunnelState(true)
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
          //  iv_tunnel_connect.setBackgroundResource(R.mipmap.icon_home_connect)
            disConnectRequest()
        } else if (newState == Tunnel.State.UP) {

            //tv_connect_state.text = "connected"
           // connectedRequest()
        }
    }

    private fun configAPPItem() {
        val appFlag =  Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"
        LogUtils.e("configAPPItem---->" + appFlag)

        if(appFlag.toInt() == 1) {
            tv_all_app_filter?.visibility = View.VISIBLE
            tv_select_app_filter?.visibility = View.GONE
            rv_app_home_item?.visibility = View.GONE
            return
        }
        tv_all_app_filter?.visibility = View.GONE
        tv_select_app_filter?.visibility = View.VISIBLE
        rv_app_home_item?.visibility = View.VISIBLE

        var appname:String = "";

        if (appFlag.toInt() == 2) {
            tv_select_app_filter?.text = "Do not allow selected apps to use"
            var excludeApps = Application.getExcludeAppList()
            homeAppAdapter?.setDataList(excludeApps)
            for (appItem in excludeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
               appname = appname.substring(0,appname.length-1)
            }
            LogUtils.e("excludeApps---->" + excludeApps.count())
        }
        if (appFlag.toInt() == 3) {
            tv_select_app_filter?.text = "Allow selected apps to use"
            var includeApps = Application.getIncludeAppList()
            homeAppAdapter?.setDataList(includeApps)
            for (appItem in includeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
                appname = appname.substring(0,appname.length-1)
            }
            LogUtils.e("includeApps---->" + includeApps.count())
        }
        LogUtils.e("appname---->" + appname)

        filterApp(appFlag.toInt(), appname)
    }

   private fun setConfigApp() {
       var applicationsSet:MutableSet<String> =  mutableSetOf()
       val appFlag =  Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"

       if(appFlag.toInt() == 1) {
           tv_all_app_filter?.visibility = View.VISIBLE
           tv_select_app_filter?.visibility = View.GONE
           rv_app_home_item?.visibility = View.GONE
           return
       }
       tv_all_app_filter?.visibility = View.GONE
       tv_select_app_filter?.visibility = View.VISIBLE
       rv_app_home_item?.visibility = View.VISIBLE

       if (appFlag.toInt() == 2) {
           var excludeApps = Application.getExcludeAppList()
           excludeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               currentConfig?.`interface`?.excludedApplications?.addAll(applicationsSet)
               LogUtils.e( "excludeApps--->" + applicationsSet.toString())
           }
           homeAppAdapter?.setDataList(excludeApps)
       }
       if (appFlag.toInt() == 3) {
           var includeApps = Application.getIncludeAppList()
           includeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               // 写子线程中的操作
               currentConfig?.`interface`?.includedApplications?.addAll(applicationsSet)
               var size = currentConfig?.`interface`?.includedApplications?.size
               LogUtils.e( "includeApps--->" + applicationsSet.toString())

           }
           homeAppAdapter?.setDataList(includeApps)
       }
   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_VPN_PERMISSION) {
            if (pendingTunnel != null && pendingTunnelUp != null) setTunnelStateWithPermissionsResult(pendingTunnel!!, pendingTunnelUp!!)
            pendingTunnel = null
            pendingTunnelUp = null
        }
        if (requestCode == FILTER_RESULT_OK) {
            configAPPItem()
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



    fun startConnectAnimation() {

        if (frameAnimation == null) {
            val typedArray = this?.resources!!.obtainTypedArray(R.array.connecting)
            val len = typedArray.length()
            val resId = IntArray(len)
            for (i in 0 until len) {
                resId[i] = typedArray.getResourceId(i, -1)
            }
            typedArray.recycle()
            LogUtils.e("len------->" + len)
            frameAnimation =  FrameAnimation(iv_logo_animation!!, resId, 110, true)
            frameAnimation?.setAnimationListener(this)

            isLocked = true
            iv_flag_lock?.setBackgroundResource(R.mipmap.icon_encryption)
            iv_app_lock?.setBackgroundResource(R.mipmap.icon_encryption)
            btn_tunnel_connect?.visibility = View.GONE

            Handler().postDelayed(Runnable {
                finishStartAnimation()
            }, 3300)

        } else {
            isLocked = true
            iv_flag_lock?.setBackgroundResource(R.mipmap.icon_encryption)
            iv_app_lock?.setBackgroundResource(R.mipmap.icon_encryption)
            btn_tunnel_connect?.visibility = View.GONE
            frameAnimation?.restartPlay()
            Handler().postDelayed(Runnable {
                finishStartAnimation()
            }, 3300)
        }
    }

    fun disconnectAnimation() {

        if (disconnectFrameAnimation == null) {
            val typedArray = this?.resources!!.obtainTypedArray(R.array.disconnect)
            val len = typedArray.length()
            val resId = IntArray(len)
            for (i in 0 until len) {
                resId[i] = typedArray.getResourceId(i, -1)
            }
            typedArray.recycle()
            LogUtils.e("len------->" + len)
            disconnectFrameAnimation =  FrameAnimation(iv_logo_animation!!, resId, 100, true)
            disconnectFrameAnimation?.setAnimationListener(this)
            btn_tunnel_connect?.visibility = View.GONE
            Handler().postDelayed(Runnable {
                finishDisconnectAnimation()
            }, 3300)
        } else {

            disconnectFrameAnimation?.restartPlay()
            btn_tunnel_connect?.visibility = View.GONE
            Handler().postDelayed(Runnable {
                finishDisconnectAnimation()
            }, 3300)
        }
    }


    override fun onAnimationStart() {
        LogUtils.e("onAnimationStart---->")
    }

    override fun onAnimationEnd() {
        LogUtils.e("onAnimationEnd----> " + selectedTunnel?.getDataState())

    }

    override fun onAnimationRepeat() {
    }

    private fun finishStartAnimation() {

        frameAnimation?.pauseAnimation()
        isConnecting = false
        btn_tunnel_connect?.visibility = View.VISIBLE
        if(selectedTunnel?.getDataState() == Tunnel.State.UP) {
            iv_home_connect_toast?.visibility = View.VISIBLE
            Handler().postDelayed({
                iv_home_connect_toast?.visibility = View.GONE
            }, 3000)
           // iv_tunnel_connect?.setBackgroundResource(R.mipmap.icon_connect_finish)
            btn_tunnel_connect?.setBackgroundColor(ContextCompat.getColor(this.requireContext(),R.color.btn_red))
            btn_tunnel_connect?.text = "DISCONNECT"
        }else {
            setTunnelState(true)
        }
    }
    private fun finishDisconnectAnimation() {
        isLocked = false
        iv_flag_lock?.setBackgroundResource(R.mipmap.icon_right_arrow)
        iv_app_lock?.setBackgroundResource(R.mipmap.icon_right_arrow)
        disconnectFrameAnimation?.pauseAnimation()
        //iv_tunnel_connect?.setBackgroundResource(R.mipmap.icon_home_connect)
        btn_tunnel_connect?.setBackgroundColor(ContextCompat.getColor(this.requireContext(),R.color.btn_blue_color))
        btn_tunnel_connect?.text = "CONNECT"
        btn_tunnel_connect?.visibility = View.VISIBLE

    }

    private fun disConnectRequest() {
        if (curWireguardData == null) {
            return
        }
        val vpnModel = curWireguardData?.wireguards?.get(0);
        val wireguardId = vpnModel?.id ?: ""
        val  wireguardServiceId = vpnModel?.serviceId ?: ""
        ApiClient.instance.service.disConnect(wireguardId,wireguardServiceId)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: BaseResponseObject) {
                       // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        if (data != null) {
                            LogUtils.e("service.disconnected success")
                            // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                            //ToastUtils.show(apiErrorModel.message)
                        }                    }
                })

    }
    private fun connectedRequest() {

        if (curWireguardData == null) {
            return
        }
        val vpnModel = curWireguardData?.wireguards?.get(0);
        val wireguardId = vpnModel?.id ?: ""
        val  wireguardServiceId = vpnModel?.serviceId ?: ""

        ApiClient.instance.service.connected(wireguardId,wireguardServiceId)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: BaseResponseObject) {
                       // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        if (data != null) {
                            LogUtils.e("service.connected success")
                           // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                           // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })
    }

    private fun filterApp(filterType:Int, appName:String) {

        ApiClient.instance.service.filterApp(filterType,appName)
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : ApiResponse<BaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: BaseResponseObject) {
                        // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: BaseResponseObject) {
                        if (data != null) {
                            LogUtils.e("service.connected success")
                            // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                            // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })
    }

    fun queryReplyCount() {

        ApiClient.instance.service.queryReplyCount(AppConfigData.loginName ?: "")
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : ApiResponse<QueryReplyCountResponse>(requireActivity(),false){
                    override fun businessFail(data: QueryReplyCountResponse) {
                        // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: QueryReplyCountResponse) {
                        if (data != null) {
                            data.data == 1
                            if (data.data > 0) {
                                val count = data.data
                                tv_red_message?.setText(count.toString())
                                tv_red_message?.visibility = View.VISIBLE
                            } else {
                                tv_red_message?.visibility = View.GONE
                            }
                            LogUtils.e("service.connected success")
                        }
                    }
                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        if (this != null) {
                            // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })


    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event : SelectWireguardEvent) {
        val handler = Handler(Looper.getMainLooper())
        var runnable = Runnable {
            initWireguardData(event)
        }
        handler.postDelayed(runnable,1000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event : ReconnectTunnelEvent) {

        Handler().postDelayed({
            setConfigApp()
        },2000)

        return
        val handler = Handler()
        var runnable = Runnable {
            if (Application.get().isNeedConnectByModifyAppFlag) {

                if( selectedTunnel?.getDataState() == Tunnel.State.UP) {
                    startAndStopWireGuard()
                    Handler().postDelayed( Runnable {
                        setConfigApp()
                        startAndStopWireGuard()
                    },3600)
                }
            }
        }
        handler.postDelayed(runnable,1500)
        //finish()
    }


    companion object {
        private const val REQUEST_CODE_VPN_PERMISSION = 23491
        private const val TAG = "sirius/tunnelConnectFragment"

    }
}