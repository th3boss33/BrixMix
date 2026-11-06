package com.example.data

import android.util.Log
import com.example.model.BuildCategory
import com.example.model.BuildStep
import com.example.model.ComplexityTier
import com.example.model.LegoCreation
import com.example.model.LegoPaletteColor
import com.example.model.LegoPart
import com.example.model.PartCategory
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private const val TAG = "GeminiBuildService"

object GeminiBuildService {

  private val legoCreationSchema = Schema.obj(
    properties = mapOf(
      "title" to Schema.string(description = "Unique, creative title for the Lego model"),
      "subtitle" to Schema.string(description = "Short subtitle e.g. 'Micro • Space & Mechs • 36 Elements'"),
      "pieceCount" to Schema.integer(description = "Total piece count matching the requested complexity level"),
      "categoryName" to Schema.string(description = "Category name e.g. SPACE_MECH, VEHICLES, MEDIEVAL_FANTASY, CREATURES, ARCHITECTURE, or QUIRKY_GADGETS"),
      "lore" to Schema.string(description = "Engaging 2-3 sentence lore and background description"),
      "techniques" to Schema.array(
        items = Schema.string(),
        description = "2 to 4 master building techniques used"
      ),
      "palette" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "name" to Schema.string(description = "Lego color name e.g. Bright Red, Warm Yellow, Classic Black"),
            "hex" to Schema.string(description = "Hex string e.g. #E52521")
          )
        ),
        description = "2 to 4 primary Lego colors"
      ),
      "parts" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "name" to Schema.string(description = "Lego element name e.g. 2x4 Brick, 1x2 Plate, 2x2 Slope"),
            "category" to Schema.string(description = "BRICKS, PLATES_TILES, SLOPES_WEDGES, TECHNIC_PINS, or SPECIALTY_SNOT"),
            "quantity" to Schema.integer(description = "Quantity of this piece"),
            "colorName" to Schema.string(description = "Color of the part"),
            "colorHex" to Schema.string(description = "Hex color string"),
            "elementNumber" to Schema.string(description = "Design ID number e.g. 3001, 3020")
          )
        ),
        description = "List of parts summing up to the piece count"
      ),
      "steps" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "stepNumber" to Schema.integer(description = "1 to 5"),
            "phaseName" to Schema.string(description = "Phase title e.g. Foundation, Internal Core, Armor Shell"),
            "instruction" to Schema.string(description = "Step by step assembly instructions"),
            "focusPieces" to Schema.string(description = "Bricks needed for this step"),
            "builderTip" to Schema.string(description = "Pro tip for clutch power or alignment")
          )
        ),
        description = "5 sequential building phase instructions"
      )
    )
  )

  private val generativeModel by lazy {
    val config = generationConfig {
      responseMimeType = "application/json"
      responseSchema = legoCreationSchema
    }
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = config
    )
  }

  suspend fun forgeCreationWithAi(
    complexityLevel: String, // "Micro", "Simple", "Moderate", "Complex", "Extreme"
    targetPieces: Int,
    category: BuildCategory,
    themePrompt: String = ""
  ): LegoCreation {
    val prompt = buildString {
      appendLine("You are an official Lego Master Builder AI for the app BrixMix.")
      appendLine("Generate a complete, authentic, structurally sound Lego build schema.")
      appendLine("Complexity Level: $complexityLevel")
      appendLine("Target Piece Count: Approximately $targetPieces pieces (range 10 to 1100 pieces max).")
      if (category != BuildCategory.ALL) {
        appendLine("Theme Category: ${category.displayName}")
      }
      if (themePrompt.isNotBlank()) {
        appendLine("User Creative Direction: $themePrompt")
      }
      appendLine("Rules:")
      appendLine("1. Use authentic Lego elements (Standard Bricks, Plates & Tiles, Slopes & Wedges, Technic Pins, Specialty SNOT).")
      appendLine("2. Use iconic Lego colors (Bright Red #E52521, Warm Yellow #FFCE00, Cobalt Blue #006CB7, Classic Black #1B1D20, Pure White #F4F6F8, Dark Bluish Gray #4C525C, Safety Orange #FA6800, Lime Green #7CB342).")
      appendLine("3. The sum of quantities in parts must equal pieceCount.")
      appendLine("4. Provide 5 distinct building phases from Foundation up to Greebling & Accents.")
    }

    return try {
      val response = withTimeout(60_000L) {
        generativeModel.generateContent(prompt)
      }

      val jsonString = response.text ?: throw IllegalStateException("Gemini returned empty response text.")
      parseCreationFromJson(jsonString, complexityLevel, targetPieces, category)
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Gemini creation generation timed out after 60s", e)
      throw Exception("AI creation generation timed out. Please try again.")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Failed to forge creation with Gemini AI", e)
      throw e
    }
  }

  private fun parseCreationFromJson(
    jsonString: String,
    complexityLevel: String,
    targetPieces: Int,
    categoryHint: BuildCategory
  ): LegoCreation {
    val json = JSONObject(jsonString)

    val title = json.optString("title", "Gemini Forged Creation")
    val subtitle = json.optString("subtitle", "$complexityLevel • $targetPieces Elements")
    val pieceCount = json.optInt("pieceCount", targetPieces).coerceIn(10, 1100)
    val lore = json.optString("lore", "An AI-engineered custom Lego masterpiece.")

    val tier = when {
      pieceCount <= 75 -> ComplexityTier.SIMPLE
      pieceCount <= 250 -> ComplexityTier.MODERATE
      pieceCount <= 600 -> ComplexityTier.COMPLEX
      else -> ComplexityTier.EXTREME
    }

    val catName = json.optString("categoryName", "")
    val category = try {
      BuildCategory.valueOf(catName)
    } catch (_: Exception) {
      if (categoryHint != BuildCategory.ALL) categoryHint else BuildCategory.SPACE_MECH
    }

    val palette = mutableListOf<LegoPaletteColor>()
    val paletteArr = json.optJSONArray("palette") ?: JSONArray()
    for (i in 0 until paletteArr.length()) {
      val obj = paletteArr.optJSONObject(i)
      if (obj != null) {
        palette.add(
          LegoPaletteColor(
            name = obj.optString("name", "Lego Color"),
            hex = obj.optString("hex", "#E52521")
          )
        )
      }
    }
    if (palette.isEmpty()) {
      palette.add(CreationGenerator.COLOR_RED)
      palette.add(CreationGenerator.COLOR_YELLOW)
    }

    val techniques = mutableListOf<String>()
    val techArr = json.optJSONArray("techniques") ?: JSONArray()
    for (i in 0 until techArr.length()) {
      techniques.add(techArr.getString(i))
    }
    if (techniques.isEmpty()) {
      techniques.add("SNOT (Studs Not On Top) Bracket Geometry")
      techniques.add("Offset Jumper Plate Centering")
    }

    val parts = mutableListOf<LegoPart>()
    val partsArr = json.optJSONArray("parts") ?: JSONArray()
    for (i in 0 until partsArr.length()) {
      val obj = partsArr.optJSONObject(i)
      if (obj != null) {
        val catStr = obj.optString("category", "BRICKS")
        val partCat = try {
          PartCategory.valueOf(catStr)
        } catch (_: Exception) {
          PartCategory.BRICKS
        }
        parts.add(
          LegoPart(
            id = "ai-part-${i + 1}",
            name = obj.optString("name", "Standard Brick"),
            category = partCat,
            quantity = obj.optInt("quantity", 1).coerceAtLeast(1),
            colorName = obj.optString("colorName", palette.first().name),
            colorHex = obj.optString("colorHex", palette.first().hex),
            elementNumber = obj.optString("elementNumber", "3001")
          )
        )
      }
    }

    val steps = mutableListOf<BuildStep>()
    val stepsArr = json.optJSONArray("steps") ?: JSONArray()
    for (i in 0 until stepsArr.length()) {
      val obj = stepsArr.optJSONObject(i)
      if (obj != null) {
        steps.add(
          BuildStep(
            stepNumber = obj.optInt("stepNumber", i + 1),
            phaseName = obj.optString("phaseName", "Phase ${i + 1}"),
            instruction = obj.optString("instruction", "Assemble modular sub-components."),
            focusPieces = obj.optString("focusPieces", "Primary plates and bricks"),
            builderTip = obj.optString("builderTip", "Check clutch strength before moving to next stage.")
          )
        )
      }
    }

    val combinationNumber = (pieceCount * 31 + title.hashCode()).let { kotlin.math.abs(it) % 1100 + 1 }
    val estMinutes = (pieceCount * 0.16 + 10).toInt()

    return LegoCreation(
      id = "ai-forge-${UUID.randomUUID().toString().take(8)}",
      title = title,
      subtitle = subtitle,
      pieceCount = pieceCount,
      tier = tier,
      category = category,
      lore = lore,
      palette = palette,
      techniques = techniques,
      parts = parts.ifEmpty { CreationGenerator.generate(pieceCount, category, combinationNumber).parts },
      steps = steps.ifEmpty { CreationGenerator.generate(pieceCount, category, combinationNumber).steps },
      combinationNumber = combinationNumber,
      estimatedBuildMinutes = estMinutes
    )
  }
}
