package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class PieceTypeCategory(val displayName: String) {
  BRICKS("Bricks"),
  PLATES("Plates"),
  TILES("Smooth Tiles"),
  SLOPES("Slopes & Wedges"),
  TECHNIC("Technic & Axles"),
  SNOT_SPECIALTY("SNOT & Hinges"),
  DETAILS("Cockpits & Details")
}

data class DigitalPieceDefinition(
  val id: String,
  val name: String,
  val category: PieceTypeCategory,
  val widthStuds: Int, // e.g. 2
  val lengthStuds: Int, // e.g. 4
  val heightPlates: Int, // 1 for plate, 3 for standard brick (3 plates = 1 brick)
  val isSlope: Boolean = false,
  val isTile: Boolean = false,
  val isRound: Boolean = false,
  val designId: String = ""
)

data class PlacedBrick3D(
  val id: String = UUID.randomUUID().toString(),
  val pieceDef: DigitalPieceDefinition,
  val gridX: Int, // 0 to 15 (baseplate stud coord)
  val gridY: Int, // 0 to 15 (baseplate stud coord)
  val layerZ: Int, // height in plates (0, 1, 2, 3, 6, ...)
  val rotationDeg: Int = 0, // 0 or 90
  val color: Color,
  val colorName: String
)

object DigitalPieceCatalog {

  val ALL_PIECES = listOf(
    // Standard Bricks (height = 3 plates)
    DigitalPieceDefinition("brick_2x4", "2x4 Standard Brick", PieceTypeCategory.BRICKS, 2, 4, 3, designId = "3001"),
    DigitalPieceDefinition("brick_2x3", "2x3 Standard Brick", PieceTypeCategory.BRICKS, 2, 3, 3, designId = "3000"),
    DigitalPieceDefinition("brick_2x2", "2x2 Standard Brick", PieceTypeCategory.BRICKS, 2, 2, 3, designId = "3003"),
    DigitalPieceDefinition("brick_1x4", "1x4 Standard Brick", PieceTypeCategory.BRICKS, 1, 4, 3, designId = "3010"),
    DigitalPieceDefinition("brick_1x3", "1x3 Standard Brick", PieceTypeCategory.BRICKS, 1, 3, 3, designId = "3622"),
    DigitalPieceDefinition("brick_1x2", "1x2 Standard Brick", PieceTypeCategory.BRICKS, 1, 2, 3, designId = "3004"),
    DigitalPieceDefinition("brick_1x1", "1x1 Standard Brick", PieceTypeCategory.BRICKS, 1, 1, 3, designId = "3005"),
    DigitalPieceDefinition("brick_1x6", "1x6 Long Beam Brick", PieceTypeCategory.BRICKS, 1, 6, 3, designId = "3009"),
    DigitalPieceDefinition("brick_1x8", "1x8 Foundation Brick", PieceTypeCategory.BRICKS, 1, 8, 3, designId = "3008"),
    DigitalPieceDefinition("brick_round_1x1", "1x1 Round Column", PieceTypeCategory.BRICKS, 1, 1, 3, isRound = true, designId = "3062"),

    // Plates & Flats (height = 1 plate)
    DigitalPieceDefinition("plate_2x4", "2x4 Base Plate", PieceTypeCategory.PLATES, 2, 4, 1, designId = "3020"),
    DigitalPieceDefinition("plate_2x6", "2x6 Structural Plate", PieceTypeCategory.PLATES, 2, 6, 1, designId = "3795"),
    DigitalPieceDefinition("plate_2x2", "2x2 Square Plate", PieceTypeCategory.PLATES, 2, 2, 1, designId = "3022"),
    DigitalPieceDefinition("plate_1x4", "1x4 Strip Plate", PieceTypeCategory.PLATES, 1, 4, 1, designId = "3710"),
    DigitalPieceDefinition("plate_1x2", "1x2 Small Plate", PieceTypeCategory.PLATES, 1, 2, 1, designId = "3023"),
    DigitalPieceDefinition("plate_1x1", "1x1 Micro Plate", PieceTypeCategory.PLATES, 1, 1, 1, designId = "3024"),
    DigitalPieceDefinition("plate_4x4", "4x4 Heavy Sub-Plate", PieceTypeCategory.PLATES, 4, 4, 1, designId = "3031"),

    // Smooth Finish Tiles (no top studs, height = 1 plate)
    DigitalPieceDefinition("tile_2x4", "2x4 Smooth Finish Tile", PieceTypeCategory.TILES, 2, 4, 1, isTile = true, designId = "87079"),
    DigitalPieceDefinition("tile_2x2", "2x2 Smooth Finish Tile", PieceTypeCategory.TILES, 2, 2, 1, isTile = true, designId = "3068"),
    DigitalPieceDefinition("tile_1x4", "1x4 Smooth Strip Tile", PieceTypeCategory.TILES, 1, 4, 1, isTile = true, designId = "2431"),
    DigitalPieceDefinition("tile_1x2", "1x2 Smooth Plate Tile", PieceTypeCategory.TILES, 1, 2, 1, isTile = true, designId = "3069"),
    DigitalPieceDefinition("tile_1x2_grille", "1x2 Grille Radiator Tile", PieceTypeCategory.TILES, 1, 2, 1, isTile = true, designId = "2412"),
    DigitalPieceDefinition("tile_1x1", "1x1 Micro Smooth Tile", PieceTypeCategory.TILES, 1, 1, 1, isTile = true, designId = "3070"),

    // Slopes & Aerodynamic Wedges
    DigitalPieceDefinition("slope_2x2_45", "2x2 45° Roof Slope", PieceTypeCategory.SLOPES, 2, 2, 3, isSlope = true, designId = "3039"),
    DigitalPieceDefinition("slope_1x2_45", "1x2 45° Small Slope", PieceTypeCategory.SLOPES, 1, 2, 3, isSlope = true, designId = "3040"),
    DigitalPieceDefinition("slope_1x2_inv", "1x2 Inverted Slope", PieceTypeCategory.SLOPES, 1, 2, 3, isSlope = true, designId = "3665"),
    DigitalPieceDefinition("slope_1x4_curved", "1x4 Curved Bow Slope", PieceTypeCategory.SLOPES, 1, 4, 2, isSlope = true, designId = "61678"),
    DigitalPieceDefinition("slope_2x3_wing", "2x3 Aerodynamic Wing Wedge", PieceTypeCategory.SLOPES, 2, 3, 1, isSlope = true, designId = "43722"),

    // Technic & Mechanical
    DigitalPieceDefinition("technic_1x2_beam", "Technic 1x2 Beam w/ Axle", PieceTypeCategory.TECHNIC, 1, 2, 3, designId = "32064"),
    DigitalPieceDefinition("technic_1x4_beam", "Technic 1x4 Liftarm", PieceTypeCategory.TECHNIC, 1, 4, 3, designId = "32449"),
    DigitalPieceDefinition("technic_axle_4L", "Technic 4L Axle Bar", PieceTypeCategory.TECHNIC, 1, 4, 1, designId = "3705"),
    DigitalPieceDefinition("technic_pin_friction", "Technic Snap Pin", PieceTypeCategory.TECHNIC, 1, 2, 1, designId = "2780"),
    DigitalPieceDefinition("technic_gear_12t", "Technic 12-Tooth Bevel Gear", PieceTypeCategory.TECHNIC, 2, 2, 2, isRound = true, designId = "6589"),

    // SNOT & Hinges
    DigitalPieceDefinition("snot_1x2_bracket", "1x2 - 2x2 90° SNOT Bracket", PieceTypeCategory.SNOT_SPECIALTY, 1, 2, 2, designId = "44728"),
    DigitalPieceDefinition("snot_1x1_4studs", "1x1 Brick with 4 Side Studs", PieceTypeCategory.SNOT_SPECIALTY, 1, 1, 3, designId = "4733"),
    DigitalPieceDefinition("jumper_1x2", "1x2 Jumper Plate (1 Stud)", PieceTypeCategory.SNOT_SPECIALTY, 1, 2, 1, designId = "3794"),
    DigitalPieceDefinition("jumper_2x2", "2x2 Jumper Plate (1 Stud)", PieceTypeCategory.SNOT_SPECIALTY, 2, 2, 1, designId = "87580"),
    DigitalPieceDefinition("hinge_swivel", "1x2 Swivel Hinge Base", PieceTypeCategory.SNOT_SPECIALTY, 1, 2, 2, designId = "2429"),

    // Details & Cockpit
    DigitalPieceDefinition("cockpit_canopy", "Translucent Cockpit Canopy", PieceTypeCategory.DETAILS, 2, 4, 3, isSlope = true, designId = "3823"),
    DigitalPieceDefinition("antenna_mast", "Micro Sensor Antenna Mast", PieceTypeCategory.DETAILS, 1, 1, 4, designId = "3957"),
    DigitalPieceDefinition("steering_wheel", "Minifig Steering Wheel", PieceTypeCategory.DETAILS, 2, 2, 2, designId = "3829"),
    DigitalPieceDefinition("headlight_1x1", "1x1 Headlight Erling Brick", PieceTypeCategory.DETAILS, 1, 1, 3, designId = "4070")
  )
}
