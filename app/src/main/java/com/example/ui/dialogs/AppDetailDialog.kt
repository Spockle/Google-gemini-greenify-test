package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.AppStatus
import com.example.ui.components.AppStatusBadge
import com.example.ui.components.RiskBadge
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.SleepingZzz
import com.example.ui.theme.WhitelistedShield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailSheet(
  app: AppInfo,
  onDismiss: () -> Unit,
  onHibernate: () -> Unit,
  onWake: () -> Unit,
  onToggleWhitelist: () -> Unit,
  onOpenSystemSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = modifier.testTag("app_detail_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header with Icon and Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(EmeraldPrimary.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = app.appName.firstOrNull()?.uppercase() ?: "A",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = app.appName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = app.packageName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_sheet_btn")
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Status & Stats Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AppStatusBadge(status = app.status)
        RiskBadge(level = app.backgroundRisk)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Text(
            text = if (app.isSystemApp) "System App" else "User Installed",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Technical Telemetry Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Background Telemetry",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = EmeraldPrimary
          )

          Spacer(modifier = Modifier.height(12.dp))

          DetailRow(
            icon = Icons.Default.Memory,
            title = "Memory Footprint",
            value = "~${app.estimatedRamMb} MB RAM"
          )
          Spacer(modifier = Modifier.height(8.dp))
          DetailRow(
            icon = Icons.Default.Power,
            title = "Standby Bucket",
            value = app.standbyBucket
          )
          Spacer(modifier = Modifier.height(8.dp))
          DetailRow(
            icon = Icons.Default.LockClock,
            title = "Wakelock Capability",
            value = if (app.hasWakeLock) "Active (Can wake CPU)" else "None"
          )
          Spacer(modifier = Modifier.height(8.dp))
          DetailRow(
            icon = Icons.Default.Warning,
            title = "Boot Autostart",
            value = if (app.hasBootAutostart) "Enabled at Boot" else "Disabled"
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Risk Reasons Section
      if (app.riskReasons.isNotEmpty()) {
        Text(
          text = "Identified Battery Drain Factors",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        app.riskReasons.forEach { reason ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = RiskHigh,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = reason,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (app.status == AppStatus.HIBERNATING) {
          Button(
            onClick = {
              onWake()
              onDismiss()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("dialog_wake_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
          ) {
            Icon(imageVector = Icons.Default.WbSunny, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Wake Up")
          }
        } else {
          Button(
            onClick = {
              onHibernate()
              onDismiss()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("dialog_hibernate_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = EmeraldPrimary,
              contentColor = Color(0xFF003816)
            )
          ) {
            Icon(imageVector = Icons.Default.Bedtime, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hibernate (Zzz)", fontWeight = FontWeight.Bold)
          }
        }

        OutlinedButton(
          onClick = {
            onToggleWhitelist()
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("dialog_whitelist_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = WhitelistedShield
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            if (app.status == AppStatus.WHITELISTED) "Unprotect" else "Whitelist",
            color = WhitelistedShield
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // System Settings Deep Link (modern Android battery restriction!)
      Button(
        onClick = {
          onOpenSystemSettings()
          onDismiss()
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dialog_system_battery_btn"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
          contentColor = MaterialTheme.colorScheme.onSurface
        )
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.OpenInNew,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("System Settings: Set Battery to Restricted")
      }
    }
  }
}

@Composable
private fun DetailRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.weight(1f))
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
