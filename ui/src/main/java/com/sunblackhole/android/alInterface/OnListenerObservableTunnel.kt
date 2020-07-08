/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alInterface
import com.sunblackhole.android.backend.Tunnel

public interface OnListenerObservableTunnel {

    fun onStateChanged(newState: Tunnel.State?)

}