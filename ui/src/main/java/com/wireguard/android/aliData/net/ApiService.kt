/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.aliData.net

import com.wireguard.android.aliData.request.BaseRequest
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.aliData.request.UsernameLoginRequest
import com.wireguard.android.aliData.response.WireguardListResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    /**
     * 会员注册接口
     */
    @POST("sso/register")
    fun register(@Body usernameRegisterRequest: UsernameLoginRequest): Observable<LoginResponse>

    /**
     * 会员登录接口
     */
    @POST("sso/login")
    fun login(@Body usernameRegisterRequest: UsernameLoginRequest): Observable<LoginResponse>


    /**
     * 会员登录接口
     */
    @POST("sso/loginByToken")
    fun loginByToken(@Query("username") username: String, @Query("loginToken") loginToken:String): Observable<LoginResponse>

    /**
     * 会员登录接口
     */
    @POST("vpnInfo/getLineWireguard")
    fun getLineWireguard(@Query("username") username: String): Observable<WireguardListResponse>

    /**
     * 会员注册接口
     */
    @POST("sms/sendCode")
    fun sendCode(@Body usernameRegisterRequest: UsernameLoginRequest): Observable<LoginResponse>




}