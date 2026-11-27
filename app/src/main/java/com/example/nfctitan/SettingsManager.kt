
package com.example.nfctitan

import android.content.Context

class SettingsManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "nfc_titan_settings",
        Context.MODE_PRIVATE
    )

    fun setLanguage(language: String) {
        if (language !in listOf("ar", "en")) return

        preferences.edit()
            .putString("language", language)
            .apply()
    }

    fun getLanguage(): String {
        return preferences.getString("language", "ar") ?: "ar"
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean("animations_enabled", enabled)
            .apply()
    }

    fun areAnimationsEnabled(): Boolean {
        return preferences.getBoolean("animations_enabled", true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean("sound_enabled", enabled)
            .apply()
    }

    fun isSoundEnabled(): Boolean {
        return preferences.getBoolean("sound_enabled", false)
    }

    fun setAutoSaveEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean("auto_save", enabled)
            .apply()
    }

    fun isAutoSaveEnabled(): Boolean {
        return preferences.getBoolean("auto_save", true)
    }
}
