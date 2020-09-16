/*
 */
package com.sunshinesky.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sunshinesky.android.backend.Backend
import com.sunshinesky.android.backend.WgQuickBackend
import com.sunshinesky.android.AlbbUtil.ExceptionLoggers

class AlbbBootShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        MyApplication.getBackendAsync().thenAccept { backend: Backend? ->
            if (backend !is WgQuickBackend) return@thenAccept
            val action = intent.action ?: return@thenAccept
            val tunnelManager = MyApplication.getTunnelManager()
            if (Intent.ACTION_BOOT_COMPLETED == action) {
                Log.i(TAG, "Broadcast receiver restoring state (boot)")
                tunnelManager.restoreState(false).whenComplete(ExceptionLoggers.D)
            } else if (Intent.ACTION_SHUTDOWN == action) {
                Log.i(TAG, "Broadcast receiver saving state (shutdown)")
                tunnelManager.saveState()
            }
        }
    }

    companion object {
        private const val TAG = "CattleVPN/AlbbBootShutdownReceiver"
    }
}
