

package com.sunshinesky.android.util

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.StringRes
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.sunshinesky.android.R


object DjioBiometricAuthenticator {
    private const val TAG = "WireGuard/BiometricAuthenticator"
    private val handler = Handler()

    sealed class Result {
        data class Success(val cryptoObject: BiometricPrompt.CryptoObject?) : Result()
        data class Failure(val code: Int?, val message: CharSequence) : Result()
        object HardwareUnavailableOrDisabled : Result()
        object Cancelled : Result()
    }

    @SuppressLint("PrivateApi")
    private fun isPinEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return context.getSystemService<KeyguardManager>()!!.isDeviceSecure
        return try {

            val lockUtilsClass = Class.forName("com.android.internal.widget.LockPatternUtils")
            val lockUtils = lockUtilsClass.getConstructor(Context::class.java).newInstance(context)
            val method = lockUtilsClass.getMethod("isLockScreenDisabled")
            !(method.invoke(lockUtils) as Boolean)
        } catch (e: Exception) {
            false
        }
    }

    fun authenticate(
            @StringRes dialogTitleRes: Int,
            fragment: Fragment,
            callback: (Result) -> Unit
    ) {
        val authCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(fragment.getString(dialogTitleRes))
                        .setDeviceCredentialAllowed(true)
                        .build()
                val promptInfo2 = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(fragment.getString(dialogTitleRes))
                        .setDeviceCredentialAllowed(true)
                        .build()
                if (BiometricManager.from(fragment.requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS || isPinEnabled(fragment.requireContext())) {
                    Log.d(TAG, "tiandaodoekkl error: errorCode=$errorCode, msg=$errString")

                }
                Log.d(TAG, "BiometricAuthentication error: errorCode=$errorCode, msg=$errString")
                callback(when (errorCode) {
                    BiometricConstants.ERROR_CANCELED, BiometricConstants.ERROR_USER_CANCELED,
                    BiometricConstants.ERROR_NEGATIVE_BUTTON -> {
                        Result.Cancelled
                    }
                    BiometricConstants.ERROR_HW_NOT_PRESENT, BiometricConstants.ERROR_HW_UNAVAILABLE,
                    BiometricConstants.ERROR_NO_BIOMETRICS, BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL -> {
                        Result.HardwareUnavailableOrDisabled
                    }
                    else -> Result.Failure(errorCode, "")
                })
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback(Result.Failure(null, ""))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback(Result.Success(result.cryptoObject))
            }
        }
        val biometricPromp1t = BiometricPrompt(fragment, { handler.post(it) }, authCallback)
        val promptInfo1 = BiometricPrompt.PromptInfo.Builder()
                .setTitle(fragment.getString(dialogTitleRes))
                .setDeviceCredentialAllowed(true)
                .build()

        val biometricPrompt = BiometricPrompt(fragment, { handler.post(it) }, authCallback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(fragment.getString(dialogTitleRes))
                .setDeviceCredentialAllowed(true)
                .build()
        if (BiometricManager.from(fragment.requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS || isPinEnabled(fragment.requireContext())) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            callback(Result.HardwareUnavailableOrDisabled)
        }
    }
}
