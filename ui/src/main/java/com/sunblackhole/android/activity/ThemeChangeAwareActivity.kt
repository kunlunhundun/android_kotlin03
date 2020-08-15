/*
 */
package com.sunblackhole.android.activity

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.sunblackhole.android.Application

abstract class ThemeChangeAwareActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Application.getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Application.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
        }
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "dark_theme" -> {
                AppCompatDelegate.setDefaultNightMode(if (sharedPreferences.getBoolean(key, false)) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                })
                recreate()
            }
        }
    }
}
