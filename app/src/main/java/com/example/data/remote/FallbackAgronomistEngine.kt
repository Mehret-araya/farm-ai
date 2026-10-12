package com.example.data.remote

import com.example.data.model.StaticCropKnowledge

object FallbackAgronomistEngine {

  fun diagnoseLocally(cropName: String, symptomDescription: String): CropDiagnosisResponse {
    val query = "$cropName $symptomDescription".lowercase()

    val matchedDisease = StaticCropKnowledge.CROP_DISEASES.firstOrNull { disease ->
      query.contains(disease.cropName.lowercase()) ||
          disease.symptoms.lowercase().split(" ").any { word -> word.length > 4 && query.contains(word) }
    } ?: StaticCropKnowledge.CROP_DISEASES.first()

    return CropDiagnosisResponse(
      diseaseName = matchedDisease.diseaseName,
      diseaseNameAmharic = matchedDisease.diseaseNameAmharic,
      confidenceScore = 93,
      severityLevel = "Moderate",
      symptomsSummary = matchedDisease.symptoms,
      organicTreatment = matchedDisease.organicControl,
      chemicalTreatment = matchedDisease.chemicalControl,
      preventionTips = matchedDisease.prevention,
      amharicAdvice = matchedDisease.amharicAdvice
    )
  }

  fun answerChatLocally(farmerQuestion: String, isAmharic: Boolean): Pair<String, String> {
    val q = farmerQuestion.lowercase()

    return when {
      q.contains("armyworm") || q.contains("ተምች") || q.contains("worm") || q.contains("በቆሎ") -> {
        val en = "For Fall Armyworm in Maize:\n1. Apply fine dry wood ash or sand directly into the central funnel/whorl when larvae are small.\n2. Hand-pick egg masses and caterpillars in early mornings.\n3. For severe infestations, apply certified insecticide such as Ampligo or Spinosad targeting the whorl.\n4. Push-pull intercropping with Desmodium repels moths effectively."
        val am = "የበቆሎ ተምችን ለመከላከል:\n1. በቆሎው እምብርት ውስጥ ደረቅ አመድ ወይም አሸዋ ይጨምሩ።\n2. ጠዋት ጠዋት በእጅ እንቁላልና ትሎችን ለቅሞ ማጥፋት።\n3. የከፋ ጥቃት ካለ ፀረ-ተባይ መድኃኒት ወደ እምብርቱ ብቻ መርጨት።"
        Pair(en, am)
      }

      q.contains("rust") || q.contains("ዋግ") || q.contains("ዝገት") || q.contains("ስንዴ") || q.contains("wheat") -> {
        val en = "For Wheat Stem & Stripe Rust:\n1. Rust thrives in humid, windy weather. Scout fields twice a week.\n2. Spray systemic triazole fungicides (e.g. Tebuconazole or Propiconazole) at the very first pustule sign.\n3. In upcoming seasons, strictly sow verified rust-resistant varieties like Kingbird or Ogolcho."
        val am = "የስንዴ ቢጫና ግንድ ዋግ በሽታን ለመከላከል:\n1. ማሳውን በየሳምንቱ ሁለት ጊዜ መፈተሽ።\n2. የዋግ ምልክት እንዳየ ወዲያውኑ ፀረ-ፈንገስ መርጨት።\n3. ለቀጣይ ወቅት በሽታውን የሚቋቋሙ የተመረጡ ዘሮችን መዝራት።"
        Pair(en, am)
      }

      q.contains("fertilizer") || q.contains("urea") || q.contains("nps") || q.contains("ማዳበሪያ") || q.contains("ዩሪያ") -> {
        val en = "Optimal Fertilizer Guidelines:\n1. NPS: Apply at planting alongside seed (not in direct seed contact to avoid scorch), 100 kg/ha for cereals.\n2. Urea (Nitrogen): Do not apply all at once. Split application is crucial! Apply 50% at tillering/knee-high stage and 50% prior to flowering.\n3. Always incorporate Urea into moist soil to avoid ammonia volatilization loss."
        val am = "የማዳበሪያ አጠቃቀም መመሪያ:\n1. ኤንፒኤስ (NPS): በዘር ወቅት ከመሬት ጋር ተደባልቆ ይሰጣል።\n2. ዩሪያ: በአንድ ጊዜ ሳይሆን ለሁለት ከፍሎ በጉልበት ቁመት እና ከአበባ በፊት በአርጥብ አፈር ውስጥ መጨመር።"
        Pair(en, am)
      }

      q.contains("teff") || q.contains("ጤፍ") || q.contains("seed rate") -> {
        val en = "Maximizing Teff Productivity:\n1. Transition from broad scattering to row planting (20cm row spacing, 10–15 kg/ha seed rate). This saves seed and increases yield by 25–40%.\n2. Firm the seedbed before and after broadcasting; teff seeds require shallow coverage (0.5 – 1 cm depth).\n3. Apply 100 kg NPS at planting and topdress with 50 kg Urea 3 weeks after germination."
        val am = "የጤፍ ምርታማነትን ለማሳደግ:\n1. በብተና ከመዝራት ይልቅ በመስመር (20 ሳ.ሜ ርቀት) መዝራት የዘር ብክነትን ይቀንሳል፣ ምርትን በ30% ያሳድጋል።\n2. የዘር ጥልቀት ከ1 ሳ.ሜ መብለጥ የለበትም።"
        Pair(en, am)
      }

      q.contains("coffee") || q.contains("ቡና") -> {
        val en = "High-Quality Coffee Agronomy:\n1. Maintain 40–50% shade canopy with indigenous trees (e.g. Millettia, Cordia africana).\n2. Prune old sucker branches after harvest to let air and light circulate, suppressing Coffee Leaf Rust.\n3. Pick only ripe red cherries for washing and sun-drying on raised African beds."
        val am = "ጥራት ያለው ቡና ለማምረት:\n1. ተስማሚ የጥላ ዛፎችን ማዘጋጀት።\n2. አሮጌ ቅርንጫፎችን በመከርከም የፈንገስ በሽታን መከላከል።\n3. ቀይ የደረሱ ፍሬዎችን ብቻ በጥንቃቄ መልቀም።"
        Pair(en, am)
      }

      q.contains("weeding") || q.contains("አረም") -> {
        val en = "Weed Control Advisory:\n1. First weeding must be conducted within 20 to 30 days of seedling emergence when crop-weed competition is critical.\n2. Second weeding prior to flowering prevents weed seed multiplication for subsequent seasons.\n3. For parasitic Striga (Arem Agam), hand-pull before flowering and rotate with trap crops like groundnuts."
        val am = "የአረም ቁጥጥር:\n1. የመጀመሪያው አረም ቡቃያው ከበቀለ ከ20-30 ቀናት ባለው ወሳኝ ወቅት መታረም አለበት።\n2. አረም አበባ ከማውጣቱ በፊት በማረም ለቀጣይ አመት እንዳይራባ ማድረግ።"
        Pair(en, am)
      }

      else -> {
        val en = "Thank you for consulting Dr. Almaz. As an agronomist, I advise monitoring your crop growth stages closely, inspecting leaves for early discoloration or insect damage, applying split fertilizers with timely rains, and maintaining farm records for season-to-season yield gains."
        val am = "የእርሻ ምክር: ሰብሎችዎ የሚያሳዩትን የእድገት ደረጃ በቅርበት ይከታተሉ፣ የቅጠል ቀለም መቀየር ወይም የተባይ ምልክት ሲኖር በፍጥነት እርምጃ ይውሰዱ፣ እና ማዳበሪያን በዝናብ ወቅት በከፊል ይስጡ።"
        Pair(en, am)
      }
    }
  }
}
