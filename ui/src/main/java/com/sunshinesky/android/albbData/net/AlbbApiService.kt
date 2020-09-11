/*
 */

package com.sunshinesky.android.albbData.net

import com.sunshinesky.android.albbData.request.AlbbUsernameLoginRequest
import com.sunshinesky.android.albbData.response.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AlbbApiService {

    /**
     * 会员注册接口
     */
    @POST("sso/register")
    fun register(@Body albbUsernameRegisterRequest: AlbbUsernameLoginRequest): Observable<AlbbLoginResponseAlbb>


    /**
     * 会员登录接口
     */
    @POST("sso/login")
    fun login(@Body albbUsernameRegisterRequest: AlbbUsernameLoginRequest): Observable<AlbbLoginResponseAlbb>

    /**
     * 会员facebook登录接口
     */
    @POST("sso/loginByFacebook")
    fun loginByFacebook(@Query("username") username: String): Observable<AlbbLoginResponseAlbb>

    /**
     * 会员登录接口
     */
    @POST("sso/loginByToken")
    fun loginByToken(@Query("username") username: String, @Query("loginToken") loginToken:String): Observable<AlbbLoginResponseAlbb>

    /**
     * 会员登录接口
     */
    @POST("vpnInfo/getLineWireguard")
    fun getLineWireguard(@Query("username") username: String): Observable<AlbbWireguardListResponseAlbb>

    /**
     * 会员注册接口
     * use 1 注册 2 找回密码
     */
    @POST("mail/sendCode")
    fun sendEmailCode(@Query("username") username: String, @Query("use") use: Int): Observable<AlbbBaseResponseObject>
    /**
     * 修改密码
     */
    @POST("sso/updatePassword")
    fun updatePassword(@Query("username") username: String, @Query("password") password:String, @Query("code") code:String): Observable<AlbbBaseResponseObject>


    /**
     * 连接成功
     */
    @POST("vpnInfo/connected")
    fun connected(@Query("id") id: String, @Query("serviceId") serviceId:String): Observable<AlbbBaseResponseObject>

    /**
     * 断开连接
     */
    @POST("vpnInfo/disConnect")
    fun disConnect(@Query("id") id: String, @Query("serviceId") serviceId:String): Observable<AlbbBaseResponseObject>

    /**
     * 断开连接
     */
    @POST("vpnInfo/filterApp")
    fun filterApp(@Query("filterType") id: Int, @Query("appName") appName:String, @Query("installAppNames") installAppNames:String): Observable<AlbbBaseResponseObject>


    /**
     * 用户提交反馈内容
     */
    @POST("comment/sendFeedBack")
    fun sendFeedBack(@Query("commentType") commentType: Int, @Query("content") content:String): Observable<AlbbBaseResponseObject>

    /**
     * 获取当前用户所有的信息反馈以及官方的回复
     */
    @POST("comment/getMessage")
    fun getMessage(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize:Int): Observable<AlbbMessageInfoResonse>


    /**
     * 更新平台版本
     * platfor 0-pc 1-android 2-ios
     */
    @POST("update/checkVersion")
    fun checkVersion(@Query("v") v: String, @Query("platform") platform:String): Observable<AlbbUpdateResponse>

    /**
     * 获取当前用户所有的信息反馈以及官方的回复的数量
     */
    @POST("comment/queryReplyCount")
    fun queryReplyCount(@Query("username") username : String): Observable<AlbbQueryReplyCountResponseAlbb>


}