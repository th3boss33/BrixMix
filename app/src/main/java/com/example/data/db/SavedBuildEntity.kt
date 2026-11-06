package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_builds")
data class SavedBuildEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val subtitle: String,
  val pieceCount: Int,
  val tierName: String,
  val categoryName: String,
  val lore: String,
  val combinationNumber: Int,
  val estimatedBuildMinutes: Int,
  val isBuilt: Boolean = false,
  val personalNotes: String = "",
  val userRating: Int = 0,
  val savedTimestamp: Long = System.currentTimeMillis(),
  val paletteJson: String = "",
  val techniquesJson: String = "",
  val partsJson: String = "",
  val stepsJson: String = "",
  val checkedPartIds: String = "" // comma-separated checked IDs
)
