/*
 */

package com.supersunstars.android.DjiInterface
import com.supersunstars.android.backend.Tunnel

public interface OnListenerObservableTunnel {

    fun onStateChanged(newState: Tunnel.State?)

}