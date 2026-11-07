package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GreenifyTab
import com.example.ui.dialogs.AddAppSheet
import com.example.ui.dialogs.AppDetailSheet
import com.example.ui.dialogs.BatchHibernationDialog
import com.example.ui.screens.BatteryDozeScreen
import com.example.ui.screens.HibernationScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.WakeupAnalyzerScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GreenifyTheme
import com.example.viewmodel.GreenifyViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      GreenifyTheme {
        GreenifyApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenifyApp(viewModel: GreenifyViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current

  // Handle hardware back button to navigate back to primary tab
  if (uiState.activeTab != GreenifyTab.HIBERNATION) {
    BackHandler {
      viewModel.selectTab(GreenifyTab.HIBERNATION)
    }
  }

  // Handle transient alert messages
  LaunchedEffect(uiState.recentAlertMessage) {
    uiState.recentAlertMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearAlert()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmeraldPrimary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
              text = "Greenify",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = EmeraldPrimary,
              letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = EmeraldPrimary.copy(alpha = 0.15f)
            ) {
              Text(
                text = "v5.0 Neo",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        },
        actions = {
          // Battery quick telemetry pill
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.padding(end = 4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (uiState.batteryInfo.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${uiState.batteryInfo.level}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = { viewModel.loadData() },
            modifier = Modifier.testTag("action_refresh_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Refresh Telemetry and Apps",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = uiState.activeTab == GreenifyTab.HIBERNATION,
          onClick = { viewModel.selectTab(GreenifyTab.HIBERNATION) },
          icon = {
            Icon(imageVector = Icons.Default.Eco, contentDescription = "Hibernation")
          },
          label = { Text("Hibernation") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldPrimary,
            selectedTextColor = EmeraldPrimary,
            indicatorColor = EmeraldPrimary.copy(alpha = 0.18f)
          ),
          modifier = Modifier.testTag("nav_tab_hibernation")
        )

        NavigationBarItem(
          selected = uiState.activeTab == GreenifyTab.BATTERY_DOZE,
          onClick = { viewModel.selectTab(GreenifyTab.BATTERY_DOZE) },
          icon = {
            Icon(imageVector = Icons.Default.Bolt, contentDescription = "Battery & Doze")
          },
          label = { Text("Battery & Doze") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldPrimary,
            selectedTextColor = EmeraldPrimary,
            indicatorColor = EmeraldPrimary.copy(alpha = 0.18f)
          ),
          modifier = Modifier.testTag("nav_tab_battery_doze")
        )

        NavigationBarItem(
          selected = uiState.activeTab == GreenifyTab.ANALYZER,
          onClick = { viewModel.selectTab(GreenifyTab.ANALYZER) },
          icon = {
            Icon(imageVector = Icons.Default.LockClock, contentDescription = "Cutoff Analyzer")
          },
          label = { Text("Analyzer") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldPrimary,
            selectedTextColor = EmeraldPrimary,
            indicatorColor = EmeraldPrimary.copy(alpha = 0.18f)
          ),
          modifier = Modifier.testTag("nav_tab_analyzer")
        )

        NavigationBarItem(
          selected = uiState.activeTab == GreenifyTab.PROFILES,
          onClick = { viewModel.selectTab(GreenifyTab.PROFILES) },
          icon = {
            Icon(imageVector = Icons.Default.Tune, contentDescription = "Profiles")
          },
          label = { Text("Profiles") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldPrimary,
            selectedTextColor = EmeraldPrimary,
            indicatorColor = EmeraldPrimary.copy(alpha = 0.18f)
          ),
          modifier = Modifier.testTag("nav_tab_profiles")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (uiState.activeTab) {
        GreenifyTab.HIBERNATION -> {
          HibernationScreen(
            apps = uiState.apps,
            stats = uiState.stats,
            selectedFilter = uiState.selectedFilter,
            searchQuery = uiState.searchQuery,
            showSystemApps = uiState.showSystemApps,
            onFilterSelected = { viewModel.setFilter(it) },
            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
            onToggleSystemApps = { viewModel.toggleSystemApps() },
            onHibernateApp = { viewModel.hibernateApp(it) },
            onWakeApp = { viewModel.wakeApp(it) },
            onToggleWhitelist = { viewModel.toggleWhitelist(it) },
            onAppDetailsClick = { viewModel.selectAppForDetail(it) },
            onStartBatchHibernate = { viewModel.startBatchHibernation() },
            onOpenAddAppDialog = { viewModel.toggleAddAppDialog(true) }
          )
        }
        GreenifyTab.BATTERY_DOZE -> {
          BatteryDozeScreen(
            batteryInfo = uiState.batteryInfo,
            isAggressiveDoze = uiState.isAggressiveDoze,
            isDeviceIdleMode = uiState.isDeviceIdleMode,
            isIgnoringOptimization = uiState.isIgnoringBatteryOptimization,
            onToggleAggressiveDoze = { viewModel.toggleAggressiveDoze() },
            onOpenOptimizationSettings = { viewModel.openBatteryOptimizationSettings() }
          )
        }
        GreenifyTab.ANALYZER -> {
          WakeupAnalyzerScreen(
            apps = uiState.apps,
            onHibernateApp = { viewModel.hibernateApp(it) },
            onAppDetailsClick = { viewModel.selectAppForDetail(it) }
          )
        }
        GreenifyTab.PROFILES -> {
          ProfilesScreen(
            profiles = uiState.profiles,
            activeProfileId = uiState.activeProfileId,
            isAutoScreenOff = uiState.isAutoScreenOff,
            isAggressiveDoze = uiState.isAggressiveDoze,
            onProfileSelected = { viewModel.setProfile(it) },
            onToggleAutoScreenOff = { viewModel.toggleAutoScreenOff() },
            onToggleAggressiveDoze = { viewModel.toggleAggressiveDoze() }
          )
        }
      }

      // App Detail Bottom Sheet
      uiState.selectedAppForDetail?.let { selectedApp ->
        AppDetailSheet(
          app = selectedApp,
          onDismiss = { viewModel.selectAppForDetail(null) },
          onHibernate = { viewModel.hibernateApp(selectedApp) },
          onWake = { viewModel.wakeApp(selectedApp) },
          onToggleWhitelist = { viewModel.toggleWhitelist(selectedApp) },
          onOpenSystemSettings = { viewModel.openAppDetails(selectedApp.packageName) }
        )
      }

      // Add App Dialog Sheet
      if (uiState.showAddAppDialog) {
        AddAppSheet(
          apps = uiState.apps,
          onDismiss = { viewModel.toggleAddAppDialog(false) },
          onAppSelected = { viewModel.trackInGreenifyList(it) }
        )
      }

      // Batch Hibernation Animation Dialog
      if (uiState.isHibernatingBatch) {
        BatchHibernationDialog(
          progress = uiState.batchHibernationProgress,
          currentAppName = uiState.batchHibernationCurrentApp
        )
      }
    }
  }
}
