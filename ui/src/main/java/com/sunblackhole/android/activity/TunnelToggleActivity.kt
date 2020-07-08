/*
 * Copyright © 2017-2019 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.sunblackhole.android.activity

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.sunblackhole.android.Application
import com.sunblackhole.android.QuickTileService
import com.sunblackhole.android.R
import com.sunblackhole.android.backend.Tunnel
import com.sunblackhole.android.util.ErrorMessages

@RequiresApi(Build.VERSION_CODES.N)
class TunnelToggleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tunnel = Application.getTunnelManager().lastUsedTunnel ?: return
        tunnel.setStateAsync(Tunnel.State.TOGGLE).whenComplete { _, t ->
            TileService.requestListeningState(this, ComponentName(this, QuickTileService::class.java))
            onToggleFinished(t)
            finishAffinity()
        }
    }

    private fun onToggleFinished(throwable: Throwable?) {
        if (throwable == null) return
        val error = ErrorMessages[throwable]
        val message = getString(R.string.toggle_error, error)
        Log.e(TAG, message, throwable)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "WireGuard/TunnelToggleActivity"
    }
}
