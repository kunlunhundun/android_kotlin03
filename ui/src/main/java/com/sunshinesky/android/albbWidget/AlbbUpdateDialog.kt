package com.sunshinesky.android.albbWidget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.gyf.barlibrary.ImmersionBar
import com.sunshinesky.android.R
import com.sunshinesky.android.albbData.response.AlbbUpdateResponse
import com.sunshinesky.android.albbUtils.AlbbLogUtils
import com.sunshinesky.android.albbUtils.AlbbUtils

class AlbbUpdateDialog : DialogFragment(){

    private var immersionBar: ImmersionBar? = null
    private var updateV: TextView? = null
    private var cancelBtn: Button? = null
    private var updateBtn: Button? = null
    private var mustUpdateBtn: Button? = null
    private var fileName: String? = null
    public var mUpdateObj: AlbbUpdateResponse.UpdateObj? = null

   // constructor(iv: ImageView, frameRes: IntArray, duration: Int, isRepeat: Boolean) {


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable((ColorDrawable(Color.TRANSPARENT)))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setBackgroundDrawable((ColorDrawable(Color.TRANSPARENT)))
        dialog?.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        immersionBar = ImmersionBar.with(this, dialog!!).statusBarDarkFont(false)
        immersionBar?.init()
        var rootView = inflater.inflate(R.layout.albb_update_dialog, null)

        updateV = rootView.findViewById(R.id.tv_update_v)
        cancelBtn = rootView.findViewById(R.id.btn_cancel)
        updateBtn = rootView.findViewById(R.id.btn_update)
        mustUpdateBtn = rootView.findViewById(R.id.btn_must_update)

        cancelBtn?.setOnClickListener {
            dismissAllowingStateLoss()
            mOnClickCancelListener?.onClickListenerCancel()
        }
        updateBtn?.setOnClickListener {
            if (fileName?.length ?: 0 > 1 ) {
                AlbbUtils.downLoadApp(this.requireContext(),fileName)
                dismissAllowingStateLoss()
            }
        }
        mustUpdateBtn?.setOnClickListener {
            if (fileName?.length ?: 0 > 1 ) {
                AlbbUtils.downLoadApp(this.requireContext(),fileName)
                dismissAllowingStateLoss()
            }
        }
        updateData(mUpdateObj)

//        Handler().postDelayed({
//            updateData(mUpdateObj)
//        },1100)

        return rootView
    }

    fun updateData( updateObj: AlbbUpdateResponse.UpdateObj?) {

        AlbbLogUtils.e("Discover a new version v" + updateObj?.v + "------" + updateV?.text)
        updateV?.text = "Discover a new version v" + updateObj?.v
        fileName = updateObj?.fileName?.replace("localhost","192.168.1.3")
        mUpdateObj = updateObj

        if (updateObj?.mustUpdate == 1) {
            mustUpdateBtn?.visibility = View.GONE
            updateBtn?.visibility = View.VISIBLE
            cancelBtn?.visibility = View.VISIBLE
        } else if (updateObj?.mustUpdate == 2) {
            mustUpdateBtn?.visibility = View.VISIBLE
            updateBtn?.visibility = View.GONE
            cancelBtn?.visibility = View.GONE
        }

    }

    private var mOnClickCancelListener: OnClickCancelListener? = null
    fun setOnCancelListener(l: OnClickCancelListener?) {
        mOnClickCancelListener = l
    }

    public interface OnClickCancelListener {
        fun onClickListenerCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }

}