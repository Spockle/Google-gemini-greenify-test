package com.example.data

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("greenify_prefs", Context.MODE_PRIVATE)

  fun getWhitelistedPackages(): Set<String> {
    return prefs.getStringSet(
      KEY_WHITELIST,
      setOf(
        "com.google.android.deskclock",
        "com.android.deskclock",
        "com.google.android.dialer",
        "com.android.phone",
        "com.google.android.apps.messaging",
        "com.whatsapp"
      )
    ) ?: emptySet()
  }

  fun setWhitelistedPackages(packages: Set<String>) {
    prefs.edit().putStringSet(KEY_WHITELIST, packages).apply()
  }

  fun getHibernatedPackages(): Set<String> {
    return prefs.getStringSet(KEY_HIBERNATED, emptySet()) ?: emptySet()
  }

  fun setHibernatedPackages(packages: Set<String>) {
    prefs.edit().putStringSet(KEY_HIBERNATED, packages).apply()
  }

  fun getGreenifyTrackedPackages(): Set<String> {
    return prefs.getStringSet(KEY_GREENIFY_LIST, emptySet()) ?: emptySet()
  }

  fun setGreenifyTrackedPackages(packages: Set<String>) {
    prefs.edit().putStringSet(KEY_GREENIFY_LIST, packages).apply()
  }

  fun getTotalMahSaved(): Int = prefs.getInt(KEY_MAH_SAVED, 420)
  fun addMahSaved(amount: Int) {
    val current = getTotalMahSaved()
    prefs.edit().putInt(KEY_MAH_SAVED, current + amount).apply()
  }

  fun getTotalHibernatedCount(): Int = prefs.getInt(KEY_TOTAL_HIBERNATED, 18)
  fun incrementHibernatedCount(amount: Int) {
    val current = getTotalHibernatedCount()
    prefs.edit().putInt(KEY_TOTAL_HIBERNATED, current + amount).apply()
  }

  fun getActiveProfileId(): String = prefs.getString(KEY_ACTIVE_PROFILE, "balanced") ?: "balanced"
  fun setActiveProfileId(id: String) {
    prefs.edit().putString(KEY_ACTIVE_PROFILE, id).apply()
  }

  fun isAggressiveDoze(): Boolean = prefs.getBoolean(KEY_AGGRESSIVE_DOZE, true)
  fun setAggressiveDoze(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_AGGRESSIVE_DOZE, enabled).apply()
  }

  fun isAutoScreenOff(): Boolean = prefs.getBoolean(KEY_AUTO_SCREEN_OFF, true)
  fun setAutoScreenOff(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_AUTO_SCREEN_OFF, enabled).apply()
  }

  companion object {
    private const val KEY_WHITELIST = "pref_whitelist"
    private const val KEY_HIBERNATED = "pref_hibernated"
    private const val KEY_GREENIFY_LIST = "pref_greenify_list"
    private const val KEY_MAH_SAVED = "pref_mah_saved"
    private const val KEY_TOTAL_HIBERNATED = "pref_total_hibernated"
    private const val KEY_ACTIVE_PROFILE = "pref_active_profile"
    private const val KEY_AGGRESSIVE_DOZE = "pref_aggressive_doze"
    private const val KEY_AUTO_SCREEN_OFF = "pref_auto_screen_off"
  }
}
