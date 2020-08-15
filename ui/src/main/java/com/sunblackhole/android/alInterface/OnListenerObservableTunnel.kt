/*
 */

package com.sunblackhole.android.alInterface
import com.sunblackhole.android.backend.Tunnel

public interface OnListenerObservableTunnel {

    fun onStateChanged(newState: Tunnel.State?)

}