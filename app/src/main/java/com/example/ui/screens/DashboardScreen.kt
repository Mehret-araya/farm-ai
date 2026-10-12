package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.FarmTaskEntity
import com.example.data.local.entity.SavedAdviceEntity
import com.example.data.local.entity.TrackedCropEntity
import com.example.data.model.AgroWeather
import com.example.ui.components.FarmScreen
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusHealthy
import com.example.ui.viewmodel.FarmViewModel
import com.example.util.AmharicStrings

@Composable
fun DashboardScreen(
  viewModel: FarmViewModel,
  onNavigate: (FarmScreen) -> Unit,
  onOpenProfile: () -> Unit
) {
  val isAmharic by viewModel.isAmharic.collectAsState()
  val profile by viewModel.profile.collectAsState()
  val crops by viewModel.crops.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val weather by viewModel.weather.collectAsState()
  val selectedRegion by viewModel.selectedRegion.collectAsState()
  val savedAdviceList by viewModel.savedAdvice.collectAsState()

  var showWeatherForecastDialog by remember { mutableStateOf(false) }
  var showAddTaskDialog by remember { mutableStateOf(false) }
  var showSavedAdviceDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Card with Farmer Profile Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .testTag("dashboard_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          Image(
            painter = painterResource(id = R.drawable.img_farm_hero),
            contentDescription = "Farm landscape",
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Transparent,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                  )
                )
              )
          )
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
          ) {
            Text(
              text = if (isAmharic) "ሰላም፣ ${profile?.fullName ?: "አርሶ አደር"} 👋" else "Hello, ${profile?.fullName ?: "Farmer"} 👋",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFFFFD54F)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${profile?.region ?: "Oromia / Arsi"} • ${profile?.farmSizeHectares ?: 3.5} Hectares",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
              )
            }
          }
        }
      }
    }

    // 2. Agro-Weather Advisory Widget (Clickable for 5-day forecast)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("weather_advisory_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🌤️", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = if (isAmharic) AmharicStrings.WEATHER_ALERT_AM else AmharicStrings.WEATHER_ALERT_EN,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (isAmharic) weather.zoneNameAmharic else weather.regionName,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { showWeatherForecastDialog = true }
                .testTag("button_view_forecast")
            ) {
              Text(
                text = if (isAmharic) "5-ቀን ትንበያ >" else "5-Day >",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            WeatherMetricItem(label = if (isAmharic) "ሙቀት" else "Temp", value = "${weather.temperatureC}°C", icon = "🌡️")
            WeatherMetricItem(label = if (isAmharic) "የዝናብ እድል" else "Rain Risk", value = "${weather.rainfallProbability}%", icon = "🌧️")
            WeatherMetricItem(label = if (isAmharic) "እርጥበት" else "Humidity", value = "${weather.humidityPercent}%", icon = "💧")
            WeatherMetricItem(label = if (isAmharic) "የአፈር እርጥበት" else "Soil Moisture", value = "Optimal", icon = "🌱")
          }

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("💡", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isAmharic) weather.farmingAdvisoryAmharic else weather.farmingAdvisory,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Region Switcher Chips
          Spacer(modifier = Modifier.height(10.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.availableRegions) { region ->
              val isSelected = region == selectedRegion
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { viewModel.selectRegion(region) }
              ) {
                Text(
                  text = region.split(" - ").first(),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }
        }
      }
    }

    // 3. Quick Action Cards (5 buttons including Saved Advice)
    item {
      Text(
        text = if (isAmharic) AmharicStrings.QUICK_ACTIONS_AM else AmharicStrings.QUICK_ACTIONS_EN,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        QuickActionCard(
          title = if (isAmharic) AmharicStrings.SCAN_DISEASE_AM else AmharicStrings.SCAN_DISEASE_EN,
          icon = Icons.Default.Healing,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier
            .weight(1f)
            .testTag("action_scan_disease"),
          onClick = { onNavigate(FarmScreen.DIAGNOSE) }
        )
        QuickActionCard(
          title = if (isAmharic) AmharicStrings.ASK_AI_AM else AmharicStrings.ASK_AI_EN,
          icon = Icons.Default.Psychology,
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier
            .weight(1f)
            .testTag("action_ask_ai"),
          onClick = { onNavigate(FarmScreen.CHAT) }
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        QuickActionCard(
          title = if (isAmharic) AmharicStrings.NAV_CROPS_AM else "My Farm Plots",
          icon = Icons.Default.Agriculture,
          color = MaterialTheme.colorScheme.tertiaryContainer,
          modifier = Modifier
            .weight(1f)
            .testTag("action_my_crops"),
          onClick = { onNavigate(FarmScreen.CROPS) }
        )
        QuickActionCard(
          title = if (isAmharic) "የተቀመጡ ምክሮች (${savedAdviceList.size})" else "Saved Advice (${savedAdviceList.size})",
          icon = Icons.Default.Bookmark,
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .weight(1f)
            .testTag("action_saved_advice"),
          onClick = { showSavedAdviceDialog = true }
        )
      }
    }

    // 4. Active Farm Plots Progress Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isAmharic) AmharicStrings.ACTIVE_CROPS_AM else AmharicStrings.ACTIVE_CROPS_EN,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (isAmharic) "ሁሉንም እይ (${crops.size})" else "View All (${crops.size})",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { onNavigate(FarmScreen.CROPS) }
        )
      }
    }

    item {
      if (crops.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("🌱", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (isAmharic) "ምንም ሰብል አልተመዘገበም" else "No active crops tracked yet.",
              style = MaterialTheme.typography.bodyMedium
            )
            Button(
              onClick = { onNavigate(FarmScreen.CROPS) },
              modifier = Modifier.padding(top = 12.dp)
            ) {
              Text(if (isAmharic) "ሰብል መዝግብ" else "Add Crop")
            }
          }
        }
      } else {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(crops) { crop ->
            DashboardCropCard(crop = crop, isAmharic = isAmharic) {
              onNavigate(FarmScreen.CROPS)
            }
          }
        }
      }
    }

    // 5. Today's Priority Reminders with "+ Add Task" button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isAmharic) AmharicStrings.TODAY_TASKS_AM else AmharicStrings.TODAY_TASKS_EN,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (isAmharic) "+ አዲስ ተግባር" else "+ Add Task",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .clickable { showAddTaskDialog = true }
            .testTag("button_add_task_dashboard")
        )
      }
    }

    if (tasks.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = if (isAmharic) "የተጠናቀቁ ስራዎች የሉም። አዲስ ተግባር ያክሉ!" else "No pending tasks. Tap '+ Add Task' to create one!",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
      }
    } else {
      items(tasks.take(5), key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          isAmharic = isAmharic,
          onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
          onDelete = { viewModel.deleteTask(task.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // 5-Day Weather Forecast Dialog
  if (showWeatherForecastDialog) {
    WeatherForecastDialog(
      weather = weather,
      isAmharic = isAmharic,
      onDismiss = { showWeatherForecastDialog = false }
    )
  }

  // Add Task Dialog
  if (showAddTaskDialog) {
    AddTaskDialog(
      crops = crops.map { it.cropName },
      isAmharic = isAmharic,
      onDismiss = { showAddTaskDialog = false },
      onConfirm = { title, crop, due, priority, category ->
        viewModel.addNewTask(title, crop, due, priority, category)
        showAddTaskDialog = false
      }
    )
  }

  // Saved Advice Dialog
  if (showSavedAdviceDialog) {
    SavedAdviceDialog(
      savedAdviceList = savedAdviceList,
      isAmharic = isAmharic,
      onDelete = { id -> viewModel.deleteSavedAdvice(id) },
      onDismiss = { showSavedAdviceDialog = false }
    )
  }
}

@Composable
fun WeatherMetricItem(label: String, value: String, icon: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(icon, fontSize = 16.sp)
    Text(
      text = value,
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.outline
    )
  }
}

@Composable
fun QuickActionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = color)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun DashboardCropCard(
  crop: TrackedCropEntity,
  isAmharic: Boolean,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(220.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isAmharic) crop.cropNameAmharic else crop.cropName,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (crop.healthStatus == "Healthy") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ) {
          Text(
            text = crop.healthStatus,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (crop.healthStatus == "Healthy") Color(0xFF2E7D32) else Color(0xFFE65100)
          )
        }
      }

      Text(
        text = "${crop.variety} • ${crop.plotSizeHectares} ha",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Stage: ${crop.currentStage}",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { crop.stageProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer
      )
    }
  }
}

@Composable
fun TaskItemCard(
  task: FarmTaskEntity,
  isAmharic: Boolean,
  onToggle: (Boolean) -> Unit,
  onDelete: () -> Unit
) {
  OutlinedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.outlinedCardColors(
      containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { onToggle(!task.isCompleted) },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = "Toggle task",
          tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (isAmharic) task.titleAmharic else task.title,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold
          ),
          color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${task.cropName} • ${task.dueDateFormatted}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
          if (task.priority == "High") {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFFFEBEE)
            ) {
              Text(
                text = "Urgent",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFC62828)
              )
            }
          }
        }
      }
      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete task",
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun WeatherForecastDialog(
  weather: AgroWeather,
  isAmharic: Boolean,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("🌤️", fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isAmharic) "የ5-ቀን የአየር ሁኔታ ትንበያ" else "5-Day Agro Forecast",
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = if (isAmharic) "${weather.zoneNameAmharic} • የዝናብና ሙቀት ሁኔታ" else "${weather.regionName} • Temperature & Rainfall Projections",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )

        weather.dailyForecast.forEach { daily ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isAmharic) daily.dayAmharic else daily.day,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.width(60.dp)
              )
              Text(daily.icon, fontSize = 20.sp)
              Text(
                text = "${daily.tempMax}° / ${daily.tempMin}°C",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (daily.rainProb > 50) Color(0xFFE1F5FE) else Color(0xFFFFFDE7)
              ) {
                Text(
                  text = "🌧️ ${daily.rainProb}%",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (daily.rainProb > 50) Color(0xFF0277BD) else Color(0xFFF57F17)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text(if (isAmharic) "እሺ" else "Close")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
  crops: List<String>,
  isAmharic: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (title: String, crop: String, due: String, priority: String, category: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var selectedCrop by remember { mutableStateOf(if (crops.isNotEmpty()) crops.first() else "General Field") }
  var due by remember { mutableStateOf("Tomorrow") }
  var priority by remember { mutableStateOf("Medium") }
  var category by remember { mutableStateOf("Fertilization") }
  var expandedCrop by remember { mutableStateOf(false) }

  val priorities = listOf("High", "Medium", "Low")
  val categories = listOf("Fertilization", "Weeding", "Pest Control", "Irrigation", "Harvest")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isAmharic) "አዲስ የእርሻ ተግባር መዝግብ" else "Schedule Farm Reminder",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(if (isAmharic) "የስራው ርዕስ (ለምሳሌ፡ ዩሪያ ማዳበሪያ መጨመር)" else "Task Title (e.g. Apply Topdress Urea)") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = due,
          onValueChange = { due = it },
          label = { Text(if (isAmharic) "የሚከናወንበት ቀን (ለምሳሌ፡ ነገ፣ በ2 ቀን)" else "Due Date (e.g. In 2 days, Tomorrow)") },
          modifier = Modifier.fillMaxWidth()
        )

        // Priority row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          priorities.forEach { p ->
            val isSelected = priority == p
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { priority = p }
            ) {
              Text(
                text = p,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, selectedCrop, due, priority, category)
          }
        }
      ) {
        Text(if (isAmharic) "አስቀምጥ" else "Save Task")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isAmharic) "ተመለስ" else "Cancel")
      }
    }
  )
}

@Composable
fun SavedAdviceDialog(
  savedAdviceList: List<SavedAdviceEntity>,
  isAmharic: Boolean,
  onDelete: (Long) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isAmharic) "የተቀመጡ የእርሻ ምክሮች" else "Saved Farm Journal & Advice",
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      if (savedAdviceList.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("📖", fontSize = 32.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (isAmharic) "ምንም የተቀመጠ ምክር የለም። ከAI ውይይት ወይም ምርመራ 'Save Advice' የሚለውን በመጫን ማስቀመጥ ይችላሉ።" else "No advice bookmarked yet. Tap 'Save Advice' in AI Chat to save tips here.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(savedAdviceList, key = { it.id }) { advice ->
            OutlinedCard(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top
                ) {
                  Text(
                    text = advice.question,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                  )
                  IconButton(
                    onClick = { onDelete(advice.id) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete advice",
                      tint = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = advice.answer,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (advice.answerAmharic.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "🇪🇹 ${advice.answerAmharic}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text(if (isAmharic) "ዝጋ" else "Close")
      }
    }
  )
}
