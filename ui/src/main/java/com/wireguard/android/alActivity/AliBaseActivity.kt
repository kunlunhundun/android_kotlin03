/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alActivity

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.wireguard.android.R

abstract class AliBaseActivity : RxAppCompatActivity() {

    private var mToolbarTitle: TextView? = null
    private var mToolbarAction: TextView? = null
    private var mToolbarIcon: ImageView? = null
    private var mToolbar: Toolbar? = null
    private var mClickListener: View.OnClickListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        mToolbar = findViewById<Toolbar>(R.id.toolbar)
        mToolbarIcon = findViewById(R.id.toolbar_icon)
        mToolbarTitle = findViewById(R.id.toolbar_title)
        mToolbarAction = findViewById(R.id.action_area)
        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar)
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
//            mToolbarTitle?.text = title
            //设置默认的标题不显示
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        mToolbarIcon?.setOnClickListener {
            if (mClickListener == null) {
                finish()
            } else {
                mClickListener?.onClick(it)
            }
        }
    }

    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml referenceId
     */
    protected abstract fun getLayoutId(): Int

    protected fun setHasBackArrow(hasBackArrow: Boolean) {
        mToolbarIcon?.visibility =  if (hasBackArrow)  View.VISIBLE else View.GONE
    }

    protected fun setTile(title: String) {
        if (mToolbar != null) {
            //将Toolbar显示到界面
            mToolbarTitle?.text = title
        }
    }

    protected fun setBackListener(listener: View.OnClickListener) {
        mClickListener = listener
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}