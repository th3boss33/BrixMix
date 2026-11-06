package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreationGenerator
import com.example.data.MatrixCombinationItem
import com.example.model.ComplexityTier
import com.example.ui.BuildViewModel
import com.example.ui.components.LegoBadge
import com.example.ui.components.StudBackground
import com.example.ui.components.TierBadge
import com.example.ui.theme.LegoBlue
import com.example.ui.theme.LegoOrange
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopCardBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant

@Composable
fun MatrixScreen(
  viewModel: BuildViewModel,
  onNavigateToBlueprint: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTier by remember { mutableStateOf<ComplexityTier?>(null) }

  // Sample items across the 1100 combinations index
  val sampleIndices = remember(selectedTier, searchQuery) {
    val list = mutableListOf<Int>()

    // If search is a valid number 1..1100, put it first
    val queryNum = searchQuery.toIntOrNull()
    if (queryNum != null && queryNum in 1..1100) {
      list.add(queryNum)
    }

    // Generate balanced items from 1 to 1100
    val step = when (selectedTier) {
      ComplexityTier.SIMPLE -> 3
      ComplexityTier.MODERATE -> 5
      ComplexityTier.COMPLEX -> 6
      ComplexityTier.EXTREME -> 4
      null -> 11
    }

    val range = when (selectedTier) {
      ComplexityTier.SIMPLE -> 1..180
      ComplexityTier.MODERATE -> 181..500
      ComplexityTier.COMPLEX -> 501..850
      ComplexityTier.EXTREME -> 851..1100
      null -> 1..1100
    }

    for (i in range step step) {
      if (!list.contains(i)) {
        list.add(i)
      }
    }

    // Always include flagship items 42 and 1100
    if ((selectedTier == null || selectedTier == ComplexityTier.SIMPLE) && !list.contains(42)) {
      list.add(0, 42)
    }
    if ((selectedTier == null || selectedTier == ComplexityTier.EXTREME) && !list.contains(1100)) {
      list.add(1100)
    }

    list
  }

  StudBackground(modifier = modifier) {
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("matrix_screen_list")
    ) {

      // Header Banner
      item {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = null,
              tint = LegoYellow,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "1,100 COMBINATION MATRIX",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }
          Text(
            text = "Explore the full spectrum of procedural brick configurations",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }

      // Lego Math Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, LegoYellow.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Surface(
              color = LegoYellow.copy(alpha = 0.2f),
              shape = CircleShape,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = LegoYellow,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "The Power of Brick Mathematics",
                color = LegoYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Six 2x4 standard bricks can be configured in 915,103,765 ways. With 1,100 pieces, the combinatorial architectures reach into the billions!",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      // Search & Lookup Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Jump to combination # (1–1100)", color = TextMuted, fontSize = 13.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = LegoYellow)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LegoYellow,
            unfocusedBorderColor = WorkshopCardBorder,
            focusedContainerColor = WorkshopSurface,
            unfocusedContainerColor = WorkshopSurface,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("matrix_search_field")
        )
      }

      // Tier Filter Chips
      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val tiers = listOf(
            Pair("All 1100", null),
            Pair("Simple", ComplexityTier.SIMPLE),
            Pair("Moderate", ComplexityTier.MODERATE),
            Pair("Complex", ComplexityTier.COMPLEX),
            Pair("Extreme", ComplexityTier.EXTREME)
          )

          tiers.forEach { (label, tier) ->
            val isSelected = selectedTier == tier
            FilterChip(
              selected = isSelected,
              onClick = { selectedTier = tier },
              label = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = tier?.badgeColor ?: LegoYellow,
                selectedLabelColor = if (tier == null) Color(0xFF211A00) else Color.White,
                containerColor = WorkshopSurface,
                labelColor = TextSecondary
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = Color(0xFF353E50),
                selectedBorderColor = tier?.badgeColor ?: LegoYellow,
                enabled = true,
                selected = isSelected
              )
            )
          }
        }
      }

      // Matrix Items List
      items(sampleIndices, key = { it }) { index ->
        val item = remember(index) { CreationGenerator.getMatrixItem(index) }

        MatrixItemCard(
          item = item,
          onSelect = {
            viewModel.loadCombinationByNumber(item.index)
            onNavigateToBlueprint()
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun MatrixItemCard(
  item: MatrixCombinationItem,
  onSelect: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onSelect() }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.padding(14.dp)
    ) {

      // Combination Number and Title
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = item.tier.badgeColor.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, item.tier.badgeColor.copy(alpha = 0.5f))
        ) {
          Text(
            text = "#${item.index}",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = item.title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${item.pieceCount} Pieces • ${item.category.displayName}",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }

      // Load & Blueprint Button
      Button(
        onClick = onSelect,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = LegoBlue,
          contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
      ) {
        Text("Build", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
