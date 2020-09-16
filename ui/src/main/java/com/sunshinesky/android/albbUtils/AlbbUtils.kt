/*
 */

package com.sunshinesky.android.albbUtils

import android.Manifest
import android.content.Context
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.github.dfqin.grantor.PermissionsUtil
import com.hjq.toast.ToastUtils
import com.sunshinesky.android.MyApplication
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.service.DownloadService
import com.vector.update_app.utils.AppUpdateUtils
import okhttp3.HttpUrl
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.TimeZone
import java.io.File


object AlbbUtils {

    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    fun getDateBefore(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day)
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        return now.time
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    fun getDateAfter(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day)
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        return now.time
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    @JvmStatic
    fun getVersion(): String {
        return try {
            val manager = MyApplication.get()!!.packageManager
            val info = manager.getPackageInfo(MyApplication.get().packageName, 0)
            val version = info.versionName
            version
        } catch (e: Exception) {
            e.printStackTrace()
            "1.0.0"
        }
    }


    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    @JvmStatic
    fun getVersionCode(): Int {
        return try {
            val manager = MyApplication.get()!!.packageManager
            val info = manager.getPackageInfo(MyApplication.get()!!.packageName, 0)
            val version = info.versionCode
            version
        } catch (e: Exception) {
            e.printStackTrace()
            12
        }
    }
    /**
     * 获取当前时区ID
     * @return 当前时区ID
     */
    @JvmStatic
    fun getTimeZoneID(): String {
        var tz = TimeZone.getDefault()
        return tz.id.toString()
    }

    /**
     * @return 获取当前的格式化日期比如 2018-03-01 12:23:35
     */
    @JvmStatic
    fun getCurentTime(): String {
        return try {
            System.currentTimeMillis().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * @return 获取当前的格式化日期比如 2018-03-01 12:23:35
     */
    @JvmStatic
    fun getCurentTime(pattern: String): String {
        return try {
            val simpleDateFormat = SimpleDateFormat(pattern)
            simpleDateFormat.format(Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }



    @JvmStatic
    fun getTwoDayHours(dateStr:String):String{

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var dayHours = ""
        try {

            val mDate =  dateFormat.parse(dateStr)
            val currentDate = Date()
            var dValueDay = (currentDate.time - mDate.time) /(24*60*60*1000)
            if (dValueDay == 0L) {
                dValueDay = (currentDate.time - mDate.time) /(60*60*1000)

                if (dValueDay == 0L) {
                    dayHours =  "1小时内"
                }else{
                    dayHours =  "$dValueDay"+"小时前"
                }
            }else{
                val month = dValueDay/30
                if (month > 0 ) {
                    if ( month > 12) {
                        val year = month/12
                        dayHours = "$year" + "年前"
                    }else{
                        dayHours = "$month" + "月前"
                    }
                }else{
                    dayHours =  "$dValueDay"+"天前"
                }
            }

        }catch ( e : Exception) {

        }
        return dayHours
    }

    /**
     * 返回一个时间+6位的随机数
     */
    @JvmStatic
    fun getRandomStr() : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateStr = dateFormat.format(Date())
        var random = (Math.random()*9+1)*1000
        return dateStr + (random.toString())
    }


    private var sTelephonyManager: TelephonyManager? = null

    @JvmStatic
    private fun getTelephonyManager(): TelephonyManager? {

        if (PermissionsUtil.hasPermission(MyApplication.get().applicationContext, Manifest.permission.READ_PHONE_STATE)) {
            if (sTelephonyManager == null) {
                sTelephonyManager = MyApplication.get().getSystemService(
                        Context.TELEPHONY_SERVICE) as TelephonyManager?
            }
        }
        return sTelephonyManager
    }



    @JvmStatic
    fun getUqid(): String {
        var temp = StringBuffer()
        if (null != getTelephonyManager()) {
            val imei = sTelephonyManager?.deviceId
            if (imei != null) {
                temp.append(imei)
            }
        }
        if (!TextUtils.isEmpty(getMacAddr())) {
            temp.append(getMacAddr())
        }

        return md5Encode(temp.toString())
    }

    /**
     * 获取设备IMEI码
     */
    @JvmStatic
    fun getIMEI(): String {
        val tm = getTelephonyManager()!!.deviceId
        if (tm == null) {
            return " "
        } else {
            return tm
        }
    }

     fun getMacAddr(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                val macBytes = nif.hardwareAddress ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
        }
        return "02:00:00:00:00:00"
    }


    @JvmStatic
    fun md5Encode(password: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest: ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
            var sb = StringBuffer()
            for (b in digest) {
                var i: Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }



    @JvmStatic
    fun checkStringUsername(string: String): Boolean {
        val r1 = Regex("^[0-9a-zA-Z]*$")
        var isCheck  = false
        if (r1.matches(string) || AlbbUtils.isEmail(string)) {
            isCheck = true
        }
        return isCheck
    }
    @JvmStatic
    fun isEmail(strEmail: String): Boolean {
        val strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$"
        return if (TextUtils.isEmpty(strPattern)) {
            false
        } else {
            strEmail.matches(Regex(strPattern))
        }
    }


    /**
     * 判断用户名是否正确*/
    @JvmStatic
    fun isUsernameCorrect(inputText: String): Boolean {
        if (TextUtils.isEmpty(inputText) || inputText.length < 5) {
            ToastUtils.show("请输入5-16位数字及字母的组合用户名")
            return false
        }
      /*  if (!Utils.checkStringUsername(inputText)) {
            ToastUtils.show("请输入5-16位数字及字母的组合用户名")
            return false
        } */
        return true
    }

    /**
     * 判断密码是否正确*/
    @JvmStatic
    fun isPasswordCorrect(inputText: String): Boolean {
        var password = inputText
        if (TextUtils.isEmpty(password) || password.length < 5) {
           // ToastUtils.show("请输入5-16位数字及字母的组合密码")
            return false
        }
        if (password.matches(Regex("^[0-9]{1,16}")) || password.matches(Regex("^[a-zA-Z]{1,16}"))) {
            // customView.showError("密码需使用数字及字母的组合")
          //  ToastUtils.show("密码需使用数字及字母的组合")
            return false
        }
        return true
    }

    @JvmStatic
    fun isCodeCorrect(inputText: String) : Boolean {
        var code = inputText
        if (TextUtils.isEmpty(code) || code.length < 6) {
            return false
        }
        return true
    }


    @JvmStatic
    fun downLoadApp(context: Context, url: String?) {

        AlbbUtils.downLoadApp(context, url, true)
    }

    @JvmStatic
    fun downLoadApp(context: Context, url: String?, cancelAble: Boolean) {

        if (TextUtils.isEmpty(url) || HttpUrl.parse(url) == null) {
            return
        }

        val updateAppBean = UpdateAppBean()

        //设置 apk 的下载地址
        updateAppBean.apkFileUrl = url
        var path: String? = ""
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()) {
            try {
                path = context?.externalCacheDir?.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            }
        } else {
            path = context?.cacheDir?.absolutePath
        }

        //设置apk 的保存路径
        updateAppBean.targetPath = path
        //实现网络接口，只实现下载就可以
        updateAppBean.httpManager = AlbbUpdateAppHttpUtil()
        UpdateAppManager.download(context, updateAppBean, object : DownloadService.DownloadCallback {

            override fun onStart() {
                AlbbHProgressDialogUtils.showHorizontalProgressDialog(context, "下载进度", false)
                AlbbHProgressDialogUtils.setCancleAble(cancelAble)
            }

            override fun onProgress(progress: Float, totalSize: Long) {
                AlbbHProgressDialogUtils.setProgress(Math.round(progress * 100))

            }

            override fun setMax(totalSize: Long) {
            }

            override fun onFinish(file: File): Boolean {
                AlbbHProgressDialogUtils.cancel()
                AppUpdateUtils.installApp(context, file)
                return false
            }

            override fun onError(msg: String) {
                AlbbHProgressDialogUtils.cancel()
            }

            override fun onInstallAppAndAppOnForeground(file: File): Boolean {
                return false
            }
        })
    }




}