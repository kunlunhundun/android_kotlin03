/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.aliData

import com.wireguard.android.Application
import com.wireguard.android.BuildConfig
import com.wireguard.android.aliData.response.CustomerObj
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.aliData.response.WireguardListResponse.VpnServiceObject

object AppConfigData {

    const val runGateUrl = "http://192.168.1.3:8080"//运营环境
   // const val runGateUrl = "http://121.196.120.24:8080"//运营环境

   // http://121.196.120.24:8080/swagger-ui.html

    const val APP_VERSION = BuildConfig.VERSION_NAME // 请求的app版本号
    const val GATEWAY_VERSION = "1.0.0" // 请求的网关版本号

    const val ACACHE_LOGIN_NAME = "ACACHE_LOGIN_NAME"
    const val ACACHE_LOGIIN_PASSWORD = "ACACHE_LOGIIN_PASSWORD"
    const val ACACHE_LOIN_TOKEN = "ACACHE_LOIN_TOKEN"
    const val ACACHE_DEVICE_ID = "ACACHE_DEVICE_ID"
    const val ACACHE_DEVICE_BRAND = "ACACHE_DEVICE_BRAND"

    var token: String? = null
    var tokenHead: String? = null
    var authorization:String? = null
    var customerInfo: CustomerObj? = null
        set(value) {
            field = value
        }
    var wireguardList: MutableList<VpnServiceObject> ? = null
        set(value) {
            field = value
        }

    @JvmStatic
    fun initAuthorization(token:String?, tokenHead:String?) {
        AppConfigData.tokenHead = tokenHead
        AppConfigData.token = token
        AppConfigData.authorization =  tokenHead + token
    }

    var loginName: String? = null
        set(value) {
            field = value
            Application.getAcache().put(ACACHE_LOGIN_NAME, field)
        }
        get() {
            if (field == null) {
                field = Application.getAcache().getAsString(ACACHE_LOGIN_NAME)
            }
            return field
        }

    var password: String? = null
        set(value) {
            field = value
            Application.getAcache().put(ACACHE_LOGIIN_PASSWORD, field)
        }
        get() {
            if (field == null) {
                field = Application.getAcache().getAsString(ACACHE_LOGIIN_PASSWORD)
            }
            return field
        }
    var deviceId: String? = null
        set(value) {
            field = value
            Application.getAcache().put(ACACHE_DEVICE_ID, field)
        }
        get() {
            if (field == null) {
                field = Application.getAcache().getAsString(ACACHE_DEVICE_ID)
            }
            return field
        }
    var deviceBrand: String? = null
        set(value) {
            field = value
            Application.getAcache().put(ACACHE_DEVICE_BRAND, field)
        }
        get() {
            if (field == null) {
                field = Application.getAcache().getAsString(ACACHE_DEVICE_BRAND)
            }
             return field
        }
    var loginToken: String? = null
        set(value) {
            field = value
            Application.getAcache().put(ACACHE_LOIN_TOKEN, field)
        }
        get() {
            if (field == null) {
                field = Application.getAcache().getAsString(ACACHE_LOIN_TOKEN)
            }
            return field
        }

}