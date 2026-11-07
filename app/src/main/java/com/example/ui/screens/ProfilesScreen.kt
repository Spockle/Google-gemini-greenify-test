package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PowerProfile
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SleepingZzz
import com.example.ui.theme.WhitelistedShield

@Composable
fun ProfilesScreen(
  profiles: List<PowerProfile>,
  activeProfileId: String,
  isAutoScreenOff: Boolean,
  isAggressiveDoze: Boolean,
  onProfileSelected: (String) -> Unit,
  onToggleAutoScreenOff: () -> Unit,
  onToggleAggressiveDoze: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header section
    Text(
      text = "Greenify Power Profiles",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold
    )

    Text(
      text = "Select an automation profile tailored to your workflow and standby battery goals.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Profiles List
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      profiles.forEach { profile ->
        val isSelected = profile.id == activeProfileId
        val (profileIcon, accentColor) = when (profile.id) {
          "extreme" -> Pair(Icons.Default.Eco, EmeraldPrimary)
          "balanced" -> Pair(Icons.Default.Tune, Color(0xFF64FFDA))
          "night" -> Pair(Icons.Default.Nightlight, SleepingZzz)
          else -> Pair(Icons.Default.Speed, Color(0xFFFFB74D))
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onProfileSelected(profile.id) }
            .testTag("profile_card_${profile.id}"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.surfaceContainer
          ),
          elevation = CardDefaults.cardElevation(if (isSelected) 3.dp else 0.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) accentColor.copy(alpha = 0.2f)
                  else MaterialTheme.colorScheme.surface
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = profileIcon,
                contentDescription = null,
                tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = profile.title,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
                )
                if (isSelected) {
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "ACTIVE",
                      style = MaterialTheme.typography.labelSmall,
                      color = accentColor,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Text(
                text = profile.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = profile.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
              )
            }

            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Active",
                tint = accentColor,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Automation switches card
    Text(
      text = "Standby Automation Controls",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Switch 1: Auto Hibernate when screen turns off
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ScreenLockPortrait,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Screen-Off Auto-Hibernation",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Puts background apps to sleep minutes after the screen locks",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = isAutoScreenOff,
            onCheckedChange = { onToggleAutoScreenOff() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = EmeraldPrimary,
              checkedTrackColor = Color(0xFF003816)
            ),
            modifier = Modifier.testTag("switch_screen_off_hibernation")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch 2: Aggressive Doze
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Bedtime,
            contentDescription = null,
            tint = SleepingZzz,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Aggressive Doze Mode",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Bypasses system motion sensor delays for instant standby",
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
            modifier = Modifier.testTag("switch_aggressive_doze")
          )
        }
      }
    }

    // Story of Greenify & Modern Android card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HistoryEdu,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "The Legacy & Evolution of Greenify",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Originally developed by Oasis Feng, Greenify was the most celebrated Android utility of the root & Xposed era. It solved Android's notorious battery drain by freezing runaway background apps without breaking push notifications.\n\nOver subsequent releases, Google baked Greenify's core innovations directly into the OS—introducing Doze Mode in Android 6 and App Standby Buckets in Android 9.\n\nThis modernized edition gives you back direct transparency and control: identifying high-risk wakelocks, providing instant batch hibernation, and using modern Android restricted standby architectures without needing root access.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 19.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
