/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.aliData.response

open class CustomerObj {

    var token: String? = null
    var expireDay = 0
    var tokenHead: String? = null
    var username: String? = null
    var password: String? = null
    var memberLevelId:String? = null
    var nickname:String? = null
}