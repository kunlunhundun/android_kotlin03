/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.aliData.net

import com.wireguard.android.aliData.request.BaseRequest
import com.wireguard.android.aliData.response.LoginResponse
import com.wireguard.android.aliData.request.UsernameLoginRequest
import com.wireguard.android.aliData.response.BaseResponseObject
import com.wireguard.android.aliData.response.MessageInfoResonse
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
     * use 1 注册 2 找回密码
     */
    @POST("mail/sendCode")
    fun sendEmailCode(@Query("username") username: String, @Query("use") use: Int): Observable<BaseResponseObject>
    /**
     * 修改密码
     */
    @POST("sso/updatePassword")
    fun updatePassword(@Query("username") username: String, @Query("password") password:String, @Query("code") code:String): Observable<BaseResponseObject>


    /**
     * 连接成功
     */
    @POST("vpnInfo/connected")
    fun connected(@Query("id") id: String, @Query("serviceId") serviceId:String): Observable<BaseResponseObject>

    /**
     * 断开连接
     */
    @POST("vpnInfo/disConnect")
    fun disConnect(@Query("id") id: String, @Query("serviceId") serviceId:String): Observable<BaseResponseObject>

    /**
     * 用户提交反馈内容
     */
    @POST("comment/sendFeedBack")
    fun sendFeedBack(@Query("commentType") commentType: Int, @Query("content") content:String): Observable<BaseResponseObject>

    /**
     * 获取当前用户所有的信息反馈以及官方的回复
     */
    @POST("comment/getMessage")
    fun getMessage(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize:Int): Observable<MessageInfoResonse>


}