/*
 */

package com.supersunstars.android.DjiModel

import android.graphics.drawable.Drawable

data class AppPackageModel(
        var icon:Drawable? = null,
        var name: String? = null,
        var packageName: String? =null,
        var itemType: Int = 0,
        var isCheck: Boolean = false
)


