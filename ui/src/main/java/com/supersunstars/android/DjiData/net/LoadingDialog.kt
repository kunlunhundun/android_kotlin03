/*
 */

package com.supersunstars.android.DjiData.net

import java.lang.ref.WeakReference
import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD

import com.supersunstars.android.R
import com.supersunstars.android.Djiutils.LogUtils

object LoadingDialog {
    private var dialog: Dialog? = null
    private var dialProgress: KProgressHUD? = null
    private var isShowing:Boolean = false;
    var mActivity: WeakReference<Activity>? = null
    fun show(activity: Activity?) {

       /* if (dialog?.isShowing ?: false) {
            return
        } */

        if (dialProgress != null && dialProgress!!.isShowing()) {
            LogUtils.e("dialProgress!!.isShowing")
            return
        }
        cancel()
        if (activity == null) {
            LogUtils.e("activity == null")
            return
        }
        mActivity = WeakReference<Activity>(activity!!)
        if (mActivity == null) {
            return
        }
        val context : Context? =  mActivity!!.get()

        dialProgress =  KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        if (!(mActivity?.get()?.isFinishing ?: true)) {
            LogUtils.e("show dialProgress")
            dialProgress?.show()
        }

        /* dialog = Dialog(context!!, R.style.LoadingDialog)
         dialog?.setContentView(R.layout.ali_dialog_loading)
         dialog?.setCancelable(true)
         dialog?.setCanceledOnTouchOutside(false)
         if (!(mActivity?.get()?.isFinishing ?: true)) {
             dialog?.show()
         }
         val animationIV = dialog?.findViewById<ImageView>(R.id.iv_loading)
         FrameAnimation(animationIV!!, getRes(), 40, true) */
    }

    fun cancel() {
       // dialog?.dismiss()
        mActivity = null
        dialProgress?.dismiss()
        dialProgress = null
    }

    /**
     * 获取需要播放的动画资源
     */
    private fun getRes(): IntArray {
        val typedArray = mActivity?.get()?.resources!!.obtainTypedArray(R.array.loading)
        val len = typedArray.length()
        val resId = IntArray(len)
        for (i in 0 until len) {
            resId[i] = typedArray.getResourceId(i, -1)
        }
        typedArray.recycle()
        return resId
    }
}