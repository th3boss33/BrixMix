package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DigitalPieceCatalog
import com.example.model.DigitalPieceDefinition
import com.example.model.PieceTypeCategory
import com.example.model.PlacedBrick3D
import com.example.ui.BuildViewModel
import com.example.ui.components.LegoBadge
import com.example.ui.components.LegoStudIcon
import com.example.ui.components.StudBackground
import com.example.ui.theme.LegoBlue
import com.example.ui.theme.LegoGreen
import com.example.ui.theme.LegoOrange
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopCardBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import kotlin.math.cos
import kotlin.math.sin

enum class StudioToolMode(val label: String) {
  PLACE("Place Brick"),
  ERASE("Brick Separator"),
  PAINT("Paint Color")
}

@Composable
fun Digital3DStudioScreen(
  viewModel: BuildViewModel,
  modifier: Modifier = Modifier
) {
  val currentCreation by viewModel.currentCreation.collectAsState()

  // 3D Camera Angles
  var azimuthAngle by remember { mutableFloatStateOf(45f) } // 0 to 360 deg
  var elevationAngle by remember { mutableFloatStateOf(35f) } // 20 to 75 deg
  var zoomScale by remember { mutableFloatStateOf(1.0f) }

  // Tool & Piece Selections
  var toolMode by remember { mutableStateOf(StudioToolMode.PLACE) }
  var selectedCategory by remember { mutableStateOf(PieceTypeCategory.BRICKS) }
  var selectedPieceDef by remember { mutableStateOf(DigitalPieceCatalog.ALL_PIECES.first()) }
  var selectedRotation by remember { mutableIntStateOf(0) } // 0 or 90

  // Standard Lego Colors Palette
  val legoPalette = remember {
    listOf(
      Pair("Bright Red", Color(0xFFE52521)),
      Pair("Warm Yellow", Color(0xFFFFCE00)),
      Pair("Cobalt Blue", Color(0xFF006CB7)),
      Pair("Classic Black", Color(0xFF1B1D20)),
      Pair("Pure White", Color(0xFFF4F6F8)),
      Pair("Dark Gray", Color(0xFF4C525C)),
      Pair("Safety Orange", Color(0xFFFA6800)),
      Pair("Lime Green", Color(0xFF7CB342)),
      Pair("Medium Azure", Color(0xFF29B6F6)),
      Pair("Trans-Orange", Color(0xFFFF7043))
    )
  }
  var activeColor by remember { mutableStateOf(legoPalette.first()) }

  // Placed Bricks in 3D Space (with initial starter structure)
  val placedBricks = remember {
    mutableStateListOf<PlacedBrick3D>().apply {
      // Starter 4-layer sample build on the baseplate
      val b2x4 = DigitalPieceCatalog.ALL_PIECES.first { it.id == "brick_2x4" }
      val b2x2 = DigitalPieceCatalog.ALL_PIECES.first { it.id == "brick_2x2" }
      val s2x2 = DigitalPieceCatalog.ALL_PIECES.first { it.id == "slope_2x2_45" }

      add(PlacedBrick3D(pieceDef = b2x4, gridX = 6, gridY = 6, layerZ = 0, color = Color(0xFFE52521), colorName = "Bright Red"))
      add(PlacedBrick3D(pieceDef = b2x4, gridX = 6, gridY = 8, layerZ = 0, color = Color(0xFF1B1D20), colorName = "Classic Black"))
      add(PlacedBrick3D(pieceDef = b2x2, gridX = 7, gridY = 7, layerZ = 3, color = Color(0xFFFFCE00), colorName = "Warm Yellow"))
      add(PlacedBrick3D(pieceDef = s2x2, gridX = 7, gridY = 7, layerZ = 6, color = Color(0xFF006CB7), colorName = "Cobalt Blue"))
    }
  }

  // Target stud coordinate on the 16x16 baseplate for placement
  var targetGridX by remember { mutableIntStateOf(7) }
  var targetGridY by remember { mutableIntStateOf(7) }

  StudBackground(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

      // Top Control Bar
      Surface(
        color = WorkshopSurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ViewInAr,
              contentDescription = null,
              tint = LegoYellow,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "3D DIGITAL BRICK STUDIO",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "${placedBricks.size} / 1100 Pieces Placed • 16x16 Studs",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Reset Camera
            IconButton(
              onClick = {
                azimuthAngle = 45f
                elevationAngle = 35f
                zoomScale = 1.0f
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "Reset View",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }

            // Undo / Remove Last
            IconButton(
              onClick = {
                if (placedBricks.isNotEmpty()) {
                  placedBricks.removeAt(placedBricks.lastIndex)
                }
              },
              enabled = placedBricks.isNotEmpty(),
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo",
                tint = if (placedBricks.isNotEmpty()) LegoYellow else TextMuted,
                modifier = Modifier.size(18.dp)
              )
            }

            // Clear All
            IconButton(
              onClick = { placedBricks.clear() },
              enabled = placedBricks.isNotEmpty(),
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Clear All",
                tint = if (placedBricks.isNotEmpty()) LegoRed else TextMuted,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // 3D Interactive Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .background(Color(0xFF0E1118))
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              // Drag horizontally to orbit 360 deg
              azimuthAngle = (azimuthAngle + dragAmount.x * 0.5f) % 360f
              // Drag vertically to tilt elevation
              elevationAngle = (elevationAngle - dragAmount.y * 0.3f).coerceIn(20f, 75f)
            }
          }
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasW = size.width
          val canvasH = size.height
          val originX = canvasW / 2f
          val originY = canvasH * 0.55f

          val radAzimuth = Math.toRadians(azimuthAngle.toDouble())
          val radElevation = Math.toRadians(elevationAngle.toDouble())

          val cosAz = cos(radAzimuth).toFloat()
          val sinAz = sin(radAzimuth).toFloat()
          val sinEl = sin(radElevation).toFloat()

          // Stud spacing scale
          val studScale = 14.dp.toPx() * zoomScale
          val plateHeightScale = 5.dp.toPx() * zoomScale

          // 3D to 2D projection function
          fun project(x: Float, y: Float, zPlates: Float): Offset {
            val centeredX = x - 7.5f
            val centeredY = y - 7.5f

            // Rotate by azimuth
            val rotX = centeredX * cosAz - centeredY * sinAz
            val rotY = centeredX * sinAz + centeredY * cosAz

            // Isometric tilt by elevation
            val screenX = originX + rotX * studScale
            val screenY = originY + rotY * studScale * sinEl - (zPlates * plateHeightScale)
            return Offset(screenX, screenY)
          }

          // 1. Draw 16x16 Baseplate Base
          val bpCorners = listOf(
            project(-0.5f, -0.5f, 0f),
            project(15.5f, -0.5f, 0f),
            project(15.5f, 15.5f, 0f),
            project(-0.5f, 15.5f, 0f)
          )

          val bpPath = Path().apply {
            moveTo(bpCorners[0].x, bpCorners[0].y)
            for (i in 1 until bpCorners.size) {
              lineTo(bpCorners[i].x, bpCorners[i].y)
            }
            close()
          }
          drawPath(bpPath, Color(0xFF1E2430))

          // Baseplate edge thickness
          val bpBottomCorners = listOf(
            project(-0.5f, 15.5f, -1.5f),
            project(15.5f, 15.5f, -1.5f),
            project(15.5f, -0.5f, -1.5f)
          )
          val bpEdgePath = Path().apply {
            moveTo(bpCorners[3].x, bpCorners[3].y)
            lineTo(bpCorners[2].x, bpCorners[2].y)
            lineTo(bpCorners[1].x, bpCorners[1].y)
            lineTo(bpBottomCorners[2].x, bpBottomCorners[2].y)
            lineTo(bpBottomCorners[1].x, bpBottomCorners[1].y)
            lineTo(bpBottomCorners[0].x, bpBottomCorners[0].y)
            close()
          }
          drawPath(bpEdgePath, Color(0xFF141820))

          // 2. Draw Stud Grid on Baseplate
          for (gx in 0..15 step 2) {
            for (gy in 0..15 step 2) {
              val p = project(gx.toFloat(), gy.toFloat(), 0f)
              val isTarget = (gx == targetGridX && gy == targetGridY)

              drawCircle(
                color = if (isTarget) LegoYellow else Color(0xFF2B3344),
                radius = 3.5.dp.toPx() * zoomScale,
                center = p
              )
              drawCircle(
                color = Color.White.copy(alpha = if (isTarget) 0.6f else 0.2f),
                radius = 1.8.dp.toPx() * zoomScale,
                center = Offset(p.x - 0.8f, p.y - 0.8f)
              )
            }
          }

          // 3. Depth Sort Placed Bricks (Back to Front)
          val sortedBricks = placedBricks.sortedBy { brick ->
            val cx = brick.gridX + brick.pieceDef.widthStuds / 2f - 7.5f
            val cy = brick.gridY + brick.pieceDef.lengthStuds / 2f - 7.5f
            // Projection depth along the camera viewing vector
            val depth = cx * sinAz + cy * cosAz
            depth * 10f + brick.layerZ
          }

          // 4. Draw each 3D Brick
          sortedBricks.forEach { brick ->
            val w = if (brick.rotationDeg == 90) brick.pieceDef.lengthStuds else brick.pieceDef.widthStuds
            val l = if (brick.rotationDeg == 90) brick.pieceDef.widthStuds else brick.pieceDef.lengthStuds
            val h = brick.pieceDef.heightPlates.toFloat()
            val z = brick.layerZ.toFloat()

            val bx = brick.gridX.toFloat()
            val by = brick.gridY.toFloat()

            val baseCol = brick.color
            val topCol = baseCol
            val leftCol = baseCol.copy(alpha = 0.72f)
            val rightCol = baseCol.copy(alpha = 0.88f)

            val p000 = project(bx, by, z)
            val p100 = project(bx + w, by, z)
            val p110 = project(bx + w, by + l, z)
            val p010 = project(bx, by + l, z)

            val p001 = project(bx, by, z + h)
            val p101 = project(bx + w, by, z + h)
            val p111 = project(bx + w, by + l, z + h)
            val p011 = project(bx, by + l, z + h)

            // Front/Left Face
            val leftFace = Path().apply {
              moveTo(p000.x, p000.y)
              lineTo(p010.x, p010.y)
              lineTo(p011.x, p011.y)
              lineTo(p001.x, p001.y)
              close()
            }
            drawPath(leftFace, leftCol)

            // Front/Right Face
            val rightFace = Path().apply {
              moveTo(p010.x, p010.y)
              lineTo(p110.x, p110.y)
              lineTo(p111.x, p111.y)
              lineTo(p011.x, p011.y)
              close()
            }
            drawPath(rightFace, rightCol)

            // Top Face
            val topFace = Path().apply {
              moveTo(p001.x, p001.y)
              lineTo(p101.x, p101.y)
              lineTo(p111.x, p111.y)
              lineTo(p011.x, p011.y)
              close()
            }
            drawPath(topFace, topCol)

            // Draw studs on top if not a smooth tile
            if (!brick.pieceDef.isTile) {
              for (sx in 0 until w) {
                for (sy in 0 until l) {
                  val studCenter = project(bx + sx + 0.5f, by + sy + 0.5f, z + h)
                  drawCircle(
                    color = topCol,
                    radius = 3.5.dp.toPx() * zoomScale,
                    center = studCenter
                  )
                  drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = 1.8.dp.toPx() * zoomScale,
                    center = Offset(studCenter.x - 0.8f, studCenter.y - 0.8f)
                  )
                }
              }
            }
          }
        }

        // Overlay 3D Navigation Controls
        Row(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Zoom In
          Surface(
            color = WorkshopSurface.copy(alpha = 0.9f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable { zoomScale = (zoomScale + 0.15f).coerceAtMost(2.0f) }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
          }

          // Zoom Out
          Surface(
            color = WorkshopSurface.copy(alpha = 0.9f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.6f) }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
          }

          // Rotate 90 deg
          Surface(
            color = if (selectedRotation == 90) LegoYellow else WorkshopSurface.copy(alpha = 0.9f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable { selectedRotation = if (selectedRotation == 0) 90 else 0 }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.RotateRight,
                contentDescription = "Rotate Piece 90°",
                tint = if (selectedRotation == 90) Color(0xFF221A00) else TextPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Coordinate Snapper Chips (Top-Left of 3D Canvas)
        Surface(
          color = WorkshopSurface.copy(alpha = 0.85f),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Stud: [X: $targetGridX, Y: $targetGridY]",
              color = LegoYellow,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              IconButton(
                onClick = { targetGridX = (targetGridX - 1).coerceAtLeast(0) },
                modifier = Modifier.size(20.dp)
              ) { Text("◀", color = TextSecondary, fontSize = 9.sp) }
              IconButton(
                onClick = { targetGridX = (targetGridX + 1).coerceAtMost(14) },
                modifier = Modifier.size(20.dp)
              ) { Text("▶", color = TextSecondary, fontSize = 9.sp) }
              IconButton(
                onClick = { targetGridY = (targetGridY - 1).coerceAtLeast(0) },
                modifier = Modifier.size(20.dp)
              ) { Text("▲", color = TextSecondary, fontSize = 9.sp) }
              IconButton(
                onClick = { targetGridY = (targetGridY + 1).coerceAtMost(14) },
                modifier = Modifier.size(20.dp)
              ) { Text("▼", color = TextSecondary, fontSize = 9.sp) }
            }
          }
        }
      }

      // BOTTOM BUILD CONTROLS PANEL
      Card(
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {

          // Action Toolbar & Tool Mode Selector
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              StudioToolMode.entries.forEach { mode ->
                val isSelected = toolMode == mode
                Surface(
                  color = if (isSelected) LegoYellow else WorkshopSurfaceVariant,
                  shape = RoundedCornerShape(8.dp),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) LegoYellow else Color(0xFF353E50)
                  ),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { toolMode = mode }
                ) {
                  Text(
                    text = mode.label,
                    color = if (isSelected) Color(0xFF221A00) else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                  )
                }
              }
            }

            // Primary Drop/Place Button
            Button(
              onClick = {
                // Calculate highest occupied layer at target coordinates to stack on top
                val highestLayer = placedBricks
                  .filter {
                    val w = if (it.rotationDeg == 90) it.pieceDef.lengthStuds else it.pieceDef.widthStuds
                    val l = if (it.rotationDeg == 90) it.pieceDef.widthStuds else it.pieceDef.lengthStuds
                    targetGridX in it.gridX until (it.gridX + w) &&
                      targetGridY in it.gridY until (it.gridY + l)
                  }
                  .maxOfOrNull { it.layerZ + it.pieceDef.heightPlates } ?: 0

                placedBricks.add(
                  PlacedBrick3D(
                    pieceDef = selectedPieceDef,
                    gridX = targetGridX,
                    gridY = targetGridY,
                    layerZ = highestLayer,
                    rotationDeg = selectedRotation,
                    color = activeColor.second,
                    colorName = activeColor.first
                  )
                )
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = LegoRed, contentColor = Color.White),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("+ PLACE BRICK", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Color Palette Swatches
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Color:",
              color = TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(end = 8.dp)
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              legoPalette.forEach { colorPair ->
                val isSelected = activeColor.first == colorPair.first
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colorPair.second)
                    .border(
                      width = if (isSelected) 2.5.dp else 1.dp,
                      color = if (isSelected) LegoYellow else Color.White.copy(alpha = 0.4f),
                      shape = CircleShape
                    )
                    .clickable { activeColor = colorPair }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Category Filter Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            PieceTypeCategory.entries.forEach { cat ->
              val isSelected = selectedCategory == cat
              FilterChip(
                selected = isSelected,
                onClick = { selectedCategory = cat },
                label = {
                  Text(
                    text = cat.displayName,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = LegoBlue,
                  selectedLabelColor = Color.White,
                  containerColor = WorkshopSurfaceVariant,
                  labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) LegoBlue else Color(0xFF353E50),
                  selectedBorderColor = LegoBlue,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Piece Catalog Row (All pieces known to man!)
          val filteredPieces = remember(selectedCategory) {
            DigitalPieceCatalog.ALL_PIECES.filter { it.category == selectedCategory }
          }

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(filteredPieces, key = { it.id }) { piece ->
              val isSelected = selectedPieceDef.id == piece.id
              Surface(
                color = if (isSelected) LegoYellow.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) LegoYellow else Color(0xFF353E50)
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedPieceDef = piece }
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = piece.name,
                    color = if (isSelected) LegoYellow else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${piece.widthStuds}x${piece.lengthStuds} • ${piece.heightPlates}p high",
                    color = TextSecondary,
                    fontSize = 9.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
