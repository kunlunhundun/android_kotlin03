/*
 * Copyright © 2017-2019 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.sunblackhole.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.*
import android.os.StrictMode.VmPolicy
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.alModel.AppPackageModel
import com.sunblackhole.android.aliData.net.ApiClient
import com.sunblackhole.android.alutils.ACache
import com.sunblackhole.android.alutils.LogUtils
import com.sunblackhole.android.alutils.Utils
import com.sunblackhole.android.backend.Backend
import com.sunblackhole.android.backend.GoBackend
import com.sunblackhole.android.backend.WgQuickBackend
import com.sunblackhole.android.configStore.FileConfigStore
import com.sunblackhole.android.model.TunnelManager
import com.sunblackhole.android.util.*
import io.fabric.sdk.android.Fabric
import java9.util.concurrent.CompletableFuture
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


class Application : android.app.Application(), OnSharedPreferenceChangeListener {
    private val futureBackend = CompletableFuture<Backend>()
    private lateinit var asyncWorker: AsyncWorker
    private var backend: Backend? = null
    private lateinit var moduleLoader: ModuleLoader
    private lateinit var rootShell: RootShell
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolsInstaller: ToolsInstaller
    private lateinit var tunnelManager: TunnelManager

    private  var excludeAppList:ArrayList<AppPackageModel> = ArrayList()
    private  var includeAppList:ArrayList<AppPackageModel> = ArrayList()
    private  var allAppList:ArrayList<AppPackageModel> = ArrayList()
    private  var excludeAppNameList:ArrayList<String> = ArrayList()
    private  var includeAppNameList:ArrayList<String> = ArrayList()
    private lateinit var mAcach: ACache
    var isNeedConnectByModifyAppFlag:Boolean = false //改变设置后自动重新连接
    private val devKey = "bBdEhLPpGE7aWhho4JoJwn"

    lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        if (BuildConfig.MIN_SDK_VERSION > Build.VERSION.SDK_INT) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            System.exit(0)
        }
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }

    override fun onCreate() {
        Log.i(TAG, USER_AGENT)
        super.onCreate()

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        AppsFlyerInstall()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        Fabric.with(this, Crashlytics())
        var bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "tianming");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,bundle)



        var version = Utils.getVersion()

        LogUtils.e("version ----->", version)
        LogUtils.d("version ----->", version)

        Thread(Runnable {
            try {
                Thread.sleep(2000); // 休眠1秒
                initAppData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()

        ToastUtils.init(this);
        ApiClient.instance.init()
        get().mAcach = ACache.get(this)

        asyncWorker = AsyncWorker(AsyncTask.SERIAL_EXECUTOR, Handler(Looper.getMainLooper()))
        rootShell = RootShell(applicationContext)
        toolsInstaller = ToolsInstaller(applicationContext, rootShell)
        moduleLoader = ModuleLoader(applicationContext, rootShell, USER_AGENT)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(
                    if (sharedPreferences.getBoolean("dark_theme", false)) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        tunnelManager = TunnelManager(FileConfigStore(applicationContext))
        tunnelManager.onCreate()
        asyncWorker.supplyAsync(Companion::getBackend).thenAccept { futureBackend.complete(it) }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }


    fun initAppData(){

        LogUtils.e("initAppData--->")
        val appFlag =  get().mAcach.getAsString(CACHE_ALLOW_APP_FLAG)
        if (TextUtils.isEmpty(appFlag) == true) {
            get().mAcach.put(CACHE_ALLOW_APP_FLAG,"1")
        }
        var excludeAppStr = getAcache().getAsString(CACHE_EXCCLUDE_DATA)
        //val turnsType = object : TypeToken<ArrayList<AppPackageModel>>() {}.type

        val turnsType = object : TypeToken<ArrayList<String>>() {}.type
        if ( !TextUtils.isEmpty(excludeAppStr) ) {
            excludeAppNameList = Gson().fromJson<ArrayList<String>>(excludeAppStr,  turnsType)
        }
        var includeAppStr = getAcache().getAsString(CACHE_INCLUDE_DATA)
        if (!TextUtils.isEmpty(includeAppStr)) {
            includeAppNameList = Gson().fromJson<ArrayList<String>>(includeAppStr, turnsType)
        }
        // val turnsType = object : TypeToken<MutableList<AppPackageModel>>() {}.type
        // val data = Gson().fromJson<MutableList<AppPackageModel>>("dataString",  turnsType)

        val pm = packageManager
        getAsyncWorker().supplyAsync<List<AppPackageModel>> {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfos = pm.queryIntentActivities(launcherIntent, 0)
            resolveInfos.forEach {
              //  val labelRes: Int = it.labelRes
               // val appName =  this.getResources().getString(labelRes)
                val packageName = it.activityInfo.packageName
                val appName =  it.loadLabel(pm).toString()
                val appData = AppPackageModel(it.loadIcon(pm), appName, packageName, 1)
                allAppList.add(appData)
                excludeAppNameList.forEach { nameItem:String ->
                    if (appName == nameItem) {
                        excludeAppList.add(appData)
                    }
                }
                includeAppNameList.forEach { nameItem:String ->
                    if (appName == nameItem) {
                        includeAppList.add(appData)
                    }
                }
            }
            allAppList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name!! })

            allAppList
        }.whenComplete { data, throwable ->
            if (data != null) {

            } else {
                val message = getString(R.string.error_fetching_apps)
            }
        }
    }



    private fun AppsFlyerInstall() {

        val conversionDataListener  = object : AppsFlyerConversionListener{
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Log.i("LOG_TAG", "conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                LogUtils.e( "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    LogUtils.e("onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                LogUtils.e( "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if ("multiple_tunnels" == key && backend != null && backend is WgQuickBackend)
            (backend as WgQuickBackend).setMultipleTunnels(sharedPreferences.getBoolean(key, false))
    }

    override fun onTerminate() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onTerminate()
    }

    companion object {
        val USER_AGENT = String.format(Locale.ENGLISH, "WireGuard/%s (Android %d; %s; %s; %s %s; %s)", BuildConfig.VERSION_NAME, Build.VERSION.SDK_INT, if (Build.SUPPORTED_ABIS.isNotEmpty()) Build.SUPPORTED_ABIS[0] else "unknown ABI", Build.BOARD, Build.MANUFACTURER, Build.MODEL, Build.FINGERPRINT)
        private const val TAG = "WireGuard/Application"
        private lateinit var weakSelf: WeakReference<Application>

        public const val CACHE_EXCCLUDE_DATA  = "cache_exclude_data" //
        public const val CACHE_INCLUDE_DATA  = "cache_include_data" // 包含的app
        public const val CACHE_ALLOW_APP_FLAG  = "cache_allow_app_flag" // 1:允许所有的app 2: 选择app可以vpn

        @JvmStatic
        fun get(): Application {
            return weakSelf.get()!!
        }

        @JvmStatic
        fun getAcache(): ACache {
            return Application.get().mAcach
        }

        @JvmStatic
        fun getAsyncWorker() = get().asyncWorker

        @JvmStatic
        fun getBackend(): Backend {
            val app = get()
            synchronized(app.futureBackend) {
                if (app.backend == null) {
                    var backend: Backend? = null
                    var didStartRootShell = false
                    if (!ModuleLoader.isModuleLoaded() && app.moduleLoader.moduleMightExist()) {
                        try {
                            app.rootShell.start()
                            didStartRootShell = true
                            app.moduleLoader.loadModule()
                        } catch (ignored: Exception) {
                        }
                    }
                    if (!app.sharedPreferences.getBoolean("disable_kernel_module", false) && ModuleLoader.isModuleLoaded()) {
                        try {
                            if (!didStartRootShell)
                                app.rootShell.start()
                            val wgQuickBackend = WgQuickBackend(app.applicationContext, app.rootShell, app.toolsInstaller)
                            wgQuickBackend.setMultipleTunnels(app.sharedPreferences.getBoolean("multiple_tunnels", false))
                            backend = wgQuickBackend
                        } catch (ignored: Exception) {
                        }
                    }
                    if (backend == null) {
                        backend = GoBackend(app.applicationContext)
                        GoBackend.setAlwaysOnCallback { get().tunnelManager.restoreState(true).whenComplete(ExceptionLoggers.D) }
                    }
                    app.backend = backend
                }
                return app.backend!!
            }
        }

        @JvmStatic
        fun getAllAppList() = get().allAppList

        @JvmStatic
        fun getIncludeAppList() = get().includeAppList

        @JvmStatic
        fun getExcludeAppList() = get().excludeAppList

        @JvmStatic
        fun getBackendAsync() = get().futureBackend

        @JvmStatic
        fun getModuleLoader() = get().moduleLoader

        @JvmStatic
        fun getRootShell() = get().rootShell

        @JvmStatic
        fun getSharedPreferences() = get().sharedPreferences

        @JvmStatic
        fun getToolsInstaller() = get().toolsInstaller

        @JvmStatic
        fun getTunnelManager() = get().tunnelManager
    }

    init {
        weakSelf = WeakReference(this)
    }



}
