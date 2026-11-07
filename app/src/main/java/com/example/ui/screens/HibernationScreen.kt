package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.AppStatus
import com.example.model.HibernationStats
import com.example.ui.components.AppCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryContainer
import com.example.ui.theme.SleepingZzz
import com.example.ui.theme.WhitelistedShield

@Composable
fun HibernationScreen(
  apps: List<AppInfo>,
  stats: HibernationStats,
  selectedFilter: String,
  searchQuery: String,
  showSystemApps: Boolean,
  onFilterSelected: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onToggleSystemApps: () -> Unit,
  onHibernateApp: (AppInfo) -> Unit,
  onWakeApp: (AppInfo) -> Unit,
  onToggleWhitelist: (AppInfo) -> Unit,
  onAppDetailsClick: (AppInfo) -> Unit,
  onStartBatchHibernate: () -> Unit,
  onOpenAddAppDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  val runningCount = apps.count { it.status == AppStatus.ACTIVE_BACKGROUND || it.status == AppStatus.PENDING_HIBERNATION }
  val hibernatedCount = apps.count { it.status == AppStatus.HIBERNATING }
  val whitelistedCount = apps.count { it.status == AppStatus.WHITELISTED }

  val filteredApps = remember(apps, selectedFilter, searchQuery, showSystemApps) {
    apps.filter { app ->
      // Filter by system app toggle
      val matchesSystem = showSystemApps || !app.isSystemApp

      // Filter by status tab
      val matchesStatus = when (selectedFilter) {
        "RUNNING" -> app.status == AppStatus.ACTIVE_BACKGROUND || app.status == AppStatus.PENDING_HIBERNATION
        "HIBERNATED" -> app.status == AppStatus.HIBERNATING
        "WHITELISTED" -> app.status == AppStatus.WHITELISTED
        else -> true
      }

      // Filter by search query
      val matchesSearch = searchQuery.isBlank() ||
          app.appName.contains(searchQuery, ignoreCase = true) ||
          app.packageName.contains(searchQuery, ignoreCase = true)

      matchesSystem && matchesStatus && matchesSearch
    }
  }

  // Animation for Floating Zzz FAB when there are running apps to hibernate
  val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")
  val fabScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (runningCount > 0) 1.05f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "fabScale"
  )

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top Hero Hibernation Impact Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hibernation_hero_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    EmeraldPrimaryContainer.copy(alpha = 0.5f),
                    DarkSurfaceVariant.copy(alpha = 0.7f)
                  )
                )
              )
              .padding(18.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = "Greenify Standby Guard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                  )
                  Text(
                    text = "$hibernatedCount apps sleeping | $runningCount active in background",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                  onClick = onOpenAddAppDialog,
                  modifier = Modifier.testTag("add_app_hero_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add app to Greenify",
                    tint = EmeraldPrimary
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // 3 Metric Stat Pillars
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                StatPillar(
                  icon = Icons.Default.Bedtime,
                  title = "Apps Asleep",
                  value = "$hibernatedCount",
                  accentColor = SleepingZzz
                )
                StatPillar(
                  icon = Icons.Default.Bolt,
                  title = "Power Saved",
                  value = "${stats.estimatedMahSaved} mAh",
                  accentColor = EmeraldPrimary
                )
                StatPillar(
                  icon = Icons.Default.Speed,
                  title = "Life Boost",
                  value = "+${stats.hoursBatteryExtended} hrs",
                  accentColor = Color(0xFFFFD54F)
                )
              }
            }
          }
        }
      }

      // Search & Filters Row
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search apps or packages...") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                  Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                }
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_apps_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = EmeraldPrimary,
              cursorColor = EmeraldPrimary
            )
          )

          // Filter Chips Horizontal Scroll
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilterChip(
              selected = selectedFilter == "ALL",
              onClick = { onFilterSelected("ALL") },
              label = { Text("All (${apps.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color(0xFF003816)
              ),
              modifier = Modifier.testTag("filter_chip_all")
            )

            FilterChip(
              selected = selectedFilter == "RUNNING",
              onClick = { onFilterSelected("RUNNING") },
              label = { Text("Running ($runningCount)") },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color(0xFF003816)
              ),
              modifier = Modifier.testTag("filter_chip_running")
            )

            FilterChip(
              selected = selectedFilter == "HIBERNATED",
              onClick = { onFilterSelected("HIBERNATED") },
              label = { Text("Asleep ($hibernatedCount)") },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(16.dp))
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color(0xFF003816)
              ),
              modifier = Modifier.testTag("filter_chip_hibernated")
            )

            FilterChip(
              selected = selectedFilter == "WHITELISTED",
              onClick = { onFilterSelected("WHITELISTED") },
              label = { Text("Whitelisted ($whitelistedCount)") },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = Color(0xFF003816)
              ),
              modifier = Modifier.testTag("filter_chip_whitelisted")
            )

            FilterChip(
              selected = showSystemApps,
              onClick = onToggleSystemApps,
              label = { Text(if (showSystemApps) "Hide System" else "Show System") },
              modifier = Modifier.testTag("filter_chip_system")
            )
          }
        }
      }

      // App Cards List
      if (filteredApps.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No apps match current filter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(filteredApps, key = { it.packageName }) { app ->
          AppCard(
            app = app,
            onHibernateClick = { onHibernateApp(app) },
            onWakeClick = { onWakeApp(app) },
            onWhitelistToggle = { onToggleWhitelist(app) },
            onDetailsClick = { onAppDetailsClick(app) }
          )
        }
      }
    }

    // Floating Zzz Hibernation FAB
    ExtendedFloatingActionButton(
      onClick = onStartBatchHibernate,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .scale(fabScale)
        .testTag("hibernate_batch_fab"),
      containerColor = EmeraldPrimary,
      contentColor = Color(0xFF003816),
      icon = {
        Icon(
          imageVector = Icons.Default.Bedtime,
          contentDescription = "Hibernate Background Apps",
          modifier = Modifier.size(22.dp)
        )
      },
      text = {
        Text(
          text = if (runningCount > 0) "Hibernate $runningCount Apps" else "Hibernate All (Zzz)",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }
    )
  }
}

@Composable
private fun StatPillar(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  value: String,
  accentColor: Color
) {
  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = accentColor,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
