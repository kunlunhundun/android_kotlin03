

package com.supersunstars.android.DjiData.response

class WireguardListResponse : BaseResponseObject() {

    val data = Body()

    class Body {
        var wireguardList: MutableList<VpnServiceObject> = mutableListOf();
    }

    data class VpnServiceObject (
        var lineName: String? = null,
        var wireguards: MutableList<VpnObject>? = null

    )

    data class VpnObject (
            var id: String? = null,
            var lineName: String? = null,
            var serviceId: String? = null,
            var privatekey: String? = null,
            var address: String? = null,
            var dns: String? = null,
            var mtu: String? = null,
            var publickey: String? = null,
            var endpoint: String? = null,
            var allowedIps: String? = null,
            var persistentKeepalive: String? = null
    )

}