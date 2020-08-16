

package com.sunblackhole.android.aliData.net

import android.app.Activity
import com.sunblackhole.android.aliData.response.BaseResponseObject
import com.sunblackhole.android.alutils.LogUtils

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class ApiResponse<T: BaseResponseObject>(private val context: Activity, private val showProgress:Boolean) : Observer<T> {

    constructor( context: Activity):this(context,true)
    abstract fun businessFail(data: T)
    abstract fun businessSuccess(data: T)
    abstract fun failure(statusCode: Int, apiErrorModel: ApiErrorModel)

    override fun onSubscribe(d: Disposable) {
        if (showProgress){
            LogUtils.e("onSubscribe----> " + context.localClassName)
            LoadingDialog.show(context)
        }

    }

    override fun onNext(t: T) {

        when(t.code){
            "200" ->{businessSuccess(t)}
            "GW_890206" ->{
               // MyApplication.getinstance().loginOut()
            }
            else ->{

                businessFail(t)
            }
        }

    }

    override fun onComplete() {
        if (showProgress){
            LoadingDialog.cancel()
        }

    }

    override fun onError(e: Throwable) {
        if (showProgress){
            LoadingDialog.cancel()
        }

        if (e is HttpException) {
            val apiErrorModel: ApiErrorModel = when (e.code()) {
                ApiErrorType.INTERNAL_SERVER_ERROR.code ->{
                    ApiErrorType.INTERNAL_SERVER_ERROR.getApiErrorModel(context)
                }

                ApiErrorType.BAD_GATEWAY.code ->
                    ApiErrorType.BAD_GATEWAY.getApiErrorModel(context)
                ApiErrorType.NOT_FOUND.code ->
                    ApiErrorType.NOT_FOUND.getApiErrorModel(context)
                ApiErrorType.NETWORK_NOT_CONNECT.code ->
                    ApiErrorType.NETWORK_NOT_CONNECT.getApiErrorModel(context)
                else -> otherError(e)

            }
//            if (e.code()!=700){
            failure(e.code(), apiErrorModel)
//            }

            return
        }

        val apiErrorType: ApiErrorType = when (e) {
            is UnknownHostException -> ApiErrorType.UNKNOWN_HOST_EXCEPTION
            is ConnectException -> ApiErrorType.NETWORK_NOT_CONNECT
            is SocketTimeoutException -> ApiErrorType.CONNECTION_TIMEOUT
            else -> ApiErrorType.UNEXPECTED_ERROR
        }
//        if (apiErrorType.code!=700){
        failure(apiErrorType.code, apiErrorType.getApiErrorModel(context))
//        }

        //未知错误打印错误日志
        if (apiErrorType == ApiErrorType.UNEXPECTED_ERROR) {
            LogUtils.e("${e.cause}")
            LogUtils.e("${e.message}")
            e.stackTrace.forEach {
                LogUtils.e("${it}")
            }
        }

    }

    private fun otherError(e: HttpException) = ApiErrorModel(e.code(),e.message())

}