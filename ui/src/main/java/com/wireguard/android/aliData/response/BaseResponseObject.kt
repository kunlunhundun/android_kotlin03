/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.aliData.response

open class BaseResponseObject {
    var success: Boolean = false
    var code: String = "200"
    var message: String? = null
}