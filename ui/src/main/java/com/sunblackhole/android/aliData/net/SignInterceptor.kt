

package com.sunblackhole.android.aliData.net

import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.alutils.LogUtils
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class SignInterceptor : Interceptor {

    constructor() {
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        if (hasRequestBody) {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)


            request = request.newBuilder()
                    .addHeader("token", AppConfigData.token?:"")
                    .addHeader("gatev", AppConfigData.GATEWAY_VERSION?:"")
                    .addHeader("appv", AppConfigData.APP_VERSION?:"")
                    .addHeader("deviceId", AppConfigData.deviceId?:"")
                    .addHeader("deviceBrand", AppConfigData.deviceBrand?:"")
                    .addHeader("deviceSystem", AppConfigData.deviceSystem?:"")
                    .addHeader("OsType", "android")
                    .addHeader("Authorization", AppConfigData.authorization?:"")
                    .addHeader("Content-Type","application/json")
                    .build()
        }

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            LogUtils.e("<-- HTTP FAILED: $e")
            throw e
        }
        return response

    }

}