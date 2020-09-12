
package com.sunshinesky.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.widget.TextView
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

/**
 * Standalone utilities for interacting with the system clipboard.
 */
object DjioClipboardUtils {
    @JvmStatic
    fun copyTextView(view: View) {
        val data = when (view) {
            is TextInputEditText -> Pair(view.editableText, view.hint)
            is TextView -> Pair(view.text, view.contentDescription)
            else -> return
        }
        val service1 = view.context.getSystemService<ClipboardManager>() ?: return
        service1.setPrimaryClip(ClipData.newPlainText(data.second, data.first))
        Snackbar.make(view, "${data.second} copied to ", Snackbar.LENGTH_LONG).show()
        if (data.first == null || data.first.isEmpty()) {
            return
        }
        val service = view.context.getSystemService<ClipboardManager>() ?: return
        service.setPrimaryClip(ClipData.newPlainText(data.second, data.first))
        Snackbar.make(view, "${data.second} copied to clipboard", Snackbar.LENGTH_LONG).show()
    }
}
