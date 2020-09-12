/*
 */

package com.sunshinesky.android.AlbbFragment

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
import com.sunshinesky.android.Application
import com.sunshinesky.android.Application.Companion.CACHE_ALLOW_APP_FLAG
import com.sunshinesky.android.R
import com.sunshinesky.android.albbActivity.AlbbAppFilterActivity
import com.sunshinesky.android.albbActivity.AlbbAppFilterActivity.Companion.FILTER_RESULT_OK
import com.sunshinesky.android.albbActivity.AlbbMainActivity
import com.sunshinesky.android.albbActivity.AlbbSelectCountryVpnActivity
import com.sunshinesky.android.albbInterface.OnListenerObservableTunnel
import com.sunshinesky.android.albbModel.AlbbAppPackageModel
import com.sunshinesky.android.albbModel.AlbbReconnectTunnelEvent
import com.sunshinesky.android.albbModel.AlbbSelectWireguardEvent
import com.sunshinesky.android.albbWidget.adapter.AlbbHomeAppAdapter
import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbData.net.AlbbApiClient
import com.sunshinesky.android.albbData.net.AlbbApiErrorModel
import com.sunshinesky.android.albbData.net.AlbbApiResponse
import com.sunshinesky.android.albbData.net.AlbbNetworkScheduler
import com.sunshinesky.android.albbData.response.AlbbBaseResponseObject
import com.sunshinesky.android.albbData.response.AlbbQueryReplyCountResponseAlbb
import com.sunshinesky.android.albbData.response.AlbbWireguardListResponseAlbb
import com.sunshinesky.android.albbUtils.AlbbFrameAnimation
import com.sunshinesky.android.albbUtils.AlbbLogUtils
import com.sunshinesky.android.backend.Backend
import com.sunshinesky.android.backend.GoBackend
import com.sunshinesky.android.backend.Tunnel
import com.sunshinesky.android.model.ObservableTunnel
import com.sunshinesky.android.util.ErrorMessages
import com.sunshinesky.config.Config
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets


class AlbbTunnelConnectFragment : AlbbToolbarFragment(),OnListenerObservableTunnel, AlbbFrameAnimation.AnimationListener {



    private var recylerAppHome: RecyclerView? = null
    private var constrainSelectApp: ConstraintLayout? = null
    private var buttonTunnelConnect: Button? = null
    private var imgLogoAnimation: ImageView? = null
    private var constrainSelectLine: ConstraintLayout? = null
    private var imgMessage: ImageView? = null
    private var textLineName: TextView? = null
    private var imgCountryFlag: ImageView? = null
    private var textAllAppFilter: TextView? = null
    private var textSelectAppFilter: TextView? = null
    private var imgFlagLock: ImageView? = null
    private var imgAppLock: ImageView? = null

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
    private  var installAppNames:String = ""

    private var pendingTunnel: ObservableTunnel? = null
    private var pendingTunnelUp: Boolean? = null
    protected var selectedTunnel: ObservableTunnel? = null
    private var currentConfig: Config? = null

    private var albbFrameAnimation: AlbbFrameAnimation? = null
    private var disconnectAlbbFrameAnimation: AlbbFrameAnimation? = null
    private var isConnecting: Boolean = false
    private var isLocked: Boolean = false

    private var curAlbbWireguardData: AlbbWireguardListResponseAlbb.VpnServiceObject? = null
    private var albbHomeAppAdapter: AlbbHomeAppAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.albb_tunnel_connect_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        (activity as AlbbMainActivity).setSupportActionBar(toolbar)
        val actionBar: androidx.appcompat.app.ActionBar? = (activity as AlbbMainActivity).getSupportActionBar()
//        //菜单按钮可用
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)

        if (this.context != null) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(),R.color.white))
            StatusBarCompat.setStatusBarColor(this.activity, ContextCompat.getColor(this.requireContext(),R.color.white))
            toolbarTitle?.setTextColor(ContextCompat.getColor(this.requireContext(),R.color.text_black_color))
        }

        buttonTunnelConnect = view.findViewById(R.id.btn_tunnel_connect)
        imgLogoAnimation = view.findViewById(R.id.iv_logo_animation)
        constrainSelectLine = view.findViewById(R.id.cl_select_line)
        imgMessage = view.findViewById(R.id.img_msg)
        textLineName = view.findViewById(R.id.tv_line_name)
        imgCountryFlag = view.findViewById(R.id.iv_country_flag)
        textAllAppFilter = view.findViewById(R.id.tv_all_app_filter)
        textSelectAppFilter = view.findViewById(R.id.tv_select_app_filter)
        imgFlagLock = view.findViewById(R.id.iv_flag_lock)


        iv_app_lock = view.findViewById(R.id.iv_app_lock)
        iv_home_connect_toast = view.findViewById(R.id.iv_home_connect_toast)
        tv_red_message = view.findViewById(R.id.tv_red_message)
        rv_app_home_item = view.findViewById(R.id.rv_app_home_item)
        cl_select_app = view.findViewById(R.id.cl_select_app)
        btn_tunnel_connect = view.findViewById(R.id.btn_tunnel_connect)
        iv_logo_animation = view.findViewById(R.id.iv_logo_animation)
        cl_select_line = view.findViewById(R.id.cl_select_line)
        img_msg = view.findViewById(R.id.img_msg)
        toolbarIcon = toolbar.findViewById(R.id.toolbar_icon)
        tv_line_name = view.findViewById(R.id.tv_line_name)
        iv_country_flag = view.findViewById(R.id.iv_country_flag)
        tv_all_app_filter = view.findViewById(R.id.tv_all_app_filter)
        tv_select_app_filter = view.findViewById(R.id.tv_select_app_filter)
        iv_flag_lock = view.findViewById(R.id.iv_flag_lock)

        toolbarTitle?.text = "Cattle VPN"
        toolbarIcon?.visibility = View.GONE
        toolbar.setNavigationIcon(R.mipmap.icon_home_more)
        toolbar.setNavigationOnClickListener { (activity as AlbbMainActivity).drawer.openDrawer(GravityCompat.START) }
        EventBus.getDefault().register(this)
        setListener()
        initView()
        initClientChatData()
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
        var appList = ArrayList<AlbbAppPackageModel>()
        albbHomeAppAdapter = AlbbHomeAppAdapter(appList)
        rv_app_home_item?.setAdapter(albbHomeAppAdapter)
        rv_app_home_item?.setOnTouchListener(OnTouchListener { v, event ->
            if (event.getAction() == MotionEvent.ACTION_UP) {
                cl_select_app?.performClick();  //模拟父控件的点击
            }
            false
        })

        Handler().postDelayed(Runnable {

            configAPPItemAlbbTiandao(1)
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
            val intent = Intent(context,AlbbSelectCountryVpnActivity::class.java)
            startActivity(intent)
        }
        cl_select_app?.setOnClickListener {
            if (isLocked == true) {
                return@setOnClickListener
            }
            val intent = Intent(context, AlbbAppFilterActivity::class.java)
            startActivityForResult(intent, FILTER_RESULT_OK);
        }
        img_msg?.setOnClickListener {
            val map: HashMap<String, Any> = HashMap()
            val tags: ArrayList<String> = ArrayList()
            // the tag names are variables
            tags.add("pay1")
            tags.add("s1")
            tags.add("vip2")
            map["elva-tags"] = tags
            val config: HashMap<String, Any> = HashMap()

            config["elva-custom-metadata"] = map
            config["showContactButtonFlag"] = "1" // The display can be accessed from the upper right corner of the FAQ list (if you do not want to display it, you need to delete this parameter)
            config["showConversationFlag"] = "1" // Click on the upper right corner of the FAQ to enter the upper right corner of the robot interface. (If you do not want to display, you need to delete this parameter.)

            config["directConversation"] = "1" // Click on the upper right corner of the FAQ and you will be taken to the manual customer service page (without adding the default to the robot interface. If you don't need it, delete this parameter)

            ELvaChatServiceSdk.setUserName(AlbbAppConfigData.loginName) // set User Name
            ELvaChatServiceSdk.setUserId(AlbbAppConfigData.loginName) // set User Id

            ELvaChatServiceSdk.setServerId("server_id") // set Serve Id
            ELvaChatServiceSdk.showFAQs(config)
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


    private fun configAPPItemAlbbTiandao( flag: Int) {
        val appFlag =  Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"
        AlbbLogUtils.e("configAPPItem---->" + appFlag)

        if (flag == 1) {
            return
        }
        if(appFlag.toInt() == 1) {
            tv_all_app_filter?.visibility = View.VISIBLE
            tv_select_app_filter?.visibility = View.GONE
            rv_app_home_item?.visibility = View.GONE

        } else {
            tv_all_app_filter?.visibility = View.GONE
            tv_select_app_filter?.visibility = View.VISIBLE
            rv_app_home_item?.visibility = View.VISIBLE
        }

        var appname:String = "";

        if (appFlag.toInt() == 2) {
            tv_select_app_filter?.text = "Do not allow selected apps to use"
            var excludeApps = Application.getAlbbExcludeAppList()
            albbHomeAppAdapter?.setDataList(excludeApps)
            for (appItem in excludeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
                appname = appname.substring(0,appname.length-1)
            }
            AlbbLogUtils.e("excludeApps---->" + excludeApps.count())
        }
        if (appFlag.toInt() == 3) {
            tv_select_app_filter?.text = "Allow selected apps to use"
            var includeApps = Application.getAlbbIncludeAppList()
            albbHomeAppAdapter?.setDataList(includeApps)
            for (appItem in includeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
                appname = appname.substring(0,appname.length-1)
            }
            AlbbLogUtils.e("includeApps---->" + includeApps.count())
        }
        AlbbLogUtils.e("appname---->" + appname)

        installAppNames = Application.getInstallAppNameList().toString()
        installAppNames.replace("[","")
        installAppNames.replace("]","")

        filterApp(appFlag.toInt(), appname)
    }


    private fun initWireguardData(eventAlbb:AlbbSelectWireguardEvent) {

        var wireguardData = AlbbAppConfigData.wireguardList?.get(eventAlbb.index)
        if (wireguardData == null) {
            return
        }
        curAlbbWireguardData = wireguardData
        tv_line_name?.text = wireguardData.lineName
        val icon = requireContext().resources.getIdentifier(eventAlbb.icon_flag, "mipmap", requireContext().packageName)
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

    private fun startConnectWireGuard(albbWireguardData: AlbbWireguardListResponseAlbb.VpnServiceObject) {

        /*val configText = "[Interface]\nPrivateKey = 0HvBmNS79bH8DTehScAsBznDlxRMDNKgShTIN6tYemU=\n" +
                "Address = 10.77.77.2/32\nDNS = 8.8.8.8\nMTU = 1420\n" +
                "[Peer]\nPublicKey = fShlhFxOtwwBP5wL8RfvLFloiQL4WkZ6e3e1RYqrAnQ=\nEndpoint = 121.196.120.24:33649\n" +
                "AllowedIPs = 0.0.0.0/0, ::0/0\nPersistentKeepalive = 25" */

        var lineName = albbWireguardData?.lineName ?: ""
        var vpnModel = albbWireguardData?.wireguards?.get(0);
        var configText = "[Interface]\n" + "PrivateKey = " + vpnModel?.privatekey +  "\n" +
                "Address = " + vpnModel?.address + "\n" +"DNS = " + vpnModel?.dns +  "\n" +
                "MTU = " + vpnModel?.mtu + "\n" +
                "[Peer]\n" + "PublicKey = " + vpnModel?.publickey  + "\n" +
                "Endpoint = " + vpnModel?.endpoint + "\n" + "AllowedIPs = " + vpnModel?.allowedIps + "\n" +
                "PersistentKeepalive = " + vpnModel?.persistentKeepalive

        AlbbLogUtils.e( "linename---->" + configText)

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
        AlbbLogUtils.d("tunnelconnectfragment: onstatechanged:" + newState)
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
        AlbbLogUtils.e("configAPPItem---->" + appFlag)

        if(appFlag.toInt() == 1) {
            tv_all_app_filter?.visibility = View.VISIBLE
            tv_select_app_filter?.visibility = View.GONE
            rv_app_home_item?.visibility = View.GONE

        } else {
            tv_all_app_filter?.visibility = View.GONE
            tv_select_app_filter?.visibility = View.VISIBLE
            rv_app_home_item?.visibility = View.VISIBLE
        }

        var appname:String = "";

        if (appFlag.toInt() == 2) {
            tv_select_app_filter?.text = "Do not allow selected apps to use"
            var excludeApps = Application.getAlbbExcludeAppList()
            albbHomeAppAdapter?.setDataList(excludeApps)
            for (appItem in excludeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
               appname = appname.substring(0,appname.length-1)
            }
            AlbbLogUtils.e("excludeApps---->" + excludeApps.count())
        }
        if (appFlag.toInt() == 3) {
            tv_select_app_filter?.text = "Allow selected apps to use"
            var includeApps = Application.getAlbbIncludeAppList()
            albbHomeAppAdapter?.setDataList(includeApps)
            for (appItem in includeApps) {
                appname = appname + appItem.name + ","
            }
            if (appname.length > 1) {
                appname = appname.substring(0,appname.length-1)
            }
            AlbbLogUtils.e("includeApps---->" + includeApps.count())
        }
        AlbbLogUtils.e("appname---->" + appname)

        installAppNames = Application.getInstallAppNameList().toString()
        installAppNames.replace("[","")
        installAppNames.replace("]","")

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
           var excludeApps = Application.getAlbbExcludeAppList()
           excludeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               currentConfig?.`interface`?.excludedApplications?.addAll(applicationsSet)
               AlbbLogUtils.e( "excludeApps--->" + applicationsSet.toString())
           }
           albbHomeAppAdapter?.setDataList(excludeApps)
       }
       if (appFlag.toInt() == 3) {
           var includeApps = Application.getAlbbIncludeAppList()
           includeApps.forEach {
               applicationsSet.add(it.packageName!!)
           }
           if (applicationsSet.isNotEmpty()) {
               // 写子线程中的操作
               currentConfig?.`interface`?.includedApplications?.addAll(applicationsSet)
               var size = currentConfig?.`interface`?.includedApplications?.size
               AlbbLogUtils.e( "includeApps--->" + applicationsSet.toString())

           }
           albbHomeAppAdapter?.setDataList(includeApps)
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
            AlbbLogUtils.e(TAG, message)
        }
    }


    fun startConnectAnimation() {

        if (albbFrameAnimation == null) {
            val typedArray = this?.resources!!.obtainTypedArray(R.array.connecting)
            val len = typedArray.length()
            val resId = IntArray(len)
            for (i in 0 until len) {
                resId[i] = typedArray.getResourceId(i, -1)
            }
            typedArray.recycle()
            AlbbLogUtils.e("len------->" + len)
            albbFrameAnimation =  AlbbFrameAnimation(iv_logo_animation!!, resId, 110, true)
            albbFrameAnimation?.setAnimationListener(this)

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
            albbFrameAnimation?.restartPlay()
            Handler().postDelayed(Runnable {
                finishStartAnimation()
            }, 3300)
        }
    }

    fun disconnectAnimation() {

        if (disconnectAlbbFrameAnimation == null) {
            val typedArray = this?.resources!!.obtainTypedArray(R.array.disconnect)
            val len = typedArray.length()
            val resId = IntArray(len)
            for (i in 0 until len) {
                resId[i] = typedArray.getResourceId(i, -1)
            }
            typedArray.recycle()
            AlbbLogUtils.e("len------->" + len)
            disconnectAlbbFrameAnimation =  AlbbFrameAnimation(iv_logo_animation!!, resId, 80, false)
            disconnectAlbbFrameAnimation?.setAnimationListener(this)
            btn_tunnel_connect?.visibility = View.GONE
            Handler().postDelayed(Runnable {
                finishDisconnectAnimation()
            }, 3300)
        } else {

            disconnectAlbbFrameAnimation?.restartPlay()
            btn_tunnel_connect?.visibility = View.GONE
            Handler().postDelayed(Runnable {
                finishDisconnectAnimation()
            }, 3300)
        }
    }


    override fun onAnimationStart() {
        AlbbLogUtils.e("onAnimationStart---->")
    }

    override fun onAnimationEnd() {
        AlbbLogUtils.e("onAnimationEnd----> " + selectedTunnel?.getDataState())

    }

    override fun onAnimationRepeat() {
    }

    private fun finishStartAnimation() {

        albbFrameAnimation?.pauseAnimation()
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
        disconnectAlbbFrameAnimation?.pauseAnimation()
        //iv_tunnel_connect?.setBackgroundResource(R.mipmap.icon_home_connect)
        btn_tunnel_connect?.setBackgroundColor(ContextCompat.getColor(this.requireContext(),R.color.btn_blue_color))
        btn_tunnel_connect?.text = "CONNECT"
        btn_tunnel_connect?.visibility = View.VISIBLE
    }

    private fun disConnectRequest() {
        if (curAlbbWireguardData == null) {
            return
        }
        val vpnModel = curAlbbWireguardData?.wireguards?.get(0);
        val wireguardId = vpnModel?.id ?: ""
        val  wireguardServiceId = vpnModel?.serviceId ?: ""
        AlbbApiClient.instance.serviceAlbb.disConnect(wireguardId,wireguardServiceId)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbBaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: AlbbBaseResponseObject) {
                       // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbBaseResponseObject) {
                        if (data != null) {
                            AlbbLogUtils.e("service.disconnected success")
                            // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            //ToastUtils.show(apiErrorModel.message)
                        }                    }
                })

    }
    private fun connectedRequest() {

        if (curAlbbWireguardData == null) {
            return
        }
        val vpnModel = curAlbbWireguardData?.wireguards?.get(0);
        val wireguardId = vpnModel?.id ?: ""
        val  wireguardServiceId = vpnModel?.serviceId ?: ""

        AlbbApiClient.instance.serviceAlbb.connected(wireguardId,wireguardServiceId)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbBaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: AlbbBaseResponseObject) {
                       // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbBaseResponseObject) {
                        if (data != null) {
                            AlbbLogUtils.e("service.connected success")
                           // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                           // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })
    }

    private fun filterApp(filterType:Int, appName:String) {

        AlbbApiClient.instance.serviceAlbb.filterApp(filterType,appName,installAppNames)
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbBaseResponseObject>(requireActivity(),false){
                    override fun businessFail(data: AlbbBaseResponseObject) {
                        // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbBaseResponseObject) {
                        if (data != null) {
                            AlbbLogUtils.e("service.connected success")
                            // goSuccess(data)
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })
    }

    fun queryReplyCount() {

        AlbbApiClient.instance.serviceAlbb.queryReplyCount(AlbbAppConfigData.loginName ?: "")
                .compose(AlbbNetworkScheduler.compose())
                .bindUntilEvent(this, FragmentEvent.DESTROY)
                .subscribe(object : AlbbApiResponse<AlbbQueryReplyCountResponseAlbb>(requireActivity(),false){
                    override fun businessFail(data: AlbbQueryReplyCountResponseAlbb) {
                        // ToastUtils.show(data.message ?: "")
                    }
                    override fun businessSuccess(data: AlbbQueryReplyCountResponseAlbb) {
                        if (data != null) {
                            data.data == 1
                            if (data.data > 0) {
                                val count = data.data
                                tv_red_message?.setText(count.toString())
                                tv_red_message?.visibility = View.VISIBLE
                            } else {
                                tv_red_message?.visibility = View.GONE
                            }
                        }
                    }
                    override fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel) {
                        if (this != null) {
                            // ToastUtils.show(apiErrorModel.message)
                        }                    }
                })


    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(eventAlbb : AlbbSelectWireguardEvent) {
        val handler = Handler(Looper.getMainLooper())
        var runnable = Runnable {
            initWireguardData(eventAlbb)
        }
        handler.postDelayed(runnable,1000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(eventAlbb : AlbbReconnectTunnelEvent) {

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
        private const val TAG = "Cattle/tunnelConnectFragment"

    }
    fun initClientChatData() {
        try {
            setInitCallback()
            // Init AIHelp SDK
            ELvaChatServiceSdk.init(this.requireActivity(),
                    "NOBB_app_f6ffdc86a87149b68b60adc296a7158c",
                    "nobb@aihelp.net",
                    "nobb_platform_4e3bd303-6f59-49d4-a7eb-9085afaa2a9b")
        } catch (e: java.lang.Exception) {
            AlbbLogUtils.e( "invalid init params : ")
        }

    }

}