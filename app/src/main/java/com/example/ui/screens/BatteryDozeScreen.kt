package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryInfo
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.SleepingZzz

@Composable
fun BatteryDozeScreen(
  batteryInfo: BatteryInfo,
  isAggressiveDoze: Boolean,
  isDeviceIdleMode: Boolean,
  isIgnoringOptimization: Boolean,
  onToggleAggressiveDoze: () -> Unit,
  onOpenOptimizationSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Battery Circular Gauge Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("battery_gauge_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier.size(190.dp),
          contentAlignment = Alignment.Center
        ) {
          // Circular Progress Arc
          val percentage = (batteryInfo.level / 100f).coerceIn(0f, 1f)
          val arcColor = when {
            batteryInfo.level > 30 -> EmeraldPrimary
            batteryInfo.level > 15 -> RiskMedium
            else -> RiskHigh
          }

          Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            // Track
            drawArc(
              color = Color(0xFF1E2E23),
              startAngle = 135f,
              sweepAngle = 270f,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress
            drawArc(
              color = arcColor,
              startAngle = 135f,
              sweepAngle = 270f * percentage,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = if (batteryInfo.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
              contentDescription = null,
              tint = if (batteryInfo.isCharging) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "${batteryInfo.level}%",
              fontSize = 42.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Text(
              text = if (batteryInfo.isCharging) "Charging" else "~${"%.1f".format(batteryInfo.estimatedHoursRemaining)} hrs left",
              style = MaterialTheme.typography.labelMedium,
              color = EmeraldPrimary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status summary pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Power,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = batteryInfo.plugType,
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Live Telemetry Grid
    Text(
      text = "Live Hardware Telemetry",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      TelemetryCard(
        title = "Thermal Temp",
        value = "${"%.1f".format(batteryInfo.temperatureC)} °C",
        subtitle = "${"%.1f".format(batteryInfo.temperatureC * 9 / 5 + 32)} °F",
        icon = Icons.Default.Thermostat,
        iconTint = if (batteryInfo.temperatureC > 38f) RiskHigh else EmeraldPrimary,
        modifier = Modifier.weight(1f)
      )

      TelemetryCard(
        title = "Cell Voltage",
        value = "${batteryInfo.voltageMv} mV",
        subtitle = "${"%.2f".format(batteryInfo.voltageMv / 1000f)} Volts",
        icon = Icons.Default.ElectricMeter,
        iconTint = Color(0xFF64FFDA),
        modifier = Modifier.weight(1f)
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      TelemetryCard(
        title = "Battery Health",
        value = batteryInfo.health,
        subtitle = batteryInfo.technology,
        icon = Icons.Default.Favorite,
        iconTint = Color(0xFFFF4081),
        modifier = Modifier.weight(1f)
      )

      TelemetryCard(
        title = "Discharge Rate",
        value = "${batteryInfo.drainRatePerHour}% / hr",
        subtitle = "Optimal Eco Range",
        icon = Icons.Default.Timer,
        iconTint = Color(0xFFFFD54F),
        modifier = Modifier.weight(1f)
      )
    }

    // Aggressive Doze Section
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("aggressive_doze_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(SleepingZzz.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Bedtime,
              contentDescription = null,
              tint = SleepingZzz,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Aggressive Doze Guard",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (isAggressiveDoze) "Active: accelerates Android deep sleep" else "Disabled: stock Doze timing",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = isAggressiveDoze,
            onCheckedChange = { onToggleAggressiveDoze() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = EmeraldPrimary,
              checkedTrackColor = Color(0xFF003816)
            ),
            modifier = Modifier.testTag("aggressive_doze_switch")
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Classic Greenify revolutionized Android by triggering Doze right after the screen turned off. On modern Android, Aggressive Doze instructs the system to bypass idle motion delays and immediately restrict background alarms, network sync, and wakelocks.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onOpenOptimizationSettings,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("open_battery_optimization_btn"),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
          )
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Manage Battery Optimization Whitelist")
        }
      }
    }

    // App Standby Buckets Educational Guide
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = "Modern Android App Standby Buckets",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = EmeraldPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Android dynamically categorizes apps into 5 Standby Buckets to conserve battery. Greenify moves apps into the Restricted bucket:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        BucketExplainerItem("1. ACTIVE", "App in foreground; zero battery restrictions.")
        BucketExplainerItem("2. WORKING SET", "App used frequently; jobs deferred briefly.")
        BucketExplainerItem("3. FREQUENT", "Used regularly; network & alarms capped to batches.")
        BucketExplainerItem("4. RARE", "Infrequently used; extreme alarm and sync limits.")
        BucketExplainerItem("5. RESTRICTED (Greenified)", "Maximum power-saving; zero background execution allowed.")
      }
    }
  }
}

@Composable
private fun TelemetryCard(
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = iconTint,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
private fun BucketExplainerItem(bucket: String, description: String) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = bucket,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = description,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
