/*
 */

package com.sunshinesky.android.AlbbFragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.sunshinesky.android.R
import com.trello.rxlifecycle2.components.support.RxFragment

open class AlbbToolbarFragment : RxFragment() {
   open  lateinit var toolbar: Toolbar
    open  lateinit var toolbar1: Toolbar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)

        toolbar1 = view.findViewById(R.id.toolbar)

    }

    open fun onBackPressed(): Boolean = false

    open fun onBackPressedtianya(): Boolean = true
}