
package com.supersunstars.android.model

import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.supersunstars.android.BR
import com.supersunstars.android.databinding.Keyed

class ApplicationData(val icon: Drawable, val name: String, val packageName: String, isSelected: Boolean) : BaseObservable(), Keyed<String> {
    override val key = name

    @get:Bindable
    var isSelected = isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}
