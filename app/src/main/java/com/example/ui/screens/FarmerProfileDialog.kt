package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun FarmerProfileDialog(
  viewModel: FarmViewModel,
  onDismiss: () -> Unit
) {
  val profile by viewModel.profile.collectAsState()
  val isAmharic by viewModel.isAmharic.collectAsState()

  var name by remember(profile) { mutableStateOf(profile?.fullName ?: "Mehret Araya") }
  var phone by remember(profile) { mutableStateOf(profile?.phoneNumber ?: "+251 91 123 4567") }
  var region by remember(profile) { mutableStateOf(profile?.region ?: "Oromia / Arsi") }
  var woreda by remember(profile) { mutableStateOf(profile?.woreda ?: "Tiyo Woreda") }
  var sizeText by remember(profile) { mutableStateOf((profile?.farmSizeHectares ?: 3.5).toString()) }
  var soil by remember(profile) { mutableStateOf(profile?.primarySoilType ?: "Vertisol (Black Clay)") }
  var expText by remember(profile) { mutableStateOf((profile?.farmingExperienceYears ?: 6).toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isAmharic) "የአርሶ አደር መረጃ" else "Farmer Profile & Settings",
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Switch Demo Farmers
        Text(
          text = if (isAmharic) "ናሙና አርሶ አደሮችን ይሞክሩ:" else "Quick Switch Demo Farmer Profile:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.outline
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          DemoProfileChip(title = "Mehret", subtitle = "Arsi", onClick = { viewModel.switchDemoFarmer(0) })
          DemoProfileChip(title = "Abebe", subtitle = "Gojjam", onClick = { viewModel.switchDemoFarmer(1) })
          DemoProfileChip(title = "Almaz", subtitle = "Hawassa", onClick = { viewModel.switchDemoFarmer(2) })
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(if (isAmharic) "ሙሉ ስም" else "Full Name") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(if (isAmharic) "ስልክ ቁጥር" else "Phone Number") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text(if (isAmharic) "ክልል / ዞን" else "Region / Zone") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = woreda,
            onValueChange = { woreda = it },
            label = { Text(if (isAmharic) "ወረዳ" else "Woreda") },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = sizeText,
            onValueChange = { sizeText = it },
            label = { Text(if (isAmharic) "ስፋት (ሄክታር)" else "Farm Size (Ha)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = expText,
            onValueChange = { expText = it },
            label = { Text(if (isAmharic) "ልምድ (ዓመታት)" else "Experience (Yrs)") },
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = soil,
          onValueChange = { soil = it },
          label = { Text(if (isAmharic) "የአፈር አይነት" else "Primary Soil Type") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          viewModel.updateProfile(
            fullName = name,
            phone = phone,
            region = region,
            woreda = woreda,
            farmSize = sizeText.toDoubleOrNull() ?: 3.0,
            soil = soil,
            lang = if (isAmharic) "am" else "en",
            experienceYears = expText.toIntOrNull() ?: 5
          )
          onDismiss()
        },
        modifier = Modifier.testTag("save_profile_button")
      ) {
        Text(if (isAmharic) "አስቀምጥ" else "Save Changes")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isAmharic) "ዝጋ" else "Close")
      }
    }
  )
}

@Composable
fun DemoProfileChip(title: String, subtitle: String, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.primaryContainer,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )
      Text(
        text = subtitle,
        fontSize = 9.sp,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}
