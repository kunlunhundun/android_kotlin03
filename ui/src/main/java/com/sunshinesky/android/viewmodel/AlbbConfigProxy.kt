
package com.sunshinesky.android.viewmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.sunshinesky.config.BadConfigException
import com.sunshinesky.config.Config
import com.sunshinesky.config.Peer
import java.util.ArrayList

class AlbbConfigProxy : Parcelable {
    val `interface`: InterfaceProxy
    val peers: ObservableList<PeerProxy> = ObservableArrayList()

    private constructor(parcel: Parcel) {
        `interface` = parcel.readParcelable(InterfaceProxy::class.java.classLoader)!!
        parcel.readTypedList(peers, PeerProxy.CREATOR)
        peers.forEach { it.bind(this) }
    }

    constructor(other: Config) {
        `interface` = InterfaceProxy(other.getInterface())
        other.peers.forEach {
            val proxy = PeerProxy(it)
            peers.add(proxy)
            proxy.bind(this)
        }
    }

    constructor() {
        `interface` = InterfaceProxy()
    }

    fun addPeer(): PeerProxy {
        val proxy = PeerProxy()
        peers.add(proxy)
        proxy.bind(this)
        return proxy
    }

    override fun describeContents() = 0

    @Throws(BadConfigException::class)
    fun resolve(): Config {
        val resolvedPeers: MutableCollection<Peer> = ArrayList()
        peers.forEach { resolvedPeers.add(it.resolve()) }
        return Config.Builder()
                .setInterface(`interface`.resolve())
                .addPeers(resolvedPeers)
                .build()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(`interface`, flags)
        dest.writeTypedList(peers)
    }

    private class ConfigProxyCreator : Parcelable.Creator<AlbbConfigProxy> {
        override fun createFromParcel(parcel: Parcel): AlbbConfigProxy {
            return AlbbConfigProxy(parcel)
        }

        override fun newArray(size: Int): Array<AlbbConfigProxy?> {
            return arrayOfNulls(size)
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AlbbConfigProxy> = ConfigProxyCreator()
    }
}
