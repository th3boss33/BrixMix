package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LegoBlue
import com.example.ui.theme.LegoGreen
import com.example.ui.theme.LegoOrange
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TierComplexColor
import com.example.ui.theme.TierExtremeColor
import com.example.ui.theme.TierModerateColor
import com.example.ui.theme.TierSimpleColor

enum class ComplexityTier(
  val label: String,
  val minPieces: Int,
  val maxPieces: Int,
  val badgeColor: Color,
  val description: String
) {
  SIMPLE("Simple Micro", 10, 75, TierSimpleColor, "Quick, elegant micro-scale builds with clever small part usage."),
  MODERATE("Classic Builder", 76, 250, TierModerateColor, "Balanced models with moving parts, cockpits, and solid geometry."),
  COMPLEX("Master Builder", 251, 600, TierComplexColor, "Advanced modular builds featuring SNOT, intricate detail, and multi-tier sub-assemblies."),
  EXTREME("Extreme Titan", 601, 1100, TierExtremeColor, "Colossal masterworks with up to 1100 pieces, reinforced skeletons, and epic scale.")
}

enum class BuildCategory(
  val displayName: String,
  val iconName: String,
  val subThemes: List<String>
) {
  ALL("All Categories", "AutoAwesome", emptyList()),
  SPACE_MECH("Space & Mechs", "RocketLaunch", listOf("Neo-Orbital Fighter", "Heavy Titan Walker", "Deep Space Probe", "Alien Mining Rig", "Modular Space Station")),
  VEHICLES("Vehicles & Speed", "DirectionsCar", listOf("Desert Cyber Buggy", "Speedboat Racer", "Steam Locomotive", "Retro Hotrod", "Heavy Construction Hauler")),
  MEDIEVAL_FANTASY("Medieval & Fantasy", "Shield", listOf("Highland Watchtower", "Dwarven Forge Gate", "Dragon Wyrm Sanctum", "Medieval Windmill", "Knight Siege Catapult")),
  CREATURES("Creatures & Beasts", "Pets", listOf("Mechanical Velociraptor", "Bioluminescent Chameleon", "Deep Trench Anglerfish", "Cyber Phoenix", "Giant Stag Beetle")),
  ARCHITECTURE("Architecture & Cities", "Apartment", listOf("Neo-Tokyo Noodle Tower", "Gothic Clock Cathedral", "Minimalist Glass Villa", "Modular Corner Cafe", "Cyberpunk Sky Bridge")),
  QUIRKY_GADGETS("Quirky & Novelty", "Lightbulb", listOf("Mini Arcade Cabinet", "Mechanical Safe Box", "Retro Boombox", "Gumball Dispenser", "Micro Vinyl Record Player"))
}

enum class PartCategory(val displayName: String) {
  BRICKS("Standard Bricks"),
  PLATES_TILES("Plates & Smooth Tiles"),
  SLOPES_WEDGES("Slopes & Aerodynamic Wedges"),
  TECHNIC_PINS("Technic Pins, Axles & Gears"),
  SPECIALTY_SNOT("Specialty, Hinges & SNOT Brackets")
}

data class LegoPart(
  val id: String,
  val name: String,
  val category: PartCategory,
  val quantity: Int,
  val colorName: String,
  val colorHex: String,
  val elementNumber: String = ""
)

data class BuildStep(
  val stepNumber: Int,
  val phaseName: String,
  val instruction: String,
  val focusPieces: String,
  val builderTip: String
)

data class LegoPaletteColor(
  val name: String,
  val hex: String
)

data class LegoCreation(
  val id: String,
  val title: String,
  val subtitle: String,
  val pieceCount: Int,
  val tier: ComplexityTier,
  val category: BuildCategory,
  val lore: String,
  val palette: List<LegoPaletteColor>,
  val techniques: List<String>,
  val parts: List<LegoPart>,
  val steps: List<BuildStep>,
  val combinationNumber: Int,
  val estimatedBuildMinutes: Int,
  val isSaved: Boolean = false,
  val isBuilt: Boolean = false,
  val personalNotes: String = "",
  val userRating: Int = 0
)

data class MatrixSummary(
  val totalCombinations: Int = 1100,
  val simpleCount: Int = 180,
  val moderateCount: Int = 320,
  val complexCount: Int = 350,
  val extremeCount: Int = 250
)
