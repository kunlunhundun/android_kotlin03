

package com.sunshinesky.android.albbData.net

import android.app.Activity
import com.sunshinesky.android.albbData.response.AlbbBaseResponseObject
import com.sunshinesky.android.albbUtils.AlbbLogUtils

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class AlbbApiResponse<T: AlbbBaseResponseObject>(private val context: Activity, private val showProgress:Boolean) : Observer<T> {

    constructor( context: Activity):this(context,true)
    abstract fun businessFail(data: T)
    abstract fun businessSuccess(data: T)
    abstract fun failure(statusCode: Int, albbApiErrorModel: AlbbApiErrorModel)

    override fun onSubscribe(d: Disposable) {
        if (showProgress){
            AlbbLogUtils.e("onSubscribe----> " + context.localClassName)
            AlbbLoadingDialog.show(context)
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
            AlbbLoadingDialog.cancel()
        }

    }

    override fun onError(e: Throwable) {
        if (showProgress){
            AlbbLoadingDialog.cancel()
        }

        if (e is HttpException) {
            val albbApiErrorModel: AlbbApiErrorModel = when (e.code()) {
                AlbbApiErrorType.INTERNAL_SERVER_ERROR.code ->{
                    AlbbApiErrorType.INTERNAL_SERVER_ERROR.getApiErrorModel(context)
                }

                AlbbApiErrorType.BAD_GATEWAY.code ->
                    AlbbApiErrorType.BAD_GATEWAY.getApiErrorModel(context)
                AlbbApiErrorType.NOT_FOUND.code ->
                    AlbbApiErrorType.NOT_FOUND.getApiErrorModel(context)
                AlbbApiErrorType.NETWORK_NOT_CONNECT.code ->
                    AlbbApiErrorType.NETWORK_NOT_CONNECT.getApiErrorModel(context)
                else -> otherError(e)

            }
//            if (e.code()!=700){
            failure(e.code(), albbApiErrorModel)
//            }

            return
        }

        val apiErrorType: AlbbApiErrorType = when (e) {
            is UnknownHostException -> AlbbApiErrorType.UNKNOWN_HOST_EXCEPTION
            is ConnectException -> AlbbApiErrorType.NETWORK_NOT_CONNECT
            is SocketTimeoutException -> AlbbApiErrorType.CONNECTION_TIMEOUT
            else -> AlbbApiErrorType.UNEXPECTED_ERROR
        }
//        if (apiErrorType.code!=700){
        failure(apiErrorType.code, apiErrorType.getApiErrorModel(context))
//        }

        //未知错误打印错误日志
        if (apiErrorType == AlbbApiErrorType.UNEXPECTED_ERROR) {
            AlbbLogUtils.e("${e.cause}")
            AlbbLogUtils.e("${e.message}")
            e.stackTrace.forEach {
                AlbbLogUtils.e("${it}")
            }
        }

    }

    private fun otherError(e: HttpException) = AlbbApiErrorModel(e.code(),e.message())

}