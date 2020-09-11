/*
 */

package com.sunshinesky.android.albbInterface
import com.sunshinesky.android.backend.Tunnel

public interface OnListenerObservableTunnel {

    fun onStateChanged(newState: Tunnel.State?)

}