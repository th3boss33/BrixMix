package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StudPatternColor
import com.example.ui.theme.WorkshopBackground

@Composable
fun StudBackground(
  modifier: Modifier = Modifier,
  studColor: Color = StudPatternColor,
  backgroundColor: Color = WorkshopBackground,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(backgroundColor)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val studRadius = 2.5.dp.toPx()
      val studHighlightRadius = 1.2.dp.toPx()
      val spacing = 28.dp.toPx()

      val cols = (size.width / spacing).toInt() + 2
      val rows = (size.height / spacing).toInt() + 2

      for (c in 0 until cols) {
        for (r in 0 until rows) {
          val cx = c * spacing
          val cy = r * spacing

          // Stud base shadow
          drawCircle(
            color = studColor,
            radius = studRadius,
            center = Offset(cx, cy)
          )
          // Subtle top highlight for 3D raised stud look
          drawCircle(
            color = studColor.copy(alpha = 0.4f),
            radius = studHighlightRadius,
            center = Offset(cx - 0.8f, cy - 0.8f)
          )
        }
      }
    }
    content()
  }
}
