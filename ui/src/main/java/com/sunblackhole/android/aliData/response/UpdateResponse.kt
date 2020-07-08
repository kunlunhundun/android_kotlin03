package com.sunblackhole.android.aliData.response

class UpdateResponse : BaseResponseObject() {

    val data : UpdateObj? = null

    class UpdateObj {

        //0 不更新 已经是最新版本,1 普通更新， 2 强制更新
        var mustUpdate: Int? = null
        var fileName: String? = null
        var v: String? = null //更新的版本号

    }
}