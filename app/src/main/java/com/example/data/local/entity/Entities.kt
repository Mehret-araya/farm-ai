package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profile")
data class FarmerProfileEntity(
  @PrimaryKey val id: Int = 1,
  val fullName: String,
  val phoneNumber: String,
  val region: String,
  val woreda: String,
  val farmSizeHectares: Double,
  val primarySoilType: String,
  val preferredLanguage: String, // "en" or "am"
  val farmingExperienceYears: Int,
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tracked_crops")
data class TrackedCropEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropName: String,
  val cropNameAmharic: String,
  val variety: String,
  val plantingDateMillis: Long,
  val expectedHarvestDateMillis: Long,
  val plotSizeHectares: Double,
  val currentStage: String, // "Sowing", "Germination", "Vegetative", "Flowering", "Maturity", "Harvest"
  val stageProgress: Float, // 0.0f to 1.0f
  val healthStatus: String, // "Healthy", "Attention", "Alert"
  val notes: String = ""
)

@Entity(tableName = "farm_tasks")
data class FarmTaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val titleAmharic: String,
  val cropName: String,
  val dueDateFormatted: String,
  val isCompleted: Boolean = false,
  val priority: String = "Medium", // "High", "Medium", "Low"
  val category: String = "General" // "Fertilization", "Irrigation", "Weeding", "Pest Control", "Harvest"
)

@Entity(tableName = "saved_advice")
data class SavedAdviceEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val topic: String,
  val question: String,
  val answer: String,
  val answerAmharic: String,
  val source: String, // "AI Agronomist", "Crop Guide", "Disease Scanner"
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "diagnostic_records")
data class DiagnosticRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropName: String,
  val diseaseName: String,
  val diseaseNameAmharic: String,
  val confidenceScore: Int, // e.g. 94%
  val severityLevel: String, // "Mild", "Moderate", "Severe"
  val symptoms: String,
  val organicRemedy: String,
  val chemicalRemedy: String,
  val amharicSummary: String,
  val dateRecorded: Long = System.currentTimeMillis()
)
