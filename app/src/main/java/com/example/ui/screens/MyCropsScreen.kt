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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TrackedCropEntity
import com.example.ui.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCropsScreen(viewModel: FarmViewModel) {
  val isAmharic by viewModel.isAmharic.collectAsState()
  val crops by viewModel.crops.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }
  var editingCrop by remember { mutableStateOf<TrackedCropEntity?>(null) }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_crop_fab")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Crop")
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (isAmharic) "የማሳ ሰብሎች ክትትል" else "My Farm Crop Inventory",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (isAmharic) "የእድገት ደረጃን፣ በሽታዎችንና ምርት ዝግጁነትን ይከታተሉ" else "Track growth stages, health indicators, and harvest timelines.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      if (crops.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("🌾", fontSize = 48.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (isAmharic) "ምንም የተመዘገበ ሰብል የለም" else "No farm plots tracked yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = if (isAmharic) "የ'+' ምልክቱን በመጫን አዲስ ሰብል ይመዝግቡ" else "Tap the '+' button to log your first field plot.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      } else {
        items(crops, key = { it.id }) { crop ->
          CropDetailCard(
            crop = crop,
            isAmharic = isAmharic,
            onAdvanceStage = { viewModel.advanceCropStage(crop) },
            onDelete = { viewModel.deleteCrop(crop.id) },
            onEditNotes = { editingCrop = crop }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  if (showAddDialog) {
    AddCropDialog(
      isAmharic = isAmharic,
      onDismiss = { showAddDialog = false },
      onConfirm = { name, variety, hectares, stage, notes ->
        viewModel.addNewCrop(name, variety, hectares, stage, notes)
        showAddDialog = false
      }
    )
  }

  if (editingCrop != null) {
    EditCropNotesDialog(
      crop = editingCrop!!,
      isAmharic = isAmharic,
      onDismiss = { editingCrop = null },
      onSave = { updatedNotes ->
        viewModel.updateCropNotes(editingCrop!!, updatedNotes)
        editingCrop = null
      }
    )
  }
}

@Composable
fun CropDetailCard(
  crop: TrackedCropEntity,
  isAmharic: Boolean,
  onAdvanceStage: () -> Unit,
  onDelete: () -> Unit,
  onEditNotes: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("crop_card_${crop.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (isAmharic) crop.cropNameAmharic else crop.cropName,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (crop.healthStatus == "Healthy") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            ) {
              Text(
                text = crop.healthStatus,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (crop.healthStatus == "Healthy") Color(0xFF2E7D32) else Color(0xFFE65100)
              )
            }
          }
          Text(
            text = "Variety: ${crop.variety} • ${crop.plotSizeHectares} ha",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete crop",
            tint = MaterialTheme.colorScheme.outline
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stage Progress
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (isAmharic) "የእድገት ደረጃ:" else "Current Stage:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = crop.currentStage,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { crop.stageProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer
      )

      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onEditNotes() }
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (crop.notes.isNotBlank()) "📝 ${crop.notes}" else (if (isAmharic) "📝 ማስታወሻ ለመጻፍ ይጫኑ" else "📝 Tap to add field observation"),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit note",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        OutlinedButton(
          onClick = onAdvanceStage,
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isAmharic) "ቀጣይ ደረጃ" else "Advance Stage",
            style = MaterialTheme.typography.labelMedium
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCropDialog(
  isAmharic: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (name: String, variety: String, hectares: Double, stage: String, notes: String) -> Unit
) {
  val cropOptions = listOf("Wheat", "Teff", "Maize", "Coffee", "Barley", "Sorghum", "Tomato", "Potato", "Chickpea")
  val stageOptions = listOf("Sowing", "Germination", "Vegetative", "Flowering", "Maturity", "Harvest Ready")

  var selectedCrop by remember { mutableStateOf(cropOptions.first()) }
  var variety by remember { mutableStateOf("") }
  var hectaresText by remember { mutableStateOf("1.5") }
  var selectedStage by remember { mutableStateOf(stageOptions.first()) }
  var notes by remember { mutableStateOf("") }

  var expandedCrop by remember { mutableStateOf(false) }
  var expandedStage by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isAmharic) "አዲስ ሰብል መዝግብ" else "Track New Farm Plot",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        // Crop Type Dropdown
        ExposedDropdownMenuBox(
          expanded = expandedCrop,
          onExpandedChange = { expandedCrop = !expandedCrop }
        ) {
          OutlinedTextField(
            value = selectedCrop,
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isAmharic) "የሰብል አይነት" else "Crop Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCrop) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = expandedCrop,
            onDismissRequest = { expandedCrop = false }
          ) {
            cropOptions.forEach { option ->
              DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                  selectedCrop = option
                  expandedCrop = false
                }
              )
            }
          }
        }

        // Variety
        OutlinedTextField(
          value = variety,
          onValueChange = { variety = it },
          label = { Text(if (isAmharic) "የዘር ዝርያ (ምሳሌ፡ ዳካ/ቁንጮ)" else "Variety (e.g. Quncho / Kingbird)") },
          modifier = Modifier.fillMaxWidth()
        )

        // Hectares
        OutlinedTextField(
          value = hectaresText,
          onValueChange = { hectaresText = it },
          label = { Text(if (isAmharic) "የመሬት ስፋት (በሄክታር)" else "Plot Size (Hectares)") },
          modifier = Modifier.fillMaxWidth()
        )

        // Initial Stage Dropdown
        ExposedDropdownMenuBox(
          expanded = expandedStage,
          onExpandedChange = { expandedStage = !expandedStage }
        ) {
          OutlinedTextField(
            value = selectedStage,
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isAmharic) "የእድገት ደረጃ" else "Starting Stage") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStage) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = expandedStage,
            onDismissRequest = { expandedStage = false }
          ) {
            stageOptions.forEach { option ->
              DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                  selectedStage = option
                  expandedStage = false
                }
              )
            }
          }
        }

        // Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(if (isAmharic) "ማስታወሻ" else "Field Observations / Notes") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val ha = hectaresText.toDoubleOrNull() ?: 1.0
          onConfirm(selectedCrop, variety, ha, selectedStage, notes)
        }
      ) {
        Text(if (isAmharic) "አስቀምጥ" else "Save Plot")
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
fun EditCropNotesDialog(
  crop: TrackedCropEntity,
  isAmharic: Boolean,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit
) {
  var notesText by remember(crop) { mutableStateOf(crop.notes) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isAmharic) "${crop.cropNameAmharic} - ማስታወሻ አዘምን" else "Update Notes: ${crop.cropName}",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = notesText,
          onValueChange = { notesText = it },
          label = { Text(if (isAmharic) "የማሳ ምልከታ" else "Field Observations & Care Log") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 3
        )
      }
    },
    confirmButton = {
      Button(onClick = { onSave(notesText) }) {
        Text(if (isAmharic) "አስቀምጥ" else "Save Note")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isAmharic) "ተመለስ" else "Cancel")
      }
    }
  )
}
