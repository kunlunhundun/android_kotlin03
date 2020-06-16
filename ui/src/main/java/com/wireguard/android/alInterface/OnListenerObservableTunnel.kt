/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alInterface
import com.wireguard.android.backend.Tunnel

public interface OnListenerObservableTunnel {

    fun onStateChanged(newState: Tunnel.State?)

}