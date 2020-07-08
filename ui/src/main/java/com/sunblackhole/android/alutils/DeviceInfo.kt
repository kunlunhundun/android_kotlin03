package com.sunblackhole.android.alutils

import android.content.Context
import android.os.Build
import android.text.TextUtils
import java.io.*
import java.math.BigDecimal
import java.util.*

object DeviceInfo {

    private val TAG = DeviceInfo::class.java.simpleName + "_channel"


    /**
     * 获取手机唯一mac地址，无需要设置权限以及不需要打开WIFI
     *
     * @return
     */
    @JvmStatic
    fun getMac(): String {
        var macSerial = ""
        var str: String? = ""
        try {
            // 三星手机定制，获取地址为cat /sys/class/net/eth0/address
            val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)
            while (null != str) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' }// 去空格
                    break
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return macSerial
    }

    /**
     * 获取手机唯一设备号
     *
     * @return 返回mac地址和手机设备号等硬件信息的拼接
     */
    @JvmStatic
    fun getDeviceId(): String {
        val sb = StringBuilder()
        var mac = getMac()// 通过Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address")   无需要打开WiFi
        if (!TextUtils.isEmpty(mac)) {
            sb.append(mac).append(";")
        } else {
            mac = Utils.getMacAddr()
            sb.append(mac).append(";")
        }
        sb.append("Build.FINGERPRINT=" + Build.FINGERPRINT).append(";")//硬件设备唯一识别号
        sb.append("Build.SERIAL=" + Build.SERIAL).append(";")// 2.3以及以上可以获取到值
        sb.append("Build.BOARD=" + Build.BOARD).append(";")
        sb.append("Build.MANUFACTURER=" + Build.MANUFACTURER).append(";")
        sb.append("Build.BRAND=" + Build.BRAND).append(";")
        sb.append("Build.DISPLAY=" + Build.DISPLAY).append(";")
        sb.append("Build.HARDWARE=" + Build.HARDWARE)
        return Utils.md5Encode(sb.toString())
    }

    /**
     * @return 手机设备基板名称以及制造商
     */
    @JvmStatic
    fun getPhoneBoard(): String {
        return Build.BOARD.replace(" ".toRegex(), "-") + "-" + Build.MANUFACTURER.replace(" ".toRegex(), "-")
    }

    /**
     * 获取系统语言
     *
     * @return
     */
    @JvmStatic
    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * @return SDK版本号
     */
    @JvmStatic
    fun getSDKVersion(): String {
        return Build.VERSION.SDK
    }

    /**
     * @return 手机系统版本号
     */
    @JvmStatic
    fun getFirmwareVersion(): String {
        return Build.VERSION.RELEASE.replace(" ".toRegex(), "-")
    }


    /**
     * @return 手机品牌
     */
    @JvmStatic
    fun getPhoneBrand(): String {
        return Build.BRAND.replace(" ".toRegex(), "-")
    }


    /**
     * @return 手机型号
     */
    @JvmStatic
    fun getPhoneModel(): String {

        return Build.MODEL.replace(" ".toRegex(), "-")
    }


    /**
     * @return 获取root信息
     */
    @JvmStatic
    fun isRoot(): String {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return "true"
        }

        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return "true"
            }
        } catch (e: Exception) {

        }

        return if (canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su")
                || canExecuteCommand("busybox which su")) "true" else "false"
    }

    private fun canExecuteCommand(command: String): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(command)
            val ins = BufferedReader(InputStreamReader(process!!.inputStream))
            val info = ins.readLine()
            return info != null
        } catch (e: Exception) {
            //do noting
        } finally {
            if (process != null) process.destroy()
        }
        return false
    }

    @JvmStatic
    fun getTotalMemorySize(context: Context): String {
        val dir = "/proc/meminfo"
        try {
            val fr = FileReader(dir)
            val br = BufferedReader(fr, 2048)
            val memoryLine = br.readLine()
            val subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"))
            br.close()
            return "${Math.round((BigDecimal(subMemoryLine.replace("\\D+".toRegex(), "")) / BigDecimal(1000000)).toDouble())}GB"
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }
}