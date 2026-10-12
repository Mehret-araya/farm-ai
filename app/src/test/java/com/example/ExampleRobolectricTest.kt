package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.StaticCropKnowledge
import com.example.data.remote.FallbackAgronomistEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FarmAI", appName)
  }

  @Test
  fun `verify crop guides loaded correctly`() {
    val guides = StaticCropKnowledge.CROP_GUIDES
    assertTrue(guides.isNotEmpty())
    val teff = guides.firstOrNull { it.id == "teff" }
    assertNotNull(teff)
    assertEquals("Teff", teff?.name)
  }

  @Test
  fun `verify fallback agronomist handles disease query`() {
    val result = FallbackAgronomistEngine.diagnoseLocally("Wheat", "yellow rust pustules on stem")
    assertNotNull(result)
    assertTrue(result.diseaseName.contains("Rust", ignoreCase = true))
    assertTrue(result.confidenceScore > 80)
  }
}
