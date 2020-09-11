

package com.sunshinesky.android.albbData.net

import android.content.Context
import com.sunshinesky.android.R

import androidx.annotation.StringRes

enum class AlbbApiErrorType(val code: Int, @param: StringRes private val messageId: Int) {
    INTERNAL_SERVER_ERROR(500, R.string.service_error),
    BAD_GATEWAY(502, R.string.service_error),
    NOT_FOUND(404, R.string.not_found),
    CONNECTION_TIMEOUT(408, R.string.timeout),
    NETWORK_NOT_CONNECT(504, R.string.network_wrong),
    UNEXPECTED_ERROR(700, R.string.unexpected_error),
    UNKNOWN_HOST_EXCEPTION(800, R.string.unknown_host_error);


    fun getApiErrorModel(context: Context): AlbbApiErrorModel {
        return AlbbApiErrorModel(code, context.getString(messageId))
    }

}
