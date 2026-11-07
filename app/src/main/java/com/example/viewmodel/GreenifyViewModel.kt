package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppPreferences
import com.example.model.AppInfo
import com.example.model.AppStatus
import com.example.model.BatteryInfo
import com.example.model.GreenifyTab
import com.example.model.HibernationStats
import com.example.model.PowerProfile
import com.example.util.SystemBatteryManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GreenifyUiState(
  val apps: List<AppInfo> = emptyList(),
  val batteryInfo: BatteryInfo = BatteryInfo(),
  val stats: HibernationStats = HibernationStats(),
  val activeTab: GreenifyTab = GreenifyTab.HIBERNATION,
  val selectedFilter: String = "ALL", // "ALL", "RUNNING", "HIBERNATED", "WHITELISTED"
  val searchQuery: String = "",
  val showSystemApps: Boolean = false,
  val isHibernatingBatch: Boolean = false,
  val batchHibernationProgress: Float = 0f,
  val batchHibernationCurrentApp: String = "",
  val selectedAppForDetail: AppInfo? = null,
  val showAddAppDialog: Boolean = false,
  val isAggressiveDoze: Boolean = true,
  val isAutoScreenOff: Boolean = true,
  val activeProfileId: String = "balanced",
  val profiles: List<PowerProfile> = emptyList(),
  val isDeviceIdleMode: Boolean = false,
  val isIgnoringBatteryOptimization: Boolean = false,
  val recentAlertMessage: String? = null
)

class GreenifyViewModel(application: Application) : AndroidViewModel(application) {

  private val batteryManager = SystemBatteryManager(application)
  private val preferences = AppPreferences(application)

  private val _uiState = MutableStateFlow(GreenifyUiState())
  val uiState: StateFlow<GreenifyUiState> = _uiState.asStateFlow()

  private val defaultProfiles = listOf(
    PowerProfile(
      id = "extreme",
      title = "Extreme Standby",
      subtitle = "Maximum Battery Life",
      description = "Aggressive Doze within 30 seconds of screen off. Puts all non-whitelisted apps into deep hibernation immediately.",
      isAggressiveDozeEnabled = true,
      autoHibernateScreenOff = true,
      aggressiveBackgroundKill = true
    ),
    PowerProfile(
      id = "balanced",
      title = "Smart Balanced",
      subtitle = "Recommended for Daily Use",
      description = "Hibernates heavy media and social apps when inactive, while preserving real-time messaging and alarms.",
      isAggressiveDozeEnabled = true,
      autoHibernateScreenOff = true,
      aggressiveBackgroundKill = false
    ),
    PowerProfile(
      id = "night",
      title = "Night Eco Mode",
      subtitle = "Zero Standby Drain (11PM - 7AM)",
      description = "Restricts background wakelocks, cuts off sync intervals, and enables ultra-sleep during overnight hours.",
      isAggressiveDozeEnabled = true,
      autoHibernateScreenOff = true,
      aggressiveBackgroundKill = true
    ),
    PowerProfile(
      id = "gaming",
      title = "Performance / Gaming",
      subtitle = "Unrestricted Device Speed",
      description = "Pauses background hibernation to avoid stutter and delivers full CPU performance to active game sessions.",
      isAggressiveDozeEnabled = false,
      autoHibernateScreenOff = false,
      aggressiveBackgroundKill = false
    )
  )

  init {
    _uiState.update {
      it.copy(
        profiles = defaultProfiles,
        activeProfileId = preferences.getActiveProfileId(),
        isAggressiveDoze = preferences.isAggressiveDoze(),
        isAutoScreenOff = preferences.isAutoScreenOff()
      )
    }
    loadData()
  }

  fun loadData() {
    val whitelisted = preferences.getWhitelistedPackages()
    val hibernated = preferences.getHibernatedPackages()
    val tracked = preferences.getGreenifyTrackedPackages()

    val batteryInfo = batteryManager.getBatteryInfo()
    val isIdle = batteryManager.isDeviceIdleMode()
    val isOptimized = batteryManager.isIgnoringBatteryOptimizations()

    val scannedApps = batteryManager.scanApps(whitelisted, hibernated, tracked)

    val totalMah = preferences.getTotalMahSaved()
    val totalHibCount = preferences.getTotalHibernatedCount()
    val extendedHours = (totalMah / 150.0f).coerceAtLeast(1.5f)

    _uiState.update {
      it.copy(
        apps = scannedApps,
        batteryInfo = batteryInfo,
        stats = HibernationStats(
          totalAppsHibernated = totalHibCount,
          estimatedMahSaved = totalMah,
          hoursBatteryExtended = extendedHours,
          lastHibernationTimestamp = System.currentTimeMillis()
        ),
        isDeviceIdleMode = isIdle,
        isIgnoringBatteryOptimization = isOptimized
      )
    }
  }

  fun selectTab(tab: GreenifyTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun setFilter(filter: String) {
    _uiState.update { it.copy(selectedFilter = filter) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleSystemApps() {
    _uiState.update { it.copy(showSystemApps = !it.showSystemApps) }
  }

  fun selectAppForDetail(app: AppInfo?) {
    _uiState.update { it.copy(selectedAppForDetail = app) }
  }

  fun toggleAddAppDialog(show: Boolean) {
    _uiState.update { it.copy(showAddAppDialog = show) }
  }

  fun clearAlert() {
    _uiState.update { it.copy(recentAlertMessage = null) }
  }

  fun hibernateApp(app: AppInfo) {
    batteryManager.killBackgroundProcesses(app.packageName)
    val currentHib = preferences.getHibernatedPackages().toMutableSet()
    currentHib.add(app.packageName)
    preferences.setHibernatedPackages(currentHib)

    val currentWhite = preferences.getWhitelistedPackages().toMutableSet()
    currentWhite.remove(app.packageName)
    preferences.setWhitelistedPackages(currentWhite)

    preferences.addMahSaved(45)
    preferences.incrementHibernatedCount(1)

    _uiState.update {
      it.copy(recentAlertMessage = "${app.appName} is now hibernated (Zzz)")
    }
    loadData()
  }

  fun wakeApp(app: AppInfo) {
    val currentHib = preferences.getHibernatedPackages().toMutableSet()
    currentHib.remove(app.packageName)
    preferences.setHibernatedPackages(currentHib)

    _uiState.update {
      it.copy(recentAlertMessage = "${app.appName} has been awakened")
    }
    loadData()
  }

  fun toggleWhitelist(app: AppInfo) {
    val currentWhite = preferences.getWhitelistedPackages().toMutableSet()
    val isCurrentlyWhitelisted = currentWhite.contains(app.packageName)

    if (isCurrentlyWhitelisted) {
      currentWhite.remove(app.packageName)
      _uiState.update { it.copy(recentAlertMessage = "${app.appName} removed from Whitelist") }
    } else {
      currentWhite.add(app.packageName)
      // also remove from hibernated
      val currentHib = preferences.getHibernatedPackages().toMutableSet()
      currentHib.remove(app.packageName)
      preferences.setHibernatedPackages(currentHib)
      _uiState.update { it.copy(recentAlertMessage = "${app.appName} protected in Whitelist (Never Hibernate)") }
    }
    preferences.setWhitelistedPackages(currentWhite)
    loadData()
  }

  fun trackInGreenifyList(app: AppInfo) {
    val tracked = preferences.getGreenifyTrackedPackages().toMutableSet()
    tracked.add(app.packageName)
    preferences.setGreenifyTrackedPackages(tracked)
    _uiState.update { it.copy(recentAlertMessage = "${app.appName} queued for Greenify auto-hibernation") }
    loadData()
  }

  fun startBatchHibernation() {
    viewModelScope.launch {
      val candidates = _uiState.value.apps.filter {
        it.status == AppStatus.ACTIVE_BACKGROUND || it.status == AppStatus.PENDING_HIBERNATION
      }

      if (candidates.isEmpty()) {
        _uiState.update { it.copy(recentAlertMessage = "All eligible apps are already hibernated!") }
        return@launch
      }

      _uiState.update {
        it.copy(
          isHibernatingBatch = true,
          batchHibernationProgress = 0f,
          batchHibernationCurrentApp = "Initiating Greenify Hibernation Cycle..."
        )
      }

      var freedRam = 0
      val hibernatedSet = preferences.getHibernatedPackages().toMutableSet()

      candidates.forEachIndexed { index, app ->
        val progress = (index + 1).toFloat() / candidates.size
        _uiState.update {
          it.copy(
            batchHibernationProgress = progress,
            batchHibernationCurrentApp = "Putting ${app.appName} to sleep..."
          )
        }
        batteryManager.killBackgroundProcesses(app.packageName)
        hibernatedSet.add(app.packageName)
        freedRam += app.estimatedRamMb
        delay(400)
      }

      preferences.setHibernatedPackages(hibernatedSet)
      val savedMah = candidates.size * 50
      preferences.addMahSaved(savedMah)
      preferences.incrementHibernatedCount(candidates.size)

      delay(300)
      _uiState.update {
        it.copy(
          isHibernatingBatch = false,
          batchHibernationProgress = 1f,
          recentAlertMessage = "Hibernated ${candidates.size} apps! Freed ~${freedRam}MB RAM, +${candidates.size * 0.4f}h battery"
        )
      }
      loadData()
    }
  }

  fun setProfile(profileId: String) {
    preferences.setActiveProfileId(profileId)
    val profile = defaultProfiles.find { it.id == profileId }
    if (profile != null) {
      preferences.setAggressiveDoze(profile.isAggressiveDozeEnabled)
      preferences.setAutoScreenOff(profile.autoHibernateScreenOff)
    }
    _uiState.update {
      it.copy(
        activeProfileId = profileId,
        isAggressiveDoze = profile?.isAggressiveDozeEnabled ?: it.isAggressiveDoze,
        isAutoScreenOff = profile?.autoHibernateScreenOff ?: it.isAutoScreenOff,
        recentAlertMessage = "Profile changed to ${profile?.title}"
      )
    }
  }

  fun toggleAggressiveDoze() {
    val newState = !_uiState.value.isAggressiveDoze
    preferences.setAggressiveDoze(newState)
    _uiState.update {
      it.copy(
        isAggressiveDoze = newState,
        recentAlertMessage = if (newState) "Aggressive Doze enabled" else "Aggressive Doze disabled"
      )
    }
  }

  fun toggleAutoScreenOff() {
    val newState = !_uiState.value.isAutoScreenOff
    preferences.setAutoScreenOff(newState)
    _uiState.update {
      it.copy(
        isAutoScreenOff = newState,
        recentAlertMessage = if (newState) "Screen-off auto-hibernation enabled" else "Screen-off auto-hibernation disabled"
      )
    }
  }

  fun openAppDetails(packageName: String) {
    batteryManager.openAppDetails(packageName)
  }

  fun openBatteryOptimizationSettings() {
    batteryManager.openBatteryOptimizationSettings()
  }
}
