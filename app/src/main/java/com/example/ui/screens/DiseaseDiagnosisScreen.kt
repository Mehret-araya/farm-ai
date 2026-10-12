package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropDisease
import com.example.data.remote.CropDiagnosisResponse
import com.example.ui.viewmodel.FarmViewModel
import com.example.util.AmharicStrings

@Composable
fun DiseaseDiagnosisScreen(viewModel: FarmViewModel) {
  val context = LocalContext.current
  val isAmharic by viewModel.isAmharic.collectAsState()
  val isDiagnosing by viewModel.isDiagnosing.collectAsState()
  val currentDiagnosis by viewModel.currentDiagnosis.collectAsState()
  val selectedLeafBitmap by viewModel.selectedLeafBitmap.collectAsState()
  val knownDiseases = viewModel.knownDiseases
  val historyRecords by viewModel.diagnosticRecords.collectAsState()

  val cropChoices = listOf("Wheat", "Maize", "Coffee", "Tomato", "Teff", "Potato", "Sorghum", "Barley")
  var selectedCrop by remember { mutableStateOf(cropChoices.first()) }
  var symptomsText by remember { mutableStateOf("") }
  var isSavedConfirmed by remember { mutableStateOf(false) }

  // Gallery Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(source)
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        viewModel.setSelectedLeafBitmap(bitmap)
      } catch (e: Exception) {
        // Fallback
      }
    }
  }

  // Camera Capture
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      viewModel.setSelectedLeafBitmap(bitmap)
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header Card
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Healing,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (isAmharic) AmharicStrings.SCANNER_TITLE_AM else AmharicStrings.SCANNER_TITLE_EN,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (isAmharic) "በAI የተደገፈ የሰብል ጤና መርማሪ" else "Multimodal leaf symptom analyzer & agronomist advisory",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (isAmharic) AmharicStrings.SCANNER_SUBTITLE_AM else AmharicStrings.SCANNER_SUBTITLE_EN,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2. Leaf Image Input Box with 3 Options (Camera, Gallery, Sample Leaf)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("leaf_image_capture_box"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (selectedLeafBitmap != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
              Image(
                bitmap = selectedLeafBitmap!!.asImageBitmap(),
                contentDescription = "Selected leaf image",
                modifier = Modifier.fillMaxSize()
              )
              IconButton(
                onClick = { viewModel.setSelectedLeafBitmap(null) },
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(8.dp)
                  .background(Color.Black.copy(alpha = 0.6f), CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove photo",
                  tint = Color.White
                )
              }
            }
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🍃", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (isAmharic) "የቅጠል ፎቶ ያንሱ ወይም ከጋለሪ ይምረጡ" else "Take a leaf photo, pick from gallery, or load sample",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { cameraLauncher.launch(null) },
              modifier = Modifier
                .weight(1f)
                .testTag("button_take_photo"),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isAmharic) "ፎቶ አንሳ" else "Camera", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = {
                photoPickerLauncher.launch(
                  androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                  )
                )
              },
              modifier = Modifier
                .weight(1f)
                .testTag("button_pick_gallery")
            ) {
              Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isAmharic) "ጋለሪ" else "Gallery", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = {
                val sampleBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_farm_logo)
                viewModel.setSelectedLeafBitmap(sampleBitmap)
              },
              modifier = Modifier
                .weight(1f)
                .testTag("button_sample_leaf")
            ) {
              Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isAmharic) "ናሙና" else "Sample", fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 3. Crop Selection & Symptoms
    item {
      Column {
        Text(
          text = if (isAmharic) "የሰብል ምርጫ" else "Select Crop",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(cropChoices) { crop ->
            val isSelected = crop == selectedCrop
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { selectedCrop = crop }
            ) {
              Text(
                text = crop,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = symptomsText,
          onValueChange = { symptomsText = it },
          label = { Text(if (isAmharic) "የምልክቶች መግለጫ (ለምሳሌ፡ ቅጠል ላይ ቢጫ ነጠብጣብ)" else "Observed Symptoms (e.g. yellow rust pustules, frass)") },
          placeholder = { Text(if (isAmharic) "የቅጠል፣ ግንዱን ወይም ፍሬውን ገጽታ ይግለጹ" else "Describe discoloration, holes, or wilting...") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 4. Quick Test Specimens Chips
    item {
      Column {
        Text(
          text = if (isAmharic) AmharicStrings.SAMPLE_CASES_AM else AmharicStrings.SAMPLE_CASES_EN,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(knownDiseases) { disease ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.secondaryContainer,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  selectedCrop = disease.cropName
                  symptomsText = disease.symptoms
                  viewModel.selectPreloadedDisease(disease)
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(disease.sampleLeafEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = disease.diseaseName.split(" / ").first(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                  )
                  Text(
                    text = disease.cropName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Run AI Diagnosis Button
    item {
      Button(
        onClick = {
          val desc = symptomsText.ifBlank { "Discoloration and leaf damage observed on $selectedCrop" }
          isSavedConfirmed = false
          viewModel.runDiseaseDiagnosis(selectedCrop, desc)
        },
        enabled = !isDiagnosing,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("run_diagnosis_button"),
        shape = RoundedCornerShape(14.dp)
      ) {
        if (isDiagnosing) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(if (isAmharic) "AI እየመረመረ ነው..." else "AI Pathologist Analyzing...")
        } else {
          Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isAmharic) AmharicStrings.DIAGNOSE_BUTTON_AM else AmharicStrings.DIAGNOSE_BUTTON_EN,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 6. Diagnosis Results Breakdown
    if (currentDiagnosis != null) {
      item {
        DiagnosisResultCard(
          diagnosis = currentDiagnosis!!,
          isAmharic = isAmharic,
          isSaved = isSavedConfirmed,
          onSaveToJournal = {
            viewModel.saveAdviceFromChat(
              question = "${currentDiagnosis!!.diseaseName} Treatment",
              answer = "${currentDiagnosis!!.organicTreatment}\n\nChemical: ${currentDiagnosis!!.chemicalTreatment}",
              amharic = currentDiagnosis!!.amharicAdvice
            )
            isSavedConfirmed = true
          },
          onDismiss = { viewModel.clearCurrentDiagnosis() }
        )
      }
    }

    // 7. Recent Diagnostic Records History
    if (historyRecords.isNotEmpty()) {
      item {
        Text(
          text = if (isAmharic) "ያለፉ ምርመራዎች (ለመመልከት ይጫኑ)" else "Past Field Diagnoses (Tap to inspect)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      items(historyRecords.take(5), key = { it.id }) { record ->
        OutlinedCard(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { viewModel.selectHistoryRecord(record) }
            .testTag("history_record_${record.id}"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text("🔬", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${record.cropName}: ${record.diseaseName}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Confidence: ${record.confidenceScore}% • ${record.severityLevel} • Tap to view",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            IconButton(
              onClick = { viewModel.deleteDiagnosticRecord(record.id) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete record",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun DiagnosisResultCard(
  diagnosis: CropDiagnosisResponse,
  isAmharic: Boolean,
  isSaved: Boolean,
  onSaveToJournal: () -> Unit,
  onDismiss: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("diagnostic_result_card"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE8F5E9)
          ) {
            Text(
              text = "AI Verified • ${diagnosis.confidenceScore}% Confidence",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF2E7D32)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = diagnosis.diseaseName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = diagnosis.diseaseNameAmharic,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close result")
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Amharic Advisory Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🇪🇹", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "የባለሙያ ምክር (አማርኛ):",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = diagnosis.amharicAdvice,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Organic Remedy
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isAmharic) AmharicStrings.ORGANIC_REMEDY_AM else AmharicStrings.ORGANIC_REMEDY_EN,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF2E7D32)
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = diagnosis.organicTreatment,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Chemical Remedy
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isAmharic) AmharicStrings.CHEMICAL_REMEDY_AM else AmharicStrings.CHEMICAL_REMEDY_EN,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.error
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = diagnosis.chemicalTreatment,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Prevention
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isAmharic) AmharicStrings.PREVENTION_AM else AmharicStrings.PREVENTION_EN,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = diagnosis.preventionTips,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action: Bookmark Advice to Journal
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        OutlinedButton(
          onClick = onSaveToJournal,
          shape = RoundedCornerShape(12.dp),
          colors = if (isSaved) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Default.CheckCircle else Icons.Default.Bookmark,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isSaved) (if (isAmharic) "ተቀምጧል ✓" else "Saved to Journal ✓") else (if (isAmharic) "ምክሩን አስቀምጥ" else "Save Advice to Journal"),
            style = MaterialTheme.typography.labelMedium
          )
        }
      }
    }
  }
}
