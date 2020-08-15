

package com.sunblackhole.android.backend;

import com.sunblackhole.util.NonNullForAll;

@NonNullForAll
public final class BackendException extends Exception {
    private final Object[] format;
    private final Reason reason;

    public BackendException(final Reason reason, final Object... format) {
        this.reason = reason;
        this.format = format;
    }

    public Object[] getFormat() {
        return format;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        UNKNOWN_KERNEL_MODULE_NAME,
        WG_QUICK_CONFIG_ERROR_CODE,
        TUNNEL_MISSING_CONFIG,
        VPN_NOT_AUTHORIZED,
        UNABLE_TO_START_VPN,
        TUN_CREATION_ERROR,
        GO_ACTIVATION_ERROR_CODE
    }
}
