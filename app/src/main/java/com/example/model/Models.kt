package com.example.model

enum class AppStatus {
  ACTIVE_BACKGROUND,
  PENDING_HIBERNATION,
  HIBERNATING,
  WHITELISTED
}

enum class RiskLevel {
  HIGH,
  MEDIUM,
  LOW
}

data class AppInfo(
  val packageName: String,
  val appName: String,
  val isSystemApp: Boolean = false,
  val status: AppStatus = AppStatus.ACTIVE_BACKGROUND,
  val standbyBucket: String = "Active",
  val hasWakeLock: Boolean = false,
  val hasBootAutostart: Boolean = false,
  val hasForegroundService: Boolean = false,
  val backgroundRisk: RiskLevel = RiskLevel.LOW,
  val riskReasons: List<String> = emptyList(),
  val estimatedRamMb: Int = 32,
  val lastHibernatedTime: Long? = null,
  val hibernationCount: Int = 0
)

data class BatteryInfo(
  val level: Int = 85,
  val isCharging: Boolean = false,
  val plugType: String = "Battery",
  val temperatureC: Float = 28.5f,
  val voltageMv: Int = 4120,
  val health: String = "Good",
  val technology: String = "Li-ion",
  val estimatedHoursRemaining: Float = 18.5f,
  val drainRatePerHour: Float = 3.8f
)

data class PowerProfile(
  val id: String,
  val title: String,
  val subtitle: String,
  val description: String,
  val isAggressiveDozeEnabled: Boolean,
  val autoHibernateScreenOff: Boolean,
  val aggressiveBackgroundKill: Boolean
)

data class HibernationStats(
  val totalAppsHibernated: Int = 0,
  val estimatedMahSaved: Int = 0,
  val hoursBatteryExtended: Float = 0f,
  val lastHibernationTimestamp: Long = 0L
)

enum class GreenifyTab {
  HIBERNATION,
  BATTERY_DOZE,
  ANALYZER,
  PROFILES
}
