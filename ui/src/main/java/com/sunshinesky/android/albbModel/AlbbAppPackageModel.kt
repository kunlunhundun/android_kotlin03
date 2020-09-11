/*
 */

package com.sunshinesky.android.albbModel

import android.graphics.drawable.Drawable

data class AlbbAppPackageModel(
        var icon:Drawable? = null,
        var name: String? = null,
        var packageName: String? =null,
        var itemType: Int = 0,
        var isCheck: Boolean = false
)


