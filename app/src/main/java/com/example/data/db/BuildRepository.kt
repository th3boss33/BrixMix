package com.example.data.db

import com.example.model.BuildCategory
import com.example.model.BuildStep
import com.example.model.ComplexityTier
import com.example.model.LegoCreation
import com.example.model.LegoPaletteColor
import com.example.model.LegoPart
import com.example.model.PartCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class BuildRepository(private val dao: SavedBuildDao) {

  val allSavedCreations: Flow<List<LegoCreation>> = dao.getAllSavedBuilds().map { entities ->
    entities.map { it.toDomain() }
  }

  suspend fun isSaved(id: String): Boolean {
    return dao.getBuildById(id) != null
  }

  suspend fun saveCreation(creation: LegoCreation) {
    dao.insertBuild(creation.toEntity())
  }

  suspend fun removeCreation(id: String) {
    dao.deleteById(id)
  }

  suspend fun updateCreation(creation: LegoCreation) {
    dao.updateBuild(creation.toEntity())
  }

  suspend fun getCreationById(id: String): LegoCreation? {
    return dao.getBuildById(id)?.toDomain()
  }

  // --- Entity <-> Domain Conversion using Android built-in org.json ---

  private fun LegoCreation.toEntity(): SavedBuildEntity {
    val paletteArr = JSONArray()
    palette.forEach {
      val obj = JSONObject().apply {
        put("name", it.name)
        put("hex", it.hex)
      }
      paletteArr.put(obj)
    }

    val techArr = JSONArray()
    techniques.forEach { techArr.put(it) }

    val partsArr = JSONArray()
    parts.forEach {
      val obj = JSONObject().apply {
        put("id", it.id)
        put("name", it.name)
        put("category", it.category.name)
        put("quantity", it.quantity)
        put("colorName", it.colorName)
        put("colorHex", it.colorHex)
        put("elementNumber", it.elementNumber)
      }
      partsArr.put(obj)
    }

    val stepsArr = JSONArray()
    steps.forEach {
      val obj = JSONObject().apply {
        put("stepNumber", it.stepNumber)
        put("phaseName", it.phaseName)
        put("instruction", it.instruction)
        put("focusPieces", it.focusPieces)
        put("builderTip", it.builderTip)
      }
      stepsArr.put(obj)
    }

    return SavedBuildEntity(
      id = id,
      title = title,
      subtitle = subtitle,
      pieceCount = pieceCount,
      tierName = tier.name,
      categoryName = category.name,
      lore = lore,
      combinationNumber = combinationNumber,
      estimatedBuildMinutes = estimatedBuildMinutes,
      isBuilt = isBuilt,
      personalNotes = personalNotes,
      userRating = userRating,
      paletteJson = paletteArr.toString(),
      techniquesJson = techArr.toString(),
      partsJson = partsArr.toString(),
      stepsJson = stepsArr.toString()
    )
  }

  private fun SavedBuildEntity.toDomain(): LegoCreation {
    val tier = try {
      ComplexityTier.valueOf(tierName)
    } catch (_: Exception) {
      ComplexityTier.MODERATE
    }

    val category = try {
      BuildCategory.valueOf(categoryName)
    } catch (_: Exception) {
      BuildCategory.SPACE_MECH
    }

    val paletteList = mutableListOf<LegoPaletteColor>()
    if (paletteJson.isNotEmpty()) {
      try {
        val arr = JSONArray(paletteJson)
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          paletteList.add(LegoPaletteColor(obj.getString("name"), obj.getString("hex")))
        }
      } catch (_: Exception) {}
    }

    val techList = mutableListOf<String>()
    if (techniquesJson.isNotEmpty()) {
      try {
        val arr = JSONArray(techniquesJson)
        for (i in 0 until arr.length()) {
          techList.add(arr.getString(i))
        }
      } catch (_: Exception) {}
    }

    val partsList = mutableListOf<LegoPart>()
    if (partsJson.isNotEmpty()) {
      try {
        val arr = JSONArray(partsJson)
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          val partCat = try {
            PartCategory.valueOf(obj.getString("category"))
          } catch (_: Exception) {
            PartCategory.BRICKS
          }
          partsList.add(
            LegoPart(
              id = obj.getString("id"),
              name = obj.getString("name"),
              category = partCat,
              quantity = obj.getInt("quantity"),
              colorName = obj.getString("colorName"),
              colorHex = obj.getString("colorHex"),
              elementNumber = obj.optString("elementNumber", "")
            )
          )
        }
      } catch (_: Exception) {}
    }

    val stepsList = mutableListOf<BuildStep>()
    if (stepsJson.isNotEmpty()) {
      try {
        val arr = JSONArray(stepsJson)
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          stepsList.add(
            BuildStep(
              stepNumber = obj.getInt("stepNumber"),
              phaseName = obj.getString("phaseName"),
              instruction = obj.getString("instruction"),
              focusPieces = obj.getString("focusPieces"),
              builderTip = obj.getString("builderTip")
            )
          )
        }
      } catch (_: Exception) {}
    }

    return LegoCreation(
      id = id,
      title = title,
      subtitle = subtitle,
      pieceCount = pieceCount,
      tier = tier,
      category = category,
      lore = lore,
      palette = paletteList,
      techniques = techList,
      parts = partsList,
      steps = stepsList,
      combinationNumber = combinationNumber,
      estimatedBuildMinutes = estimatedBuildMinutes,
      isSaved = true,
      isBuilt = isBuilt,
      personalNotes = personalNotes,
      userRating = userRating
    )
  }
}
