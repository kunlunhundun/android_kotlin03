

package com.sunshinesky.android.albbData.net

import com.sunshinesky.android.albbData.AlbbAppConfigData
import com.sunshinesky.android.albbUtils.AlbbLogUtils
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class AlbbSignInterceptor : Interceptor {

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
                    .addHeader("token", AlbbAppConfigData.token?:"")
                    .addHeader("gatev", AlbbAppConfigData.GATEWAY_VERSION?:"")
                    .addHeader("appv", AlbbAppConfigData.APP_VERSION?:"")
                    .addHeader("deviceId", AlbbAppConfigData.deviceId?:"")
                    .addHeader("deviceBrand", AlbbAppConfigData.deviceBrand?:"")
                    .addHeader("deviceSystem", AlbbAppConfigData.deviceSystem?:"")
                    .addHeader("OsType", "android")
                    .addHeader("Authorization", AlbbAppConfigData.authorization?:"")
                    .addHeader("Content-Type","application/json")
                    .build()
        }

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            AlbbLogUtils.e("<-- HTTP FAILED: $e")
            throw e
        }
        return response

    }

}