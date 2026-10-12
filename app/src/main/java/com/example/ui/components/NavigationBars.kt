package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AmharicStrings

enum class FarmScreen(val titleEn: String, val titleAm: String) {
  DASHBOARD(AmharicStrings.NAV_HOME_EN, AmharicStrings.NAV_HOME_AM),
  CROPS(AmharicStrings.NAV_CROPS_EN, AmharicStrings.NAV_CROPS_AM),
  DIAGNOSE(AmharicStrings.NAV_DIAGNOSE_EN, AmharicStrings.NAV_DIAGNOSE_AM),
  CHAT(AmharicStrings.NAV_CHAT_EN, AmharicStrings.NAV_CHAT_AM),
  GUIDES(AmharicStrings.NAV_GUIDES_EN, AmharicStrings.NAV_GUIDES_AM)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmTopBar(
  currentScreen: FarmScreen,
  isAmharic: Boolean,
  onToggleLanguage: () -> Unit,
  onOpenProfile: () -> Unit
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Text("🌱", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (isAmharic) currentScreen.titleAm else currentScreen.titleEn,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    actions = {
      // Offline ready badge
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.padding(end = 6.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CloudDone,
            contentDescription = "Offline ready",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isAmharic) "ኦፍላይን" else "Offline",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      // Language Switcher Chip
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .clickable { onToggleLanguage() }
          .testTag("language_toggle_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = "Toggle Language",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isAmharic) "አማ" else "EN",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
        }
      }

      IconButton(
        onClick = onOpenProfile,
        modifier = Modifier.testTag("profile_button")
      ) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Farmer Profile",
          tint = MaterialTheme.colorScheme.primary
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}

@Composable
fun FarmBottomNavBar(
  currentScreen: FarmScreen,
  isAmharic: Boolean,
  onScreenSelected: (FarmScreen) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    val items = listOf(
      Triple(FarmScreen.DASHBOARD, Icons.Default.Dashboard, if (isAmharic) "ዳሽቦርድ" else "Home"),
      Triple(FarmScreen.CROPS, Icons.Default.Agriculture, if (isAmharic) "ሰብሎች" else "Crops"),
      Triple(FarmScreen.DIAGNOSE, Icons.Default.Healing, if (isAmharic) "ምርመራ" else "AI Doctor"),
      Triple(FarmScreen.CHAT, Icons.Default.Psychology, if (isAmharic) "AI ጠይቅ" else "AI Chat"),
      Triple(FarmScreen.GUIDES, Icons.AutoMirrored.Filled.MenuBook, if (isAmharic) "መመሪያ" else "Guides")
    )

    items.forEach { (screen, icon, label) ->
      val selected = currentScreen == screen
      NavigationBarItem(
        selected = selected,
        onClick = { onScreenSelected(screen) },
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer,
          unselectedIconColor = MaterialTheme.colorScheme.outline,
          unselectedTextColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
      )
    }
  }
}
