package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.AppStatus
import com.example.model.RiskLevel
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.SleepingZzz
import com.example.ui.theme.WhitelistedShield

@Composable
fun AppCard(
  app: AppInfo,
  onHibernateClick: () -> Unit,
  onWakeClick: () -> Unit,
  onWhitelistToggle: () -> Unit,
  onDetailsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isHibernated = app.status == AppStatus.HIBERNATING
  val isWhitelisted = app.status == AppStatus.WHITELISTED

  val cardBg by animateColorAsState(
    targetValue = when {
      isHibernated -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f)
      isWhitelisted -> MaterialTheme.colorScheme.surfaceContainerHigh
      else -> MaterialTheme.colorScheme.surface
    },
    label = "cardBg"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onDetailsClick)
      .testTag("app_card_${app.packageName}"),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isHibernated) 0.dp else 2.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Avatar / Icon Representation
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              when {
                isHibernated -> Color(0xFF1E3326)
                isWhitelisted -> Color(0xFF2C223E)
                else -> Color(0xFF163222)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = app.appName.firstOrNull()?.uppercase() ?: "A",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = when {
              isHibernated -> SleepingZzz
              isWhitelisted -> WhitelistedShield
              else -> EmeraldPrimary
            }
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Package Name
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = app.appName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Status Badge
            AppStatusBadge(status = app.status)
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = app.packageName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Action Buttons
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Whitelist toggle button
          IconButton(
            onClick = onWhitelistToggle,
            modifier = Modifier
              .size(38.dp)
              .testTag("whitelist_btn_${app.packageName}")
          ) {
            Icon(
              imageVector = if (isWhitelisted) Icons.Default.Shield else Icons.Default.Security,
              contentDescription = if (isWhitelisted) "Whitelisted" else "Protect with Whitelist",
              tint = if (isWhitelisted) WhitelistedShield else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
              modifier = Modifier.size(20.dp)
            )
          }

          // Hibernate / Wake toggle button
          if (isHibernated) {
            FilledTonalIconButton(
              onClick = onWakeClick,
              modifier = Modifier
                .size(38.dp)
                .testTag("wake_btn_${app.packageName}"),
              colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
              )
            ) {
              Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Wake Up App",
                tint = Color(0xFFFFCA28),
                modifier = Modifier.size(20.dp)
              )
            }
          } else {
            FilledTonalIconButton(
              onClick = onHibernateClick,
              modifier = Modifier
                .size(38.dp)
                .testTag("hibernate_btn_${app.packageName}"),
              colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = EmeraldPrimary.copy(alpha = 0.18f),
                contentColor = EmeraldPrimary
              )
            ) {
              Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = "Hibernate App (Zzz)",
                tint = EmeraldPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Risk & Permission Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Standby Bucket pill
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Text(
            text = "Bucket: ${app.standbyBucket}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        // RAM estimation
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Text(
            text = "~${app.estimatedRamMb} MB RAM",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        // Wakelock indicator
        if (app.hasWakeLock) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = RiskMedium.copy(alpha = 0.15f)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LockClock,
                contentDescription = null,
                tint = RiskMedium,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Wakelock",
                style = MaterialTheme.typography.labelSmall,
                color = RiskMedium
              )
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Risk level pill
        RiskBadge(level = app.backgroundRisk)
      }
    }
  }
}

@Composable
fun AppStatusBadge(status: AppStatus) {
  val (bgColor, textColor, label) = when (status) {
    AppStatus.ACTIVE_BACKGROUND -> Triple(Color(0xFF2E2218), Color(0xFFFFB74D), "Running")
    AppStatus.PENDING_HIBERNATION -> Triple(Color(0xFF1E2833), Color(0xFF64B5F6), "Pending")
    AppStatus.HIBERNATING -> Triple(Color(0xFF0F2B1D), EmeraldPrimary, "Zzz Asleep")
    AppStatus.WHITELISTED -> Triple(Color(0xFF281938), WhitelistedShield, "Protected")
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = textColor,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
  }
}

@Composable
fun RiskBadge(level: RiskLevel) {
  val (color, text) = when (level) {
    RiskLevel.HIGH -> Pair(RiskHigh, "High Drain")
    RiskLevel.MEDIUM -> Pair(RiskMedium, "Moderate")
    RiskLevel.LOW -> Pair(RiskLow, "Eco")
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = color.copy(alpha = 0.15f)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = color
      )
    }
  }
}
