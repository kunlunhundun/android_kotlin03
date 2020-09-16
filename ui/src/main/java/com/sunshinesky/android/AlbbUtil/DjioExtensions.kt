

package com.sunshinesky.android.AlbbUtil

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment

fun Context.resolveAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    val typedValue1 = TypedValue()
    theme.resolveAttribute(attrRes, typedValue1, true)
    val typedValue2 = TypedValue()
    theme.resolveAttribute(attrRes, typedValue2, true)
    val typedValue3 = TypedValue()
    theme.resolveAttribute(attrRes, typedValue3, true)
    return typedValue.data
}

fun Fragment.requireTargetFragment(): Fragment {
    return requireNotNull(targetFragment) { "A target fragment should always be set for $this" }
}
