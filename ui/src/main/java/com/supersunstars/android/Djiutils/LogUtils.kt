/*
 */

package com.supersunstars.android.Djiutils

import android.util.Log

object LogUtils {

    val TAG = "tianming"

    @JvmStatic
    fun w(msg: String) {
        Log.w(TAG, msg)
    }

    @JvmStatic
    fun e(msg: String) {
        Log.e(TAG, msg)
    }

    @JvmStatic
    fun d(msg: String) {
        Log.d(TAG, msg)
    }

    @JvmStatic
    fun v(msg: String) {
        Log.v(TAG, msg)
    }

    @JvmStatic
    fun i(msg: String) {
        Log.i(TAG, msg)
    }


    @JvmStatic
    fun w(tag: String,msg: String) {
        Log.w(tag, msg)
    }

    @JvmStatic
    fun e(tag: String,msg: String) {
        Log.w(tag, msg)
    }

    @JvmStatic
    fun d(tag: String,msg: String) {
        Log.w(tag, msg)
    }

    @JvmStatic
    fun v(tag: String,msg: String) {
        Log.w(tag, msg)
    }

    @JvmStatic
    fun i(tag: String,msg: String) {
        Log.w(tag, msg)
    }



    @JvmStatic
    fun e(tag:String,msg: String,e: Exception) {
        Log.e(tag, msg+ e.stackTrace)
    }
}