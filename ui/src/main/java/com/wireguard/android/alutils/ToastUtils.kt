/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alutils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.wireguard.android.Application


object ToastUtils {
    private var mToast: Toast? = null

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 2000ms
     * @param charSequence 字符串
     */

    fun show(charSequence: CharSequence) {

        if (mToast == null) {
            var context =  Application.get()?.baseContext
            mToast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(charSequence)
            mToast!!.duration = Toast.LENGTH_SHORT
        }
        mToast?.show()
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 3500ms
     * @param charSequence 字符串
     */
    fun showLong(charSequence: CharSequence) {
        if (mToast == null) {
            mToast = Toast.makeText(Application.get()?.baseContext, charSequence, Toast.LENGTH_LONG)
        } else {
            mToast!!.setText(charSequence)
            mToast!!.duration = Toast.LENGTH_LONG
        }

        mToast?.show()
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 2000ms
     * @param resId String资源ID
     */
    fun show(resId: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(Application.get()?.baseContext, resId, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(resId)
            mToast!!.duration = Toast.LENGTH_SHORT
        }

        mToast?.show()
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 3500ms
     * @param resId String资源ID
     */
    fun showLong(resId: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(Application.get()?.baseContext, resId, Toast.LENGTH_LONG)
        } else {
            mToast!!.setText(resId)
            mToast!!.duration = Toast.LENGTH_LONG
        }

        mToast?.show()
    }

    /**
     * 取消Toast显示
     */
    fun cancelToast() {
        mToast?.cancel()

    }

}
