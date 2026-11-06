package com.example.data

import com.example.model.BuildCategory
import com.example.model.BuildStep
import com.example.model.ComplexityTier
import com.example.model.LegoCreation
import com.example.model.LegoPaletteColor
import com.example.model.LegoPart
import com.example.model.PartCategory
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

object CreationGenerator {

  // Iconic Lego Colors
  val COLOR_RED = LegoPaletteColor("Bright Red", "#E52521")
  val COLOR_YELLOW = LegoPaletteColor("Warm Yellow", "#FFCE00")
  val COLOR_BLUE = LegoPaletteColor("Cobalt Blue", "#006CB7")
  val COLOR_BLACK = LegoPaletteColor("Classic Black", "#1B1D20")
  val COLOR_WHITE = LegoPaletteColor("Pure White", "#F4F6F8")
  val COLOR_DARK_GRAY = LegoPaletteColor("Dark Bluish Gray", "#4C525C")
  val COLOR_LIGHT_GRAY = LegoPaletteColor("Light Bluish Gray", "#9EA3AA")
  val COLOR_ORANGE = LegoPaletteColor("Safety Orange", "#FA6800")
  val COLOR_LIME = LegoPaletteColor("Lime Green", "#7CB342")
  val COLOR_SAND_GREEN = LegoPaletteColor("Sand Green", "#708E7C")
  val COLOR_TRANS_ORANGE = LegoPaletteColor("Trans-Neon Orange", "#FF7043")
  val COLOR_MEDIUM_AZURE = LegoPaletteColor("Medium Azure", "#29B6F6")
  val COLOR_DARK_TAN = LegoPaletteColor("Dark Tan", "#8D6E63")

  private val ALL_COLORS = listOf(
    COLOR_RED, COLOR_YELLOW, COLOR_BLUE, COLOR_BLACK, COLOR_WHITE,
    COLOR_DARK_GRAY, COLOR_LIGHT_GRAY, COLOR_ORANGE, COLOR_LIME,
    COLOR_SAND_GREEN, COLOR_TRANS_ORANGE, COLOR_MEDIUM_AZURE, COLOR_DARK_TAN
  )

  private val MASTER_TECHNIQUES = listOf(
    "SNOT (Studs Not On Top) - 90-degree sideways bracket geometry",
    "Plate Sandwiching for high-tensile structural rigidity",
    "Ball-and-Socket articulation for dynamic poses",
    "Offset pinning with 1/2 stud jumper plates",
    "Inverse slope tapering for aerodynamic bevels",
    "Technic gear integration with smooth ratio drive",
    "Greebling with micro-tiles, grilles, and levers",
    "Dual-hinge canopy locking mechanism",
    "Half-plate staircasing for organic creature contours"
  )

  /**
   * Generates a LegoCreation based on piece budget and category filter.
   */
  fun generate(
    targetPieces: Int? = null,
    categoryFilter: BuildCategory = BuildCategory.ALL,
    combinationSeed: Int? = null
  ): LegoCreation {
    val seed = combinationSeed ?: Random.nextInt(1, 1101)
    val random = Random(seed.toLong())

    // Determine piece count (10 to 1100)
    val pieceCount = targetPieces ?: when {
      seed <= 180 -> 10 + ((seed.toFloat() / 180f) * 65).roundToInt()
      seed <= 500 -> 76 + (((seed - 180).toFloat() / 320f) * 174).roundToInt()
      seed <= 850 -> 251 + (((seed - 500).toFloat() / 350f) * 349).roundToInt()
      else -> 601 + (((seed - 850).toFloat() / 250f) * 499).roundToInt()
    }.coerceIn(10, 1100)

    val tier = when {
      pieceCount <= 75 -> ComplexityTier.SIMPLE
      pieceCount <= 250 -> ComplexityTier.MODERATE
      pieceCount <= 600 -> ComplexityTier.COMPLEX
      else -> ComplexityTier.EXTREME
    }

    val selectedCategory = if (categoryFilter != BuildCategory.ALL) {
      categoryFilter
    } else {
      val cats = BuildCategory.entries.filter { it != BuildCategory.ALL }
      cats[seed % cats.size]
    }

    // Check if we match a flagship curated build
    val curated = getFlagshipBuild(seed, tier, selectedCategory)
    if (curated != null) {
      return curated.copy(combinationNumber = seed)
    }

    // Procedural generation tailored to the tier & piece count
    return generateProceduralCreation(seed, pieceCount, tier, selectedCategory, random)
  }

  /**
   * Generates a procedural build adhering to real-world Lego constraints.
   */
  private fun generateProceduralCreation(
    seed: Int,
    pieces: Int,
    tier: ComplexityTier,
    category: BuildCategory,
    random: Random
  ): LegoCreation {
    val prefixes = listOf("Neo", "Apex", "Vanguard", "Chrono", "Hyper", "Titan", "Quantum", "Solar", "Iron", "Obsidian")
    val nouns = when (category) {
      BuildCategory.SPACE_MECH -> listOf("Starstrider", "Void Walker", "Eclipse Skiff", "Orbital Interceptor", "Starlight Frigate", "Nebula Dreadnought")
      BuildCategory.VEHICLES -> listOf("Sandstorm Buggy", "Turbine Interceptor", "Apex Hotrod", "Sub-Zero Snowcat", "Cargo Hauler", "Hover Cruiser")
      BuildCategory.MEDIEVAL_FANTASY -> listOf("Dragon Sanctum", "Rune Gatehouse", "Highland Bastion", "Dwarven Anvil Tower", "Siege Ballista", "Shadow Fortress")
      BuildCategory.CREATURES -> listOf("Bioluminescent Wyrm", "Cyber Mantis", "Glacier Wolf", "Solar Phoenix", "Deep Abyss Kraken", "Iron Scorpion")
      BuildCategory.ARCHITECTURE -> listOf("Monolith Spire", "Sky Garden Modular", "Neon Metro Tower", "Brutalist Citadel", "Hanging Pavilion", "Cyber Shinto Shrine")
      BuildCategory.QUIRKY_GADGETS -> listOf("Retro Cassette Rig", "Pocket Pinball Desk", "Clockwork Vault", "Mini Jukebox", "Mechanical Periscope", "Micro Synth Station")
      else -> listOf("Creation Unit", "Constructor Model", "Master Prototype")
    }

    val title = "${prefixes[random.nextInt(prefixes.size)]} ${nouns[random.nextInt(nouns.size)]} #$seed"
    val subtitle = "${tier.label} • ${category.displayName} • $pieces Elements"

    val lore = "A ${tier.name.lowercase()} custom model engineered for maximum visual impact within an exact budget of $pieces bricks. Optimized with internal modular cross-bracing and sleek exterior finishing tiles."

    // Select color palette (2 to 4 colors)
    val shuffledColors = ALL_COLORS.shuffled(random)
    val paletteSize = when (tier) {
      ComplexityTier.SIMPLE -> 2
      ComplexityTier.MODERATE -> 3
      ComplexityTier.COMPLEX -> 3
      ComplexityTier.EXTREME -> 4
    }
    val palette = shuffledColors.take(paletteSize)

    // Generate balanced parts list matching piece count
    val parts = generatePartsList(pieces, palette, tier, random)

    // Generate step-by-step instructions
    val steps = generateBuildSteps(pieces, tier, category, palette)

    // Select key techniques
    val techniqueCount = when (tier) {
      ComplexityTier.SIMPLE -> 1
      ComplexityTier.MODERATE -> 2
      ComplexityTier.COMPLEX -> 3
      ComplexityTier.EXTREME -> 4
    }
    val techniques = MASTER_TECHNIQUES.shuffled(random).take(techniqueCount)

    val estimatedMinutes = (pieces * 0.15 + when (tier) {
      ComplexityTier.SIMPLE -> 8
      ComplexityTier.MODERATE -> 20
      ComplexityTier.COMPLEX -> 45
      ComplexityTier.EXTREME -> 90
    }).roundToInt()

    return LegoCreation(
      id = "lego-$seed-${UUID.randomUUID().toString().take(6)}",
      title = title,
      subtitle = subtitle,
      pieceCount = pieces,
      tier = tier,
      category = category,
      lore = lore,
      palette = palette,
      techniques = techniques,
      parts = parts,
      steps = steps,
      combinationNumber = seed,
      estimatedBuildMinutes = estimatedMinutes
    )
  }

  /**
   * Produces a categorized parts list that adds up exactly to the total pieces.
   */
  private fun generatePartsList(
    totalPieces: Int,
    palette: List<LegoPaletteColor>,
    tier: ComplexityTier,
    random: Random
  ): List<LegoPart> {
    val parts = mutableListOf<LegoPart>()
    var remaining = totalPieces

    val brickTemplates = listOf(
      Triple("2x4 Standard Brick", PartCategory.BRICKS, "3001"),
      Triple("2x2 Standard Brick", PartCategory.BRICKS, "3003"),
      Triple("1x4 Standard Brick", PartCategory.BRICKS, "3010"),
      Triple("1x2 Standard Brick", PartCategory.BRICKS, "3004"),
      Triple("1x1 Round Brick", PartCategory.BRICKS, "3062"),

      Triple("2x6 Structural Plate", PartCategory.PLATES_TILES, "3795"),
      Triple("2x4 Base Plate", PartCategory.PLATES_TILES, "3020"),
      Triple("1x2 Smooth Tile", PartCategory.PLATES_TILES, "3069"),
      Triple("2x2 Jumper Plate (1 Stud)", PartCategory.PLATES_TILES, "87580"),
      Triple("1x1 Translucent Round Plate", PartCategory.PLATES_TILES, "4073"),

      Triple("2x2 45° Roof Slope", PartCategory.SLOPES_WEDGES, "3039"),
      Triple("1x2 Inverted Slope", PartCategory.SLOPES_WEDGES, "3665"),
      Triple("1x4 Curved Aerodynamic Slope", PartCategory.SLOPES_WEDGES, "61678"),
      Triple("2x3 Left & Right Wing Wedge", PartCategory.SLOPES_WEDGES, "43722"),

      Triple("Technic 1x2 Beam with Axle Hole", PartCategory.TECHNIC_PINS, "32064"),
      Triple("Technic Friction Pin (Black/Blue)", PartCategory.TECHNIC_PINS, "2780"),
      Triple("Technic 4L Axle Rod", PartCategory.TECHNIC_PINS, "3705"),
      Triple("Technic 12-Tooth Bevel Gear", PartCategory.TECHNIC_PINS, "6589"),

      Triple("1x2 - 2x2 90° SNOT Bracket", PartCategory.SPECIALTY_SNOT, "44728"),
      Triple("1x1 Brick with 4 Side Studs", PartCategory.SPECIALTY_SNOT, "4733"),
      Triple("Hinge Plate 1x2 Swivel Base", PartCategory.SPECIALTY_SNOT, "2429"),
      Triple("Small Ball-and-Socket Joint", PartCategory.SPECIALTY_SNOT, "14704")
    )

    var partIndex = 1
    val numLineItems = when (tier) {
      ComplexityTier.SIMPLE -> random.nextInt(4, 7)
      ComplexityTier.MODERATE -> random.nextInt(7, 12)
      ComplexityTier.COMPLEX -> random.nextInt(12, 18)
      ComplexityTier.EXTREME -> random.nextInt(18, 25)
    }

    val selectedTemplates = brickTemplates.shuffled(random).take(numLineItems)

    for (i in selectedTemplates.indices) {
      if (remaining <= 0) break
      val template = selectedTemplates[i]
      val color = palette[i % palette.size]

      val isLast = i == selectedTemplates.lastIndex
      val qty = if (isLast) {
        remaining
      } else {
        val maxPortion = (remaining / (selectedTemplates.size - i)).coerceAtLeast(1)
        val candidate = random.nextInt(1, (maxPortion * 1.8).roundToInt().coerceAtLeast(2))
        candidate.coerceAtMost(remaining - (selectedTemplates.size - i - 1))
      }

      if (qty > 0) {
        parts.add(
          LegoPart(
            id = "part-$partIndex",
            name = template.first,
            category = template.second,
            quantity = qty,
            colorName = color.name,
            colorHex = color.hex,
            elementNumber = template.third
          )
        )
        remaining -= qty
        partIndex++
      }
    }

    // Sort by category for optimal builder workflow
    return parts.sortedBy { it.category.ordinal }
  }

  /**
   * Generates step-by-step assembly phases with pro builder tips.
   */
  private fun generateBuildSteps(
    pieces: Int,
    tier: ComplexityTier,
    category: BuildCategory,
    palette: List<LegoPaletteColor>
  ): List<BuildStep> {
    val primaryColor = palette.firstOrNull()?.name ?: "Primary Color"
    val accentColor = palette.getOrNull(1)?.name ?: "Accent Color"

    return listOf(
      BuildStep(
        stepNumber = 1,
        phaseName = "Foundation & Base Chassis",
        instruction = "Gather baseplates and bottom reinforcement bricks. Interlock longer plates in cross-hatch layout to establish a solid footprint.",
        focusPieces = "Baseplates, Technic beams, and 2x4 anchor bricks in $primaryColor",
        builderTip = "Stagger brick seams so joints do not align vertically; this prevents structural flexing during handling."
      ),
      BuildStep(
        stepNumber = 2,
        phaseName = "Internal Core & SNOT Framing",
        instruction = "Erect the central load-bearing spine. Integrate side-facing bracket bricks (SNOT) to allow exterior paneling to mount horizontally.",
        focusPieces = "1x2-2x2 brackets, technic axle pins, and interior core bricks",
        builderTip = "Double-check ball joint friction before enclosing the cockpit or torso chamber."
      ),
      BuildStep(
        stepNumber = 3,
        phaseName = "Structural Shell & Mechanical Articulation",
        instruction = "Install angled slopes, side wing assemblies, and hinge elements. Align gear mesh smoothly without binding.",
        focusPieces = "Slopes, inverse wedges, and articulation joints in $primaryColor and $accentColor",
        builderTip = "Leave 1/2 plate tolerance around rotating hinges to prevent clutch friction wear."
      ),
      BuildStep(
        stepNumber = 4,
        phaseName = "Exterior Armor & Tile Paneling",
        instruction = "Cover exposed top studs with smooth finish tiles. Apply curved wedges and transparent cockpit/visor elements.",
        focusPieces = "1x2/2x4 smooth tiles, curved wedges, and translucent canopy parts",
        builderTip = "Use jumper plates to center sub-assemblies across an odd number of studs for symmetrical balance."
      ),
      BuildStep(
        stepNumber = 5,
        phaseName = "Greebling, Antennae & Final Polish",
        instruction = "Add high-density micro details: grilles, exhaust vents, antennae rods, and cockpit display levers to bring the model to life.",
        focusPieces = "1x1 round tiles, lever antennas, clips, and specialized micro-elements",
        builderTip = "Cluster fine greebling near mechanical joints while keeping primary armor plates clean for visual contrast."
      )
    )
  }

  /**
   * Curated flagship builds showcasing the extreme spectrum.
   */
  private fun getFlagshipBuild(seed: Int, tier: ComplexityTier, category: BuildCategory): LegoCreation? {
    return when (seed) {
      42 -> LegoCreation(
        id = "flagship-42",
        title = "Chrono-Speedster Mini 42",
        subtitle = "Simple Micro • Space & Mechs • 42 Elements",
        pieceCount = 42,
        tier = ComplexityTier.SIMPLE,
        category = BuildCategory.SPACE_MECH,
        lore = "A beloved ultra-compact orbital racer built using minimal pieces with maximum aerodynamic presence.",
        palette = listOf(COLOR_YELLOW, COLOR_BLACK, COLOR_TRANS_ORANGE),
        techniques = listOf("SNOT horizontal engine exhaust", "Jumper plate centered nose-cone"),
        parts = listOf(
          LegoPart("p1", "2x4 Base Plate", PartCategory.PLATES_TILES, 2, COLOR_BLACK.name, COLOR_BLACK.hex, "3020"),
          LegoPart("p2", "1x2 Smooth Tile", PartCategory.PLATES_TILES, 6, COLOR_YELLOW.name, COLOR_YELLOW.hex, "3069"),
          LegoPart("p3", "2x2 45° Roof Slope", PartCategory.SLOPES_WEDGES, 4, COLOR_YELLOW.name, COLOR_YELLOW.hex, "3039"),
          LegoPart("p4", "1x2 Inverted Slope", PartCategory.SLOPES_WEDGES, 4, COLOR_BLACK.name, COLOR_BLACK.hex, "3665"),
          LegoPart("p5", "1x1 Trans-Orange Round Plate", PartCategory.PLATES_TILES, 6, COLOR_TRANS_ORANGE.name, COLOR_TRANS_ORANGE.hex, "4073"),
          LegoPart("p6", "1x2 - 1x2 Bracket", PartCategory.SPECIALTY_SNOT, 4, COLOR_BLACK.name, COLOR_BLACK.hex, "99781"),
          LegoPart("p7", "1x4 Aerodynamic Curved Slope", PartCategory.SLOPES_WEDGES, 4, COLOR_YELLOW.name, COLOR_YELLOW.hex, "61678"),
          LegoPart("p8", "Technic Pin with Friction", PartCategory.TECHNIC_PINS, 4, COLOR_BLACK.name, COLOR_BLACK.hex, "2780"),
          LegoPart("p9", "1x1 Micro Antenna Rod", PartCategory.SPECIALTY_SNOT, 8, COLOR_BLACK.name, COLOR_BLACK.hex, "3957")
        ),
        steps = generateBuildSteps(42, ComplexityTier.SIMPLE, BuildCategory.SPACE_MECH, listOf(COLOR_YELLOW, COLOR_BLACK, COLOR_TRANS_ORANGE)),
        combinationNumber = 42,
        estimatedBuildMinutes = 12
      )
      1100 -> LegoCreation(
        id = "flagship-1100",
        title = "Chrono-Leviathan Prime 1100",
        subtitle = "Extreme Titan • Space & Mechs • 1,100 Elements Max",
        pieceCount = 1100,
        tier = ComplexityTier.EXTREME,
        category = BuildCategory.SPACE_MECH,
        lore = "The absolute apex build of the 1,100 combination matrix. A gargantuan star-faring capital vessel boasting reinforced Technic load spines, four articulated plasma thruster banks, multi-level flight decks, and over 1,100 interconnected elements.",
        palette = listOf(COLOR_DARK_GRAY, COLOR_RED, COLOR_LIGHT_GRAY, COLOR_TRANS_ORANGE),
        techniques = listOf(
          "Multi-tier Technic spine cross-triangulation for rigid 1100-piece support",
          "Quadrant SNOT shell plating with zero visible studs",
          "Articulated hydraulic piston gantry with Technic ball sockets",
          "Micro-diorama interior bridge deck sealed under cantilevered canopy"
        ),
        parts = listOf(
          LegoPart("p1", "2x4 Standard Brick", PartCategory.BRICKS, 180, COLOR_DARK_GRAY.name, COLOR_DARK_GRAY.hex, "3001"),
          LegoPart("p2", "1x4 Structural Wall Brick", PartCategory.BRICKS, 140, COLOR_DARK_GRAY.name, COLOR_DARK_GRAY.hex, "3010"),
          LegoPart("p3", "2x6 Heavy Base Plate", PartCategory.PLATES_TILES, 160, COLOR_LIGHT_GRAY.name, COLOR_LIGHT_GRAY.hex, "3795"),
          LegoPart("p4", "2x4 Smooth Finishing Tile", PartCategory.PLATES_TILES, 150, COLOR_RED.name, COLOR_RED.hex, "87079"),
          LegoPart("p5", "1x2 Smooth Grille Tile", PartCategory.PLATES_TILES, 80, COLOR_LIGHT_GRAY.name, COLOR_LIGHT_GRAY.hex, "2412"),
          LegoPart("p6", "4x4 Aerodynamic Wing Wedge", PartCategory.SLOPES_WEDGES, 70, COLOR_RED.name, COLOR_RED.hex, "3935"),
          LegoPart("p7", "1x4 Curved Inverted Slope", PartCategory.SLOPES_WEDGES, 90, COLOR_DARK_GRAY.name, COLOR_DARK_GRAY.hex, "13547"),
          LegoPart("p8", "Technic 11M Liftarm Beam", PartCategory.TECHNIC_PINS, 60, COLOR_LIGHT_GRAY.name, COLOR_LIGHT_GRAY.hex, "32525"),
          LegoPart("p9", "Technic Friction Pin & Bushings", PartCategory.TECHNIC_PINS, 80, COLOR_DARK_GRAY.name, COLOR_DARK_GRAY.hex, "2780"),
          LegoPart("p10", "1x2-2x2 SNOT Heavy Angle Bracket", PartCategory.SPECIALTY_SNOT, 50, COLOR_DARK_GRAY.name, COLOR_DARK_GRAY.hex, "44728"),
          LegoPart("p11", "Trans-Orange Thruster Core Dishes", PartCategory.SPECIALTY_SNOT, 40, COLOR_TRANS_ORANGE.name, COLOR_TRANS_ORANGE.hex, "3960")
        ),
        steps = listOf(
          BuildStep(
            1,
            "Titan Spine & Keel Cross-Ribs",
            "Construct the triple-thick Technic backbone. Use 11M liftarms and double-pin connectors to form an unbending keel that bears the weight of all 1,100 pieces.",
            "Technic 11M beams, heavy friction pins, 2x6 base plates",
            "Ensure pin connections are firmly snapped together with zero rattle before framing the engines."
          ),
          BuildStep(
            2,
            "Reactor Chamber & Vector Thrusters",
            "Assemble the quad-thruster engine assembly at the stern. Connect translucent orange engine dishes surrounded by heat-shielding slopes.",
            "Trans-orange dishes, curved 1x4 inverted slopes, axle linkages",
            "Lock the thruster pivots with friction pins so they hold their angle when displayed."
          ),
          BuildStep(
            3,
            "Internal Command Bridge & Modular Hangars",
            "Build the interior command level with micro-scale displays, crew stations, and port/starboard launch decks before sealing the lower hull.",
            "1x2 smooth tiles, grille elements, transparent consoles",
            "Add micro details now because reaching into the lower decks later is impossible once armor is mounted."
          ),
          BuildStep(
            4,
            "Heavy SNOT Citadel Armor & Wing Pylons",
            "Mount the primary port and starboard wedge wings using side-bracket arrays. Seamlessly contour the hull so no internal Technic skeleton shows.",
            "Red wing wedges, 2x4 smooth finishing tiles, 1x2-2x2 brackets",
            "Align long rows of tiles by running the edge of a flat ruler or Lego brick separator down the seams."
          ),
          BuildStep(
            5,
            "Greebling Matrix & Final Command Spire",
            "Crown the 1,100-piece flagship with the sensory spire, communications array, and high-density surface greebling along the central trench.",
            "Micro antennae, grille tiles, clip levers, defense turrets",
            "Congratulations on conquering the peak of the 1,100 combination matrix! Inspect all clutch points for maximum durability."
          )
        ),
        combinationNumber = 1100,
        estimatedBuildMinutes = 180
      )
      else -> null
    }
  }

  /**
   * Generates a catalog summary item for the 1,100 Combination Matrix view.
   */
  fun getMatrixItem(index: Int): MatrixCombinationItem {
    val seed = index.coerceIn(1, 1100)
    val random = Random(seed.toLong())

    val pieceCount = when {
      seed <= 180 -> 10 + ((seed.toFloat() / 180f) * 65).roundToInt()
      seed <= 500 -> 76 + (((seed - 180).toFloat() / 320f) * 174).roundToInt()
      seed <= 850 -> 251 + (((seed - 500).toFloat() / 350f) * 349).roundToInt()
      else -> 601 + (((seed - 850).toFloat() / 250f) * 499).roundToInt()
    }.coerceIn(10, 1100)

    val tier = when {
      pieceCount <= 75 -> ComplexityTier.SIMPLE
      pieceCount <= 250 -> ComplexityTier.MODERATE
      pieceCount <= 600 -> ComplexityTier.COMPLEX
      else -> ComplexityTier.EXTREME
    }

    val cats = BuildCategory.entries.filter { it != BuildCategory.ALL }
    val category = cats[seed % cats.size]

    val archetypeNames = when (category) {
      BuildCategory.SPACE_MECH -> listOf("Micro Fighter", "Scout Walker", "Orbital Interceptor", "Assault Mecha", "Void Dreadnought", "Titan Carrier")
      BuildCategory.VEHICLES -> listOf("Pocket Kart", "Rally Cruiser", "Turbine Roadster", "Cargo Transporter", "All-Terrain Crawler", "Heavy Rig")
      BuildCategory.MEDIEVAL_FANTASY -> listOf("Watch Sentry", "Archer Turret", "Dwarven Gate", "Knight Citadel", "Dragon Sanctum", "Imperial Fortress")
      BuildCategory.CREATURES -> listOf("Mini Chameleon", "Desktop Bug", "Cyber Raptor", "Mechanical Dragon", "Sea Leviathan", "Colossal Behemoth")
      BuildCategory.ARCHITECTURE -> listOf("Micro Spire", "Corner Pavilion", "Gothic Tower", "Modular Skyline", "Sky Bridge Complex", "Mega Citadel")
      BuildCategory.QUIRKY_GADGETS -> listOf("Mini Dice Roller", "Retro Boombox", "Puzzle Lockbox", "Working Safe", "Arcade Console", "Grand Clockwork")
      else -> listOf("Concept Model")
    }

    val archetypeName = archetypeNames[random.nextInt(archetypeNames.size)]

    return MatrixCombinationItem(
      index = seed,
      title = "$archetypeName #$seed",
      pieceCount = pieceCount,
      tier = tier,
      category = category
    )
  }
}

data class MatrixCombinationItem(
  val index: Int,
  val title: String,
  val pieceCount: Int,
  val tier: ComplexityTier,
  val category: BuildCategory
)
