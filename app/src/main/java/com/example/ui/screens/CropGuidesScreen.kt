package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CropGuide
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun CropGuidesScreen(viewModel: FarmViewModel) {
  val isAmharic by viewModel.isAmharic.collectAsState()
  val allGuides = viewModel.cropGuides
  var searchQuery by remember { mutableStateOf("") }
  val categories = listOf("All", "Cereal", "Cash Crop", "Horticulture", "Pulse")
  var selectedCategory by remember { mutableStateOf("All") }

  val filteredGuides = allGuides.filter { guide ->
    val matchesCategory = selectedCategory == "All" || guide.category.equals(selectedCategory, ignoreCase = true)
    val matchesSearch = searchQuery.isBlank() ||
        guide.name.contains(searchQuery, ignoreCase = true) ||
        guide.nameAmharic.contains(searchQuery, ignoreCase = true) ||
        guide.amharicSummary.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(32.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = if (isAmharic) "የኢትዮጵያ የሰብል እንክብካቤ መመሪያዎች" else "Ethiopian Crop-Care Knowledge Base",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
              text = if (isAmharic) "ለ100% ኦፍላይን አገልግሎት የተዘጋጀ" else "100% accessible offline without internet connection",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.tertiary
            )
          }
        }
      }
    }

    // Search Box
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(if (isAmharic) "ሰብሎችን ፈልግ (ጤፍ፣ ስንዴ፣ በቆሎ...)" else "Search crops (Teff, Wheat, Coffee...)") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_crop_guides"),
        shape = RoundedCornerShape(14.dp)
      )
    }

    // Category Filter Chips
    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { selectedCategory = cat }
          ) {
            Text(
              text = cat,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }
    }

    items(filteredGuides, key = { it.id }) { guide ->
      CropGuideCard(
        guide = guide,
        isAmharic = isAmharic,
        onTrackCrop = { g ->
          viewModel.addNewCrop(g.name, "Standard Improved", 1.0, "Sowing", "Added from Crop Care Guide")
        }
      )
    }
  }
}

@Composable
fun CropGuideCard(
  guide: CropGuide,
  isAmharic: Boolean,
  onTrackCrop: (CropGuide) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  var isAddedConfirmed by remember { mutableStateOf(false) }

  OutlinedCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("crop_guide_${guide.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = guide.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = guide.category,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
          Text(
            text = guide.nameAmharic,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = "Expand guide",
          tint = MaterialTheme.colorScheme.outline
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "Altitude", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
          Text(text = guide.altitudeMeters, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        }
        Column {
          Text(text = "Rainfall", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
          Text(text = guide.rainfallMm, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        }
        Column {
          Text(text = "Seed Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
          Text(text = guide.seedRate.split("(").first(), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Amharic Summary Card
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "🇪🇹 የሰብል አጭር መመሪያ:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = guide.amharicSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Planting Season & Soil
          Column {
            Text(
              text = "Planting Season & Soil Type:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "${guide.plantingSeason} • Soil: ${guide.soilType}",
              style = MaterialTheme.typography.bodySmall
            )
          }

          // Fertilizer Guide
          Column {
            Text(
              text = "Fertilizer Schedule (NPS / Urea):",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = guide.fertilizerGuide,
              style = MaterialTheme.typography.bodySmall
            )
          }

          // Common Diseases
          Column {
            Text(
              text = "Key Pests & Pathogens:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.error
            )
            Text(
              text = guide.commonDiseases.joinToString(", "),
              style = MaterialTheme.typography.bodySmall
            )
          }

          // Harvesting & Post-Harvest
          Column {
            Text(
              text = "Harvesting & Storage:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = guide.harvestingTips,
              style = MaterialTheme.typography.bodySmall
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              onTrackCrop(guide)
              isAddedConfirmed = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = if (isAddedConfirmed) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) else ButtonDefaults.buttonColors()
          ) {
            Icon(
              imageVector = if (isAddedConfirmed) Icons.Default.CheckCircle else Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isAddedConfirmed) (if (isAmharic) "በማሳዎ ላይ ተመዝግቧል ✓" else "Added to My Crops ✓") else (if (isAmharic) "ይህን ሰብል መዝግብ" else "Track in My Crops")
            )
          }
        }
      }
    }
  }
}
