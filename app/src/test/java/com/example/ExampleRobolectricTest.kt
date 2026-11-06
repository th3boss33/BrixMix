package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CreationGenerator
import com.example.model.BuildCategory
import com.example.model.ComplexityTier
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
  fun `read string from context matches app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BrixMix", appName)
  }

  @Test
  fun `generator produces simple creation within piece range`() {
    val creation = CreationGenerator.generate(targetPieces = 45, categoryFilter = BuildCategory.SPACE_MECH)
    assertEquals(ComplexityTier.SIMPLE, creation.tier)
    assertEquals(45, creation.pieceCount)
    val totalParts = creation.parts.sumOf { it.quantity }
    assertEquals(45, totalParts)
    assertTrue(creation.steps.isNotEmpty())
  }

  @Test
  fun `generator produces extreme 1100 piece titan creation`() {
    val titan = CreationGenerator.generate(combinationSeed = 1100)
    assertEquals(ComplexityTier.EXTREME, titan.tier)
    assertEquals(1100, titan.pieceCount)
    assertEquals(1100, titan.combinationNumber)
    val totalParts = titan.parts.sumOf { it.quantity }
    assertEquals(1100, totalParts)
    assertTrue(titan.techniques.isNotEmpty())
  }

  @Test
  fun `matrix item generation covers full 1 to 1100 scale`() {
    val item1 = CreationGenerator.getMatrixItem(1)
    val item500 = CreationGenerator.getMatrixItem(500)
    val item1100 = CreationGenerator.getMatrixItem(1100)

    assertEquals(1, item1.index)
    assertTrue(item1.pieceCount in 10..75)

    assertEquals(500, item500.index)
    assertTrue(item500.pieceCount in 76..250)

    assertEquals(1100, item1100.index)
    assertEquals(1100, item1100.pieceCount)
    assertEquals(ComplexityTier.EXTREME, item1100.tier)
  }
}
