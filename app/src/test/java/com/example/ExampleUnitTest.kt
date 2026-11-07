package com.example

import com.example.model.AppInfo
import com.example.model.AppStatus
import com.example.model.BatteryInfo
import com.example.model.PowerProfile
import com.example.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun appInfo_riskCalculation_verifiesCorrectDefaults() {
    val app = AppInfo(
      packageName = "com.spotify.music",
      appName = "Spotify",
      status = AppStatus.ACTIVE_BACKGROUND,
      hasWakeLock = true,
      hasBootAutostart = true,
      hasForegroundService = true,
      backgroundRisk = RiskLevel.HIGH,
      estimatedRamMb = 112
    )

    assertEquals("com.spotify.music", app.packageName)
    assertEquals(RiskLevel.HIGH, app.backgroundRisk)
    assertTrue(app.hasWakeLock)
    assertTrue(app.hasBootAutostart)
    assertTrue(app.hasForegroundService)
    assertFalse(app.isSystemApp)
  }

  @Test
  fun batteryInfo_statusAndDischarge_areValid() {
    val battery = BatteryInfo(
      level = 82,
      isCharging = false,
      plugType = "Battery",
      temperatureC = 29.4f,
      voltageMv = 4150,
      health = "Good",
      technology = "Li-ion",
      estimatedHoursRemaining = 18.2f,
      drainRatePerHour = 4.5f
    )

    assertEquals(82, battery.level)
    assertFalse(battery.isCharging)
    assertEquals("Good", battery.health)
    assertTrue(battery.voltageMv > 3000)
    assertTrue(battery.temperatureC in 10.0f..50.0f)
  }

  @Test
  fun powerProfile_extremeStandby_configuresCorrectFlags() {
    val profile = PowerProfile(
      id = "extreme",
      title = "Extreme Standby",
      subtitle = "Maximum Battery Life",
      description = "Aggressive Doze within 30 seconds of screen off",
      isAggressiveDozeEnabled = true,
      autoHibernateScreenOff = true,
      aggressiveBackgroundKill = true
    )

    assertTrue(profile.isAggressiveDozeEnabled)
    assertTrue(profile.autoHibernateScreenOff)
    assertTrue(profile.aggressiveBackgroundKill)
  }
}
