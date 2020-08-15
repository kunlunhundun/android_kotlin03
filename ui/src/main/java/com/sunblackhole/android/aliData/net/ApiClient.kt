/*
 */

package com.sunblackhole.android.aliData.net

import com.sunblackhole.android.Application
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.alutils.LogUtils
import com.sunblackhole.android.alutils.NetWorkUtil
import com.sunblackhole.android.alutils.SSLSocketFactoryUtil
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.*
//import org.litepal.crud.DataSupport.isExist
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class ApiClient private constructor() {

    private val TAG = "ApiClient"

    //短缓存有效期为10分钟
    private val CACHE_STALE_SHORT = 60 * 3

    //长缓存有效期为7天
    private val CACHE_STALE_LONG = 60 * 60 * 24 * 7

    lateinit var service: ApiService

    private object Holder {
        val INSTANCE = ApiClient()
    }

    companion object {
        val instance by lazy { Holder.INSTANCE }
    }


    fun init() {

        var simulateResponseInterceptor = SimulateResponseInterceptor()
        NetApiInterceptorList.init()

        val cache = Cache(File(Application.get().externalCacheDir, "File_VPN"), 10 * 1024 * 1024)
        val mOkHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .retryOnConnectionFailure(false)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(
//                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
//                        else HttpLoggingInterceptor.Level.NONE
                        HttpLoggingInterceptor.Level.BODY
                ))
                .sslSocketFactory(SSLSocketFactoryUtil.createSSLSocketFactory())
                .addNetworkInterceptor(rewriteCacheControlInterceptor)
                .hostnameVerifier( SSLSocketFactoryUtil.TrustAllHostnameVerifier())
                .addInterceptor(baseInterceptor)
               // .addInterceptor(simulateResponseInterceptor)
                .addInterceptor(SignInterceptor())
                .build()



        val retrofit = Retrofit.Builder()
                .baseUrl(AppConfigData.runGateUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build()

        service = retrofit.create(ApiService::class.java)

    }

    /**
     * 获取缓存
     */
    private var baseInterceptor: Interceptor = Interceptor { chain ->
        var request = chain.request()
        if (!NetWorkUtil.isNetWorkConnected()) {
            /**
             * 离线缓存控制  总的缓存时间=在线缓存时间+设置离线缓存时间
             */

            val tempCacheControl = CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(CACHE_STALE_LONG, TimeUnit.SECONDS)
                    .build()
            request = request.newBuilder()
                    .cacheControl(tempCacheControl)
                    .build()
            LogUtils.i(TAG, "intercept:no network ")
        }
        chain.proceed(request)
    }


    private var rewriteCacheControlInterceptor: Interceptor = Interceptor { chain ->
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        originalResponse.newBuilder()
                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=$CACHE_STALE_SHORT")
                .build()
    }


    //模拟响应拦截器
    inner class SimulateResponseInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var url = request.url()
            var parameter:RequestBody? =  request.body()

            val buffer:okio.Buffer = okio.Buffer()
            parameter?.writeTo(buffer)
            var charset:Charset? = Charsets.UTF_8

            var contentType = parameter?.contentType()
            if (contentType != null) {
                var tempCharset = Charset.forName("UTF-8")
                charset = contentType?.charset(tempCharset)
            }
            var parameterStr = buffer.readString(charset)

            val apiName = NetApiInterceptorList.isExist(url.toString(),parameterStr)
            if (apiName != null) {
                val responseContent = NetApiInterceptorList.getVisualResponseByApi(apiName) ?: ""
                return  Response.Builder().code(200)
                        .message("成功")
                        .body(ResponseBody.create(MediaType.parse("UTF-8"),responseContent))
                        .protocol(Protocol.HTTP_1_0)
                        .request(request)
                        .build()
            }
            else{
                return  Response.Builder().code(400)
                        .message("失败")
                        .body(ResponseBody.create(MediaType.parse("UTF-8"),""))
                        .request(request)
                        .protocol(Protocol.HTTP_1_0)
                        .build()
            }
            return chain.proceed(request)
        }

    }


    object  NetApiInterceptorList {

        var apiResponseMap: HashMap<String, String> = HashMap()
        var keyParameterS: MutableList<String> = mutableListOf()

        @JvmStatic
        fun isExist(apiName: String, parameter: String?): String? {

            var keyName = apiName
            if (apiName.contains(SIMULATEAPI_SENDCODE) ) {
                keyParameterS.forEach {
                    if (parameter?.contains(it) == true) {
                        keyName = it
                    }
                }
            }

            apiResponseMap.forEach {
                if (keyName.contains(it.key)) {
                    return it.key
                }
            }
            return null
        }

        @JvmStatic
        fun getVisualResponseByApi(apiName: String): String? {

            return apiResponseMap.get(apiName)
        }

        fun init() {

            keyParameterS.add(SIMULATEAPI_SENDCODE)
            keyParameterS.add(SIMULATEAPI_LOGIN)

            apiResponseMap.put(SIMULATEAPI_SENDCODE, "{\"body\":{\"expire\":900,\"messageId\":\"8b2f64129b6f48f0b2ce336cfe798b8b\",\"mobileNo\":\"13********9\"},\"head\":{\"cost\":715,\"errCode\":\"0000\",\"errMsg\":\"操作成功\"}}")

        }
        const val SIMULATEAPI_SENDCODE = "sms/sendCode"
        const val SIMULATEAPI_LOGIN = "customer/loginByAccount"

    }



}