package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LegoCreation
import com.example.ui.theme.LegoBlue
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopCardBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant

@Composable
fun LegoBrickVisualizer(
  creation: LegoCreation,
  modifier: Modifier = Modifier
) {
  var visibleLayers by remember(creation.id) { mutableFloatStateOf(5f) }
  val animatedLayers by animateFloatAsState(
    targetValue = visibleLayers,
    animationSpec = tween(durationMillis = 250),
    label = "layers"
  )

  val parsedColors = remember(creation.palette) {
    if (creation.palette.isNotEmpty()) {
      creation.palette.map {
        try {
          Color(android.graphics.Color.parseColor(it.hex))
        } catch (_: Exception) {
          LegoRed
        }
      }
    } else {
      listOf(LegoRed, LegoYellow, LegoBlue)
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(WorkshopSurface)
      .border(1.dp, WorkshopCardBorder, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = null,
          tint = LegoYellow,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Modular Structure Preview",
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "Layer ${visibleLayers.toInt()} of 5",
        color = LegoYellow,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Interactive 2.5D Isometric Canvas
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(WorkshopSurfaceVariant)
    ) {
      Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val centerX = size.width / 2f
        val centerY = size.height * 0.72f

        val brickWidth = 100.dp.toPx()
        val brickDepth = 55.dp.toPx()
        val layerHeight = 16.dp.toPx()

        // Baseplate shadow
        val shadowPath = Path().apply {
          moveTo(centerX, centerY + 18)
          lineTo(centerX + brickWidth + 20, centerY - brickDepth + 18)
          lineTo(centerX, centerY - (brickDepth * 2) + 18)
          lineTo(centerX - brickWidth - 20, centerY - brickDepth + 18)
          close()
        }
        drawPath(shadowPath, Color(0xFF0C0E13))

        // Draw stacked isometric layers from bottom to top
        val maxLayer = animatedLayers.toInt().coerceIn(1, 5)
        for (layer in 1..maxLayer) {
          val yOffset = (layer - 1) * layerHeight
          val baseColor = parsedColors[(layer - 1) % parsedColors.size]

          val topColor = baseColor
          val leftColor = baseColor.copy(alpha = 0.75f)
          val rightColor = baseColor.copy(alpha = 0.88f)

          val lX = centerX - brickWidth * (1f - (layer * 0.04f))
          val rX = centerX + brickWidth * (1f - (layer * 0.04f))
          val topY = centerY - (brickDepth * 2) - yOffset
          val midY = centerY - brickDepth - yOffset
          val botY = centerY - yOffset

          // Left face
          val leftFace = Path().apply {
            moveTo(lX, midY)
            lineTo(centerX, botY)
            lineTo(centerX, botY + layerHeight)
            lineTo(lX, midY + layerHeight)
            close()
          }
          drawPath(leftFace, leftColor)

          // Right face
          val rightFace = Path().apply {
            moveTo(centerX, botY)
            lineTo(rX, midY)
            lineTo(rX, midY + layerHeight)
            lineTo(centerX, botY + layerHeight)
            close()
          }
          drawPath(rightFace, rightColor)

          // Top Face
          val topFace = Path().apply {
            moveTo(centerX, topY)
            lineTo(rX, midY)
            lineTo(centerX, botY)
            lineTo(lX, midY)
            close()
          }
          drawPath(topFace, topColor)

          // Studs on the top layer
          if (layer == maxLayer) {
            val studRadiusX = 9.dp.toPx()
            val studRadiusY = 5.dp.toPx()

            val studOffsets = listOf(
              Offset(centerX - 35, midY - 12),
              Offset(centerX + 35, midY - 12),
              Offset(centerX, midY - 24),
              Offset(centerX, midY + 4)
            )

            studOffsets.forEach { pos ->
              // Stud cylinder side
              val studSide = Path().apply {
                moveTo(pos.x - studRadiusX, pos.y)
                lineTo(pos.x + studRadiusX, pos.y)
                lineTo(pos.x + studRadiusX, pos.y + 4.dp.toPx())
                lineTo(pos.x - studRadiusX, pos.y + 4.dp.toPx())
                close()
              }
              drawPath(studSide, leftColor)

              // Stud oval cap
              drawOval(
                color = topColor,
                topLeft = Offset(pos.x - studRadiusX, pos.y - studRadiusY),
                size = androidx.compose.ui.geometry.Size(studRadiusX * 2, studRadiusY * 2)
              )
              // Stud top highlight rim
              drawOval(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(pos.x - studRadiusX * 0.7f, pos.y - studRadiusY * 0.7f),
                size = androidx.compose.ui.geometry.Size(studRadiusX * 1.4f, studRadiusY * 1.4f)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Layer Slicer Slider
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "Assembly Slicer:",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.width(10.dp))
      Slider(
        value = visibleLayers,
        onValueChange = { visibleLayers = it },
        valueRange = 1f..5f,
        steps = 3,
        colors = SliderDefaults.colors(
          thumbColor = LegoYellow,
          activeTrackColor = LegoRed,
          inactiveTrackColor = Color(0xFF333C4E)
        ),
        modifier = Modifier.weight(1f)
      )
    }
  }
}
