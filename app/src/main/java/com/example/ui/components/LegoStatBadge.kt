package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComplexityTier
import com.example.model.LegoPaletteColor
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopSurfaceVariant

@Composable
fun LegoStudIcon(
  color: Color = LegoYellow,
  modifier: Modifier = Modifier.size(14.dp)
) {
  Canvas(modifier = modifier) {
    val r = size.minDimension / 2f
    // Outer circle
    drawCircle(color = color.copy(alpha = 0.85f), radius = r)
    // Raised bevel rim
    drawCircle(
      color = Color.White.copy(alpha = 0.35f),
      radius = r * 0.7f,
      center = Offset(center.x - 1f, center.y - 1f)
    )
    // Center recess
    drawCircle(color = color, radius = r * 0.5f)
  }
}

@Composable
fun LegoBadge(
  text: String,
  backgroundColor: Color,
  textColor: Color = Color.White,
  modifier: Modifier = Modifier,
  showStud: Boolean = true
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor.copy(alpha = 0.22f))
      .border(1.dp, backgroundColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    if (showStud) {
      LegoStudIcon(color = backgroundColor, modifier = Modifier.size(10.dp))
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(
      text = text,
      color = textColor,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun TierBadge(tier: ComplexityTier, modifier: Modifier = Modifier) {
  LegoBadge(
    text = tier.label,
    backgroundColor = tier.badgeColor,
    textColor = Color.White,
    modifier = modifier
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaletteRow(
  palette: List<LegoPaletteColor>,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = "Recommended Palette:",
      color = TextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 6.dp)
    )
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      palette.forEach { colorItem ->
        val colorInt = try {
          Color(android.graphics.Color.parseColor(colorItem.hex))
        } catch (_: Exception) {
          Color.Gray
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(WorkshopSurfaceVariant)
            .border(1.dp, Color(0xFF374052), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(12.dp)
              .clip(CircleShape)
              .background(colorInt)
              .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = colorItem.name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
