package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiAgronomistService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  /**
   * Diagnoses crop disease from leaf photo or symptom description
   */
  suspend fun diagnoseCropDisease(
    cropName: String,
    symptomDescription: String,
    leafBitmap: Bitmap?,
    isAmharic: Boolean
  ): CropDiagnosisResponse = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (apiKey.isNullOrBlank()) {
      // Offline / Keyless fallback: return expert rule-based agronomy result
      return@withContext FallbackAgronomistEngine.diagnoseLocally(cropName, symptomDescription)
    }

    try {
      val promptText = buildString {
        append("You are an expert tropical and highland agronomist and crop pathologist. ")
        append("Analyze this crop health issue for crop: $cropName. ")
        append("Symptoms: $symptomDescription. ")
        append("Respond strictly in valid JSON with these exact fields: ")
        append("{ \"diseaseName\": \"...\", \"diseaseNameAmharic\": \"...\", ")
        append("\"confidence\": 92, \"severity\": \"Moderate\", ")
        append("\"symptomsSummary\": \"...\", \"organicTreatment\": \"...\", ")
        append("\"chemicalTreatment\": \"...\", \"prevention\": \"...\", ")
        append("\"amharicAdvice\": \"...\" }")
      }

      val contentsArray = JSONArray()
      val partsArray = JSONArray()

      // Add text prompt
      val textPart = JSONObject().put("text", promptText)
      partsArray.put(textPart)

      // Add image if available
      if (leafBitmap != null) {
        val base64Data = bitmapToBase64(leafBitmap)
        val imagePart = JSONObject().put("inlineData", JSONObject().apply {
          put("mimeType", "image/jpeg")
          put("data", base64Data)
        })
        partsArray.put(imagePart)
      }

      contentsArray.put(JSONObject().put("parts", partsArray))

      val requestJson = JSONObject().apply {
        put("contents", contentsArray)
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.2)
          put("responseMimeType", "application/json")
        })
      }

      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val responseBody = response.body?.string()

      if (!response.isSuccessful || responseBody.isNullOrBlank()) {
        return@withContext FallbackAgronomistEngine.diagnoseLocally(cropName, symptomDescription)
      }

      val parsed = JSONObject(responseBody)
      val candidates = parsed.optJSONArray("candidates")
      val textResponse = candidates?.optJSONObject(0)
        ?.optJSONObject("content")
        ?.optJSONArray("parts")
        ?.optJSONObject(0)
        ?.optString("text")

      if (!textResponse.isNullOrBlank()) {
        val data = JSONObject(textResponse)
        CropDiagnosisResponse(
          diseaseName = data.optString("diseaseName", "Crop Leaf Rust / Blight"),
          diseaseNameAmharic = data.optString("diseaseNameAmharic", "የሰብል ቅጠል በሽታ"),
          confidenceScore = data.optInt("confidence", 91),
          severityLevel = data.optString("severity", "Moderate"),
          symptomsSummary = data.optString("symptomsSummary", symptomDescription),
          organicTreatment = data.optString("organicTreatment", "Neem extract & crop aeration"),
          chemicalTreatment = data.optString("chemicalTreatment", "Recommended broad-spectrum fungicide"),
          preventionTips = data.optString("prevention", "Maintain spacing and crop rotation"),
          amharicAdvice = data.optString("amharicAdvice", "ተክሎችን በተገቢው ርቀት መትከልና የተበላሹትን ቅጠሎች ማስወገድ")
        )
      } else {
        FallbackAgronomistEngine.diagnoseLocally(cropName, symptomDescription)
      }
    } catch (e: Exception) {
      FallbackAgronomistEngine.diagnoseLocally(cropName, symptomDescription)
    }
  }

  /**
   * Generates AI agronomist chat response
   */
  suspend fun askAgronomist(
    farmerQuestion: String,
    chatHistory: List<Pair<String, String>>, // sender to text
    isAmharic: Boolean
  ): Pair<String, String> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (apiKey.isNullOrBlank()) {
      return@withContext FallbackAgronomistEngine.answerChatLocally(farmerQuestion, isAmharic)
    }

    try {
      val systemPrompt = "You are Dr. Almaz, an expert AI Senior Agronomist serving smallholder farmers in East Africa (Ethiopia). " +
          "You provide practical, scientific, low-cost agronomic advice on planting, fertilizers (Urea, NPS), pest management (Fall Armyworm, Rust), irrigation, and post-harvest. " +
          "Always provide clear actionable advice. In addition to English, provide an accurate Amharic summary."

      val contentsArray = JSONArray()

      // Add recent history
      for (turn in chatHistory.takeLast(6)) {
        val role = if (turn.first == "user") "user" else "model"
        contentsArray.put(JSONObject().apply {
          put("role", role)
          put("parts", JSONArray().put(JSONObject().put("text", turn.second)))
        })
      }

      // Add user prompt
      val promptWithInstruction = if (isAmharic) {
        "Farmer asks (in Amharic): $farmerQuestion. Please answer helpfully in Amharic with an English summary."
      } else {
        "Farmer asks: $farmerQuestion. Please provide English advice followed by: '---AMHARIC_SUMMARY---' and then concise Amharic translation."
      }

      contentsArray.put(JSONObject().apply {
        put("role", "user")
        put("parts", JSONArray().put(JSONObject().put("text", promptWithInstruction)))
      })

      val requestJson = JSONObject().apply {
        put("contents", contentsArray)
        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
        })
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.4)
          put("maxOutputTokens", 1024)
        })
      }

      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val request = Request.Builder()
        .url(url)
        .post(requestJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val responseBody = response.body?.string()

      if (!response.isSuccessful || responseBody.isNullOrBlank()) {
        return@withContext FallbackAgronomistEngine.answerChatLocally(farmerQuestion, isAmharic)
      }

      val parsed = JSONObject(responseBody)
      val candidates = parsed.optJSONArray("candidates")
      val textResponse = candidates?.optJSONObject(0)
        ?.optJSONObject("content")
        ?.optJSONArray("parts")
        ?.optJSONObject(0)
        ?.optString("text") ?: ""

      if (textResponse.isNotBlank()) {
        if (textResponse.contains("---AMHARIC_SUMMARY---")) {
          val parts = textResponse.split("---AMHARIC_SUMMARY---")
          Pair(parts[0].trim(), parts.getOrNull(1)?.trim() ?: "")
        } else {
          Pair(textResponse.trim(), "")
        }
      } else {
        FallbackAgronomistEngine.answerChatLocally(farmerQuestion, isAmharic)
      }
    } catch (e: Exception) {
      FallbackAgronomistEngine.answerChatLocally(farmerQuestion, isAmharic)
    }
  }

  private fun getApiKey(): String? {
    return try {
      val key = BuildConfig.GEMINI_API_KEY
      if (key.isNullOrBlank() || key.contains("MY_GEMINI_API_KEY")) null else key
    } catch (e: Exception) {
      null
    }
  }

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    // Scale down if large to reduce payload
    val scaledBitmap = if (bitmap.width > 1024 || bitmap.height > 1024) {
      val ratio = 1024f / Math.max(bitmap.width, bitmap.height)
      Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
    } else bitmap
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }
}

data class CropDiagnosisResponse(
  val diseaseName: String,
  val diseaseNameAmharic: String,
  val confidenceScore: Int,
  val severityLevel: String,
  val symptomsSummary: String,
  val organicTreatment: String,
  val chemicalTreatment: String,
  val preventionTips: String,
  val amharicAdvice: String
)
