package com.example.data.model

data class CropGuide(
  val id: String,
  val name: String,
  val nameAmharic: String,
  val category: String, // Cereal, Cash Crop, Pulse, Horticulture
  val scientificName: String,
  val altitudeMeters: String,
  val rainfallMm: String,
  val soilType: String,
  val seedRate: String,
  val plantingSeason: String,
  val fertilizerGuide: String,
  val commonDiseases: List<String>,
  val harvestingTips: String,
  val amharicSummary: String
)

data class CropDisease(
  val id: String,
  val cropName: String,
  val diseaseName: String,
  val diseaseNameAmharic: String,
  val pathogenType: String, // Fungal, Pest, Bacterial, Viral, Deficiency
  val symptoms: String,
  val organicControl: String,
  val chemicalControl: String,
  val prevention: String,
  val amharicAdvice: String,
  val sampleLeafEmoji: String = "🍃"
)

data class AgroWeather(
  val regionName: String,
  val zoneNameAmharic: String,
  val temperatureC: Int,
  val condition: String,
  val conditionAmharic: String,
  val rainfallProbability: Int,
  val humidityPercent: Int,
  val soilMoisture: String,
  val sprayingFeasibility: String, // "Favorable", "Caution - Rain Imminent", "Unfavorable"
  val farmingAdvisory: String,
  val farmingAdvisoryAmharic: String,
  val dailyForecast: List<DailyAgroForecast>
)

data class DailyAgroForecast(
  val day: String,
  val dayAmharic: String,
  val tempMax: Int,
  val tempMin: Int,
  val rainProb: Int,
  val icon: String
)

data class AgronomistMessage(
  val id: String,
  val sender: MessageSender,
  val content: String,
  val contentAmharic: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
  USER,
  AI_AGRONOMIST
}
