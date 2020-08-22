/*
 */

package com.supersunstars.android.DjiFragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.supersunstars.android.R
import com.trello.rxlifecycle2.components.support.RxFragment

open class DjiToolbarFragment : RxFragment() {
   open  lateinit var toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)

    }

    open fun onBackPressed(): Boolean = false
}