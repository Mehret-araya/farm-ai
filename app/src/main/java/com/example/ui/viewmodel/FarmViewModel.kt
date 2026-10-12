package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.DiagnosticRecordEntity
import com.example.data.local.entity.FarmTaskEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.SavedAdviceEntity
import com.example.data.local.entity.TrackedCropEntity
import com.example.data.model.AgroWeather
import com.example.data.model.AgronomistMessage
import com.example.data.model.CropDisease
import com.example.data.model.CropGuide
import com.example.data.model.MessageSender
import com.example.data.remote.CropDiagnosisResponse
import com.example.data.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FarmViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: FarmRepository

  init {
    val db = AppDatabase.getInstance(application)
    repository = FarmRepository(db)
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  // Language state: true = Amharic (አማርኛ), false = English
  private val _isAmharic = MutableStateFlow(false)
  val isAmharic: StateFlow<Boolean> = _isAmharic.asStateFlow()

  fun toggleLanguage() {
    _isAmharic.value = !_isAmharic.value
  }

  fun setLanguage(amharic: Boolean) {
    _isAmharic.value = amharic
  }

  // Farmer Profile
  val profile: StateFlow<FarmerProfileEntity?> = repository.profileFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Tracked Crops
  val crops: StateFlow<List<TrackedCropEntity>> = repository.cropsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Farm Tasks
  val tasks: StateFlow<List<FarmTaskEntity>> = repository.tasksFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Saved Advice
  val savedAdvice: StateFlow<List<SavedAdviceEntity>> = repository.savedAdviceFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Diagnostics History
  val diagnosticRecords: StateFlow<List<DiagnosticRecordEntity>> = repository.diagnosticHistoryFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Weather
  val availableRegions = repository.getAvailableRegions()
  private val _selectedRegion = MutableStateFlow(availableRegions.firstOrNull() ?: "Arsi - Highlands (Dega)")
  val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

  private val _weather = MutableStateFlow(repository.getWeatherReport(_selectedRegion.value))
  val weather: StateFlow<AgroWeather> = _weather.asStateFlow()

  fun selectRegion(region: String) {
    _selectedRegion.value = region
    _weather.value = repository.getWeatherReport(region)
  }

  // Crop Guides
  val cropGuides: List<CropGuide> = repository.getCropGuides()
  private val _selectedGuide = MutableStateFlow<CropGuide?>(cropGuides.firstOrNull())
  val selectedGuide: StateFlow<CropGuide?> = _selectedGuide.asStateFlow()

  fun selectGuide(guide: CropGuide) {
    _selectedGuide.value = guide
  }

  // Known Diseases for quick test/demo
  val knownDiseases: List<CropDisease> = repository.getKnownDiseases()

  // Disease Scanner State
  private val _isDiagnosing = MutableStateFlow(false)
  val isDiagnosing: StateFlow<Boolean> = _isDiagnosing.asStateFlow()

  private val _currentDiagnosis = MutableStateFlow<CropDiagnosisResponse?>(null)
  val currentDiagnosis: StateFlow<CropDiagnosisResponse?> = _currentDiagnosis.asStateFlow()

  private val _selectedLeafBitmap = MutableStateFlow<Bitmap?>(null)
  val selectedLeafBitmap: StateFlow<Bitmap?> = _selectedLeafBitmap.asStateFlow()

  fun setSelectedLeafBitmap(bitmap: Bitmap?) {
    _selectedLeafBitmap.value = bitmap
  }

  fun runDiseaseDiagnosis(cropName: String, symptoms: String) {
    viewModelScope.launch {
      _isDiagnosing.value = true
      val result = repository.diagnoseCropIssue(
        cropName = cropName,
        symptomsDescription = symptoms,
        leafBitmap = _selectedLeafBitmap.value,
        isAmharic = _isAmharic.value
      )
      _currentDiagnosis.value = result
      _isDiagnosing.value = false

      // Auto record in history
      repository.recordDiagnosis(cropName, result)
    }
  }

  fun selectPreloadedDisease(disease: CropDisease) {
    val simulated = CropDiagnosisResponse(
      diseaseName = disease.diseaseName,
      diseaseNameAmharic = disease.diseaseNameAmharic,
      confidenceScore = 95,
      severityLevel = "Moderate",
      symptomsSummary = disease.symptoms,
      organicTreatment = disease.organicControl,
      chemicalTreatment = disease.chemicalControl,
      preventionTips = disease.prevention,
      amharicAdvice = disease.amharicAdvice
    )
    _currentDiagnosis.value = simulated
    viewModelScope.launch {
      repository.recordDiagnosis(disease.cropName, simulated)
    }
  }

  fun clearCurrentDiagnosis() {
    _currentDiagnosis.value = null
    _selectedLeafBitmap.value = null
  }

  fun selectHistoryRecord(record: DiagnosticRecordEntity) {
    _currentDiagnosis.value = CropDiagnosisResponse(
      diseaseName = record.diseaseName,
      diseaseNameAmharic = record.diseaseNameAmharic,
      confidenceScore = record.confidenceScore,
      severityLevel = record.severityLevel,
      symptomsSummary = record.symptoms,
      organicTreatment = record.organicRemedy,
      chemicalTreatment = record.chemicalRemedy,
      preventionTips = "Practice crop rotation, field hygiene, and early scouting.",
      amharicAdvice = record.amharicSummary
    )
  }

  fun deleteDiagnosticRecord(recordId: Long) {
    viewModelScope.launch {
      repository.deleteDiagnosticRecord(recordId)
    }
  }

  fun deleteSavedAdvice(adviceId: Long) {
    viewModelScope.launch {
      repository.deleteAdvice(adviceId)
    }
  }

  fun resetChat() {
    _chatMessages.value = listOf(
      AgronomistMessage(
        id = System.currentTimeMillis().toString(),
        sender = MessageSender.AI_AGRONOMIST,
        content = "Consultation reset. How can I assist you with your crops today? 🌱",
        contentAmharic = "አዲስ ውይይት ተጀምሯል። ዛሬ ስለ ሰብልዎ ምን ማወቅ ይፈልጋሉ? 🌱"
      )
    )
  }

  fun updateCropNotes(crop: TrackedCropEntity, newNotes: String) {
    viewModelScope.launch {
      repository.updateCrop(crop.copy(notes = newNotes))
    }
  }

  // Chat State
  private val _chatMessages = MutableStateFlow<List<AgronomistMessage>>(
    listOf(
      AgronomistMessage(
        id = "init",
        sender = MessageSender.AI_AGRONOMIST,
        content = "Hello! I am Dr. Almaz, your AI Senior Agronomist 🌱. Ask me anything about crop diseases, pest control, fertilizer scheduling, or soil care in Ethiopia.",
        contentAmharic = "ሰላም! እኔ ዶ/ር አልማዝ የእርሻ ባለሙያዎ ነኝ 🌱። ስለ ሰብል በሽታዎች፣ ማዳበሪያ አጠቃቀም፣ ተባዮችና አፈር እንክብካቤ ማንኛውንም ጥያቄ ይጠይቁኝ።"
      )
    )
  )
  val chatMessages: StateFlow<List<AgronomistMessage>> = _chatMessages.asStateFlow()

  private val _isChatLoading = MutableStateFlow(false)
  val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

  fun sendChatMessage(question: String) {
    if (question.isBlank()) return

    val userMsg = AgronomistMessage(
      id = System.currentTimeMillis().toString(),
      sender = MessageSender.USER,
      content = question
    )
    _chatMessages.value = _chatMessages.value + userMsg

    viewModelScope.launch {
      _isChatLoading.value = true
      val history = _chatMessages.value.map {
        Pair(if (it.sender == MessageSender.USER) "user" else "model", it.content)
      }

      val response = repository.askAgronomist(question, history, _isAmharic.value)
      val aiMsg = AgronomistMessage(
        id = (System.currentTimeMillis() + 1).toString(),
        sender = MessageSender.AI_AGRONOMIST,
        content = response.first,
        contentAmharic = response.second.ifBlank { null }
      )
      _chatMessages.value = _chatMessages.value + aiMsg
      _isChatLoading.value = false
    }
  }

  fun saveAdviceFromChat(question: String, answer: String, amharic: String = "") {
    viewModelScope.launch {
      repository.saveAdvice(
        SavedAdviceEntity(
          topic = "Agronomist Advice",
          question = question,
          answer = answer,
          answerAmharic = amharic,
          source = "AI Agronomist"
        )
      )
    }
  }

  // Crop Management
  fun addNewCrop(
    name: String,
    variety: String,
    hectares: Double,
    stage: String,
    notes: String
  ) {
    viewModelScope.launch {
      val amharicName = when (name.lowercase()) {
        "wheat" -> "ስንዴ"
        "teff" -> "ጤፍ"
        "maize" -> "በቆሎ"
        "coffee" -> "ቡና"
        "barley" -> "ገብስ"
        "sorghum" -> "ማሽላ"
        "tomato" -> "ቲማቲም"
        "potato" -> "ድንች"
        else -> name
      }

      val now = System.currentTimeMillis()
      val dayMillis = 86400000L
      val newCrop = TrackedCropEntity(
        cropName = name,
        cropNameAmharic = amharicName,
        variety = variety.ifBlank { "Standard Improved" },
        plantingDateMillis = now,
        expectedHarvestDateMillis = now + (90 * dayMillis),
        plotSizeHectares = hectares,
        currentStage = stage,
        stageProgress = 0.2f,
        healthStatus = "Healthy",
        notes = notes
      )
      repository.addCrop(newCrop)
    }
  }

  fun advanceCropStage(crop: TrackedCropEntity) {
    viewModelScope.launch {
      val stages = listOf("Sowing", "Germination", "Vegetative", "Flowering", "Maturity", "Harvest Ready")
      val currentIndex = stages.indexOf(crop.currentStage)
      val nextIndex = if (currentIndex >= 0 && currentIndex < stages.size - 1) currentIndex + 1 else currentIndex
      val nextStage = stages[nextIndex]
      val nextProgress = (nextIndex + 1).toFloat() / stages.size.toFloat()

      repository.updateCrop(
        crop.copy(
          currentStage = nextStage,
          stageProgress = nextProgress
        )
      )
    }
  }

  fun deleteCrop(cropId: Long) {
    viewModelScope.launch {
      repository.removeCrop(cropId)
    }
  }

  // Task Management
  fun toggleTask(taskId: Long, isCompleted: Boolean) {
    viewModelScope.launch {
      repository.toggleTaskCompletion(taskId, isCompleted)
    }
  }

  fun addNewTask(title: String, crop: String, due: String, priority: String, category: String) {
    viewModelScope.launch {
      repository.addTask(
        FarmTaskEntity(
          title = title,
          titleAmharic = title,
          cropName = crop,
          dueDateFormatted = due,
          isCompleted = false,
          priority = priority,
          category = category
        )
      )
    }
  }

  fun deleteTask(taskId: Long) {
    viewModelScope.launch {
      repository.deleteTask(taskId)
    }
  }

  // Profile Management
  fun updateProfile(
    fullName: String,
    phone: String,
    region: String,
    woreda: String,
    farmSize: Double,
    soil: String,
    lang: String,
    experienceYears: Int
  ) {
    viewModelScope.launch {
      repository.saveProfile(
        FarmerProfileEntity(
          id = 1,
          fullName = fullName,
          phoneNumber = phone,
          region = region,
          woreda = woreda,
          farmSizeHectares = farmSize,
          primarySoilType = soil,
          preferredLanguage = lang,
          farmingExperienceYears = experienceYears
        )
      )
    }
  }

  fun switchDemoFarmer(index: Int) {
    viewModelScope.launch {
      when (index) {
        0 -> updateProfile("Mehret Araya", "+251 91 123 4567", "Oromia / Arsi", "Tiyo Woreda", 3.5, "Vertisol (Black Clay)", "en", 6)
        1 -> updateProfile("Abebe Bikila", "+251 92 345 6789", "Amhara / Gojjam", "Debre Markos", 4.2, "Red Clay Loam", "am", 12)
        2 -> updateProfile("Almaz Kebede", "+251 93 456 7890", "Sidama / Hawassa", "Dale Woreda", 2.0, "Volcanic Loam", "en", 8)
      }
    }
  }
}
