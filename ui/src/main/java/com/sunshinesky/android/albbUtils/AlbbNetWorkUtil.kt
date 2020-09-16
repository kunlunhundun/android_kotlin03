/*
 */

package com.sunshinesky.android.albbUtils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import com.sunshinesky.android.MyApplication
import java.net.NetworkInterface
import java.net.SocketException


/**
 *
 * 网络工具类
 *
 */
object AlbbNetWorkUtil {
    @JvmStatic
    fun isNetWorkConnected(): Boolean {
        val cm = MyApplication.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val currentNet = cm.activeNetworkInfo ?: return false
        return currentNet.isAvailable
    }

    private fun getWifiIP(context: Context): String? {
        var ip: String? = null
        val wifiManager = context
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            val wifiInfo = wifiManager.connectionInfo
            val i = wifiInfo.ipAddress
            ip = ((i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF)
                    + "." + (i shr 24 and 0xFF))
        }
        return ip
    }

    private fun getMobileIP(): String? {
        try {
            val en = NetworkInterface
                    .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf
                        .inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("哎呀，出错了...", ex.toString())
        }

        return null
    }

    fun getIP(context: Context):String?{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        return if(wifiNetworkInfo.isConnected){
            getWifiIP(context)
        }else{
            getMobileIP()
        }
    }
}