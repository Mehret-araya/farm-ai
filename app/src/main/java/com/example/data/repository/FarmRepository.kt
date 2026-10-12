package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.AppDatabase
import com.example.data.local.dao.FarmDao
import com.example.data.local.entity.DiagnosticRecordEntity
import com.example.data.local.entity.FarmTaskEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.SavedAdviceEntity
import com.example.data.local.entity.TrackedCropEntity
import com.example.data.model.AgroWeather
import com.example.data.model.CropDisease
import com.example.data.model.CropGuide
import com.example.data.model.StaticCropKnowledge
import com.example.data.remote.CropDiagnosisResponse
import com.example.data.remote.GeminiAgronomistService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class FarmRepository(
  private val database: AppDatabase,
  private val geminiService: GeminiAgronomistService = GeminiAgronomistService()
) {
  private val dao: FarmDao = database.farmDao()

  // Farmer Profile
  val profileFlow: Flow<FarmerProfileEntity?> = dao.getFarmerProfile()

  suspend fun saveProfile(profile: FarmerProfileEntity) {
    dao.insertOrUpdateProfile(profile)
  }

  // Tracked Crops
  val cropsFlow: Flow<List<TrackedCropEntity>> = dao.getAllTrackedCrops()

  suspend fun addCrop(crop: TrackedCropEntity): Long {
    return dao.insertCrop(crop)
  }

  suspend fun updateCrop(crop: TrackedCropEntity) {
    dao.updateCrop(crop)
  }

  suspend fun removeCrop(cropId: Long) {
    dao.deleteCropById(cropId)
  }

  // Farm Tasks
  val tasksFlow: Flow<List<FarmTaskEntity>> = dao.getAllTasks()

  suspend fun addTask(task: FarmTaskEntity): Long {
    return dao.insertTask(task)
  }

  suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
    dao.setTaskCompleted(taskId, isCompleted)
  }

  suspend fun deleteTask(taskId: Long) {
    dao.deleteTaskById(taskId)
  }

  // Saved Advice
  val savedAdviceFlow: Flow<List<SavedAdviceEntity>> = dao.getAllSavedAdvice()

  suspend fun saveAdvice(advice: SavedAdviceEntity): Long {
    return dao.insertAdvice(advice)
  }

  suspend fun deleteAdvice(adviceId: Long) {
    dao.deleteAdviceById(adviceId)
  }

  // Diagnostics History
  val diagnosticHistoryFlow: Flow<List<DiagnosticRecordEntity>> = dao.getAllDiagnosticRecords()

  suspend fun recordDiagnosis(
    cropName: String,
    result: CropDiagnosisResponse
  ): Long {
    val entity = DiagnosticRecordEntity(
      cropName = cropName,
      diseaseName = result.diseaseName,
      diseaseNameAmharic = result.diseaseNameAmharic,
      confidenceScore = result.confidenceScore,
      severityLevel = result.severityLevel,
      symptoms = result.symptomsSummary,
      organicRemedy = result.organicTreatment,
      chemicalRemedy = result.chemicalTreatment,
      amharicSummary = result.amharicAdvice
    )
    return dao.insertDiagnosticRecord(entity)
  }

  suspend fun deleteDiagnosticRecord(recordId: Long) {
    dao.deleteDiagnosticRecord(recordId)
  }

  // Static Offline Knowledge
  fun getCropGuides(): List<CropGuide> = StaticCropKnowledge.CROP_GUIDES

  fun getCropGuideById(id: String): CropGuide? =
    StaticCropKnowledge.CROP_GUIDES.firstOrNull { it.id == id }

  fun getKnownDiseases(): List<CropDisease> = StaticCropKnowledge.CROP_DISEASES

  fun getWeatherReport(regionKey: String): AgroWeather {
    return StaticCropKnowledge.SAMPLE_WEATHER_REPORTS[regionKey]
      ?: StaticCropKnowledge.SAMPLE_WEATHER_REPORTS.values.first()
  }

  fun getAvailableRegions(): List<String> =
    StaticCropKnowledge.SAMPLE_WEATHER_REPORTS.keys.toList()

  // AI Agronomist Operations
  suspend fun diagnoseCropIssue(
    cropName: String,
    symptomsDescription: String,
    leafBitmap: Bitmap?,
    isAmharic: Boolean
  ): CropDiagnosisResponse {
    return geminiService.diagnoseCropDisease(cropName, symptomsDescription, leafBitmap, isAmharic)
  }

  suspend fun askAgronomist(
    question: String,
    chatHistory: List<Pair<String, String>>,
    isAmharic: Boolean
  ): Pair<String, String> {
    return geminiService.askAgronomist(question, chatHistory, isAmharic)
  }

  // Initial Seeding
  suspend fun seedInitialDataIfEmpty() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    val currentProfile = dao.getFarmerProfile().firstOrNull()
    if (currentProfile == null) {
      dao.insertOrUpdateProfile(
        FarmerProfileEntity(
          id = 1,
          fullName = "Mehret Araya",
          phoneNumber = "+251 91 123 4567",
          region = "Oromia / Arsi",
          woreda = "Tiyo Woreda",
          farmSizeHectares = 3.5,
          primarySoilType = "Fertile Vertisol (Black Clay)",
          preferredLanguage = "en",
          farmingExperienceYears = 6
        )
      )

      // Sample Crops
      val now = System.currentTimeMillis()
      val dayMillis = 86400000L
      dao.insertCrop(
        TrackedCropEntity(
          cropName = "Wheat",
          cropNameAmharic = "ስንዴ",
          variety = "Daka / Kingbird (Rust-Resistant)",
          plantingDateMillis = now - (45 * dayMillis),
          expectedHarvestDateMillis = now + (75 * dayMillis),
          plotSizeHectares = 2.0,
          currentStage = "Stem Elongation / Tillering",
          stageProgress = 0.55f,
          healthStatus = "Healthy",
          notes = "First round of weeding completed. Leaf color is deep green."
        )
      )

      dao.insertCrop(
        TrackedCropEntity(
          cropName = "Maize",
          cropNameAmharic = "በቆሎ",
          variety = "BH-661 (Highland Hybrid)",
          plantingDateMillis = now - (25 * dayMillis),
          expectedHarvestDateMillis = now + (110 * dayMillis),
          plotSizeHectares = 1.0,
          currentStage = "Knee-High Vegetative",
          stageProgress = 0.30f,
          healthStatus = "Attention",
          notes = "Scouted small windowpane holes on leaves. Possible early Fall Armyworm presence."
        )
      )

      dao.insertCrop(
        TrackedCropEntity(
          cropName = "Teff",
          cropNameAmharic = "ጤፍ",
          variety = "Quncho (DZ-Cr-37)",
          plantingDateMillis = now - (60 * dayMillis),
          expectedHarvestDateMillis = now + (35 * dayMillis),
          plotSizeHectares = 0.5,
          currentStage = "Heading / Panicle Formation",
          stageProgress = 0.75f,
          healthStatus = "Healthy",
          notes = "Row planting trial. Excellent uniform tillering."
        )
      )

      // Sample Tasks
      dao.insertTask(
        FarmTaskEntity(
          title = "Apply Topdress Urea on Wheat",
          titleAmharic = "በስንዴ ማሳ ላይ የዩሪያ ማዳበሪያ መጨመር",
          cropName = "Wheat",
          dueDateFormatted = "In 2 days",
          isCompleted = false,
          priority = "High",
          category = "Fertilization"
        )
      )

      dao.insertTask(
        FarmTaskEntity(
          title = "Scout Maize whorls for Fall Armyworm",
          titleAmharic = "የበቆሎ ተምች መኖሩን በቅጠሉ እምብርት ላይ መፈተሽ",
          cropName = "Maize",
          dueDateFormatted = "Tomorrow morning",
          isCompleted = false,
          priority = "High",
          category = "Pest Control"
        )
      )

      dao.insertTask(
        FarmTaskEntity(
          title = "Prepare tarps for Teff threshing yard",
          titleAmharic = "ለጤፍ ማበጠሪያ ንፁህ ሸራ ማዘጋጀት",
          cropName = "Teff",
          dueDateFormatted = "Next week",
          isCompleted = false,
          priority = "Medium",
          category = "Harvest"
        )
      )
    }
  }
}
