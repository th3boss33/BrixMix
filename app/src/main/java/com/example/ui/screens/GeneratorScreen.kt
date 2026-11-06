package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.ComplexityTier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BuildCategory
import com.example.ui.AiForgeUiState
import com.example.ui.BuildViewModel
import com.example.ui.ComplexityLevelOption
import com.example.ui.components.LegoBadge
import com.example.ui.components.LegoBrickVisualizer
import com.example.ui.components.LegoStudIcon
import com.example.ui.components.PaletteRow
import com.example.ui.components.StudBackground
import com.example.ui.components.TierBadge
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
  viewModel: BuildViewModel,
  onNavigateToBlueprint: () -> Unit,
  onNavigateToStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentCreation by viewModel.currentCreation.collectAsState()
  val selectedComplexity by viewModel.selectedComplexity.collectAsState()
  val pieceBudget by viewModel.pieceBudget.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val customPrompt by viewModel.customPrompt.collectAsState()
  val aiForgeState by viewModel.aiForgeState.collectAsState()
  val isRolling by viewModel.isRolling.collectAsState()

  val isLoading = aiForgeState is AiForgeUiState.Loading || isRolling

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  StudBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

      // Header Banner
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "BRIX",
              color = LegoRed,
              fontSize = 26.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Text(
              text = "MIX",
              color = LegoYellow,
              fontSize = 26.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            LegoStudIcon(color = LegoYellow, modifier = Modifier.size(16.dp))
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = LegoYellow,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Gemini AI Master Builder • Max 1100 Pieces",
              color = TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        LegoBadge(
          text = "#${currentCreation.combinationNumber}",
          backgroundColor = LegoBlue,
          textColor = Color.White
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // COMPLEXITY LEVEL SELECTOR (Micro to Extreme)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WorkshopCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              LegoStudIcon(color = LegoRed, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Complexity Level",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Surface(
              color = LegoYellow.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, LegoYellow.copy(alpha = 0.5f))
            ) {
              Text(
                text = "${selectedComplexity.label} (${selectedComplexity.pieceRange})",
                color = LegoYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 5 Complexity Level Buttons (Micro, Simple, Moderate, Complex, Extreme)
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            ComplexityLevelOption.entries.forEach { level ->
              val isSelected = selectedComplexity == level
              val btnBgColor by animateColorAsState(
                targetValue = if (isSelected) LegoYellow else WorkshopSurfaceVariant,
                label = "btnBg"
              )
              val btnTextColor = if (isSelected) Color(0xFF221A00) else TextSecondary

              Surface(
                color = btnBgColor,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) LegoYellow else Color(0xFF353E50)
                ),
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { viewModel.setComplexity(level) }
                  .testTag("complexity_${level.name.lowercase()}")
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                ) {
                  Text(
                    text = level.label,
                    color = btnTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                  )
                  Text(
                    text = "${level.defaultPieces}p",
                    color = if (isSelected) Color(0xFF423400) else TextMuted,
                    fontSize = 9.sp
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Fine-tune Piece Budget Slider within 10 to 1100 pieces
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Target Budget: $pieceBudget pieces",
              color = TextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Max: 1100",
              color = TextMuted,
              fontSize = 11.sp
            )
          }

          Slider(
            value = pieceBudget.toFloat(),
            onValueChange = { viewModel.setPieceBudget(it.toInt()) },
            valueRange = 10f..1100f,
            colors = SliderDefaults.colors(
              thumbColor = LegoYellow,
              activeTrackColor = LegoRed,
              inactiveTrackColor = Color(0xFF283042)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("piece_budget_slider")
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Creative Idea / Prompt Input
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Creative Theme or Direction (Optional):",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          OutlinedTextField(
            value = customPrompt,
            onValueChange = { viewModel.setCustomPrompt(it) },
            placeholder = {
              Text(
                text = "e.g., Cyberpunk pizza truck, Dragon with crystal wings, Steampunk submarine...",
                color = TextMuted,
                fontSize = 12.sp
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = LegoYellow,
              unfocusedBorderColor = Color(0xFF353E50),
              focusedContainerColor = WorkshopSurfaceVariant,
              unfocusedContainerColor = WorkshopSurfaceVariant,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_theme_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Category Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            BuildCategory.entries.forEach { category ->
              val isSelected = selectedCategory == category
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.setCategory(category) },
                label = {
                  Text(
                    text = category.displayName,
                    fontSize = 11.sp,
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
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // FORGE CREATION WITH GEMINI AI PRIMARY ACTION BUTTON
      Button(
        onClick = { viewModel.forgeCreationWithGemini() },
        enabled = !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = LegoRed,
          contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .testTag("forge_creation_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          if (aiForgeState is AiForgeUiState.Loading) {
            CircularProgressIndicator(
              color = LegoYellow,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Gemini AI Forging Schema...",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          } else {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = LegoYellow,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "FORGE CREATION WITH GEMINI AI",
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Secondary Quick Offline Roll Option
      OutlinedButton(
        onClick = { viewModel.rollNewCreation() },
        enabled = !isLoading,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A4458)),
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .testTag("quick_offline_roll_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Casino,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Instant Offline Roll ($pieceBudget Pieces)",
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Error Banner (Transparent error propagation per skills)
      if (aiForgeState is AiForgeUiState.Error) {
        Spacer(modifier = Modifier.height(12.dp))
        val errorMsg = (aiForgeState as AiForgeUiState.Error).message
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LegoRed.copy(alpha = 0.15f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, LegoRed.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = LegoRed,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "AI Forge Notice",
                color = LegoRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = errorMsg,
                color = Color.White,
                fontSize = 11.sp
              )
            }
            TextButton(onClick = { viewModel.forgeCreationWithGemini() }) {
              Text("Retry", color = LegoYellow, fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // ACTIVE CREATION PREVIEW CARD
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, WorkshopCardBorder),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("creation_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {

          // Header Row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              TierBadge(tier = currentCreation.tier)
              LegoBadge(
                text = "${currentCreation.pieceCount} Pcs",
                backgroundColor = LegoYellow,
                textColor = Color(0xFF282000),
                showStud = true
              )
            }

            IconButton(
              onClick = { viewModel.toggleSaveCurrentCreation() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = if (currentCreation.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (currentCreation.isSaved) "Remove from saved" else "Save creation",
                tint = if (currentCreation.isSaved) LegoYellow else TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Build Preview Image
          val previewImageRes = remember(currentCreation.id, currentCreation.pieceCount, currentCreation.category) {
            when {
              currentCreation.pieceCount <= 75 || currentCreation.category == BuildCategory.SPACE_MECH && currentCreation.pieceCount < 250 -> R.drawable.img_micro_spaceship
              currentCreation.pieceCount >= 600 || currentCreation.tier == ComplexityTier.EXTREME -> R.drawable.img_titan_mech
              currentCreation.category == BuildCategory.VEHICLES -> R.drawable.img_cyber_buggy
              else -> if (currentCreation.combinationNumber % 2 == 0) R.drawable.img_micro_spaceship else R.drawable.img_cyber_buggy
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(14.dp))
              .border(1.dp, WorkshopCardBorder, RoundedCornerShape(14.dp))
          ) {
            Image(
              painter = painterResource(id = previewImageRes),
              contentDescription = "Build Preview Photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            // Overlay Badge
            Surface(
              color = Color.Black.copy(alpha = 0.65f),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.PhotoCamera,
                  contentDescription = null,
                  tint = LegoYellow,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Build Preview Picture",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Title & Subtitle
          Text(
            text = currentCreation.title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = currentCreation.subtitle,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Lore / Description
          Text(
            text = currentCreation.lore,
            color = Color(0xFFCCD4E0),
            fontSize = 13.sp,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // 2.5D Isometric Structure Preview
          LegoBrickVisualizer(
            creation = currentCreation,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Palette Chips
          PaletteRow(palette = currentCreation.palette)

          Spacer(modifier = Modifier.height(14.dp))

          // Key Master Techniques
          Text(
            text = "Master Techniques:",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            currentCreation.techniques.forEach { tech ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LegoOrange)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = tech,
                  color = TextPrimary,
                  fontSize = 12.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Stat Bar: Estimated Time & Parts Count
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(WorkshopSurfaceVariant)
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = LegoYellow,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "~${currentCreation.estimatedBuildMinutes} min build",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = LegoBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${currentCreation.parts.size} part types",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action 1: BUILD DIGITALLY IN 3D SPACE
          Button(
            onClick = onNavigateToStudio,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = LegoYellow,
              contentColor = Color(0xFF221A00)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("build_in_3d_studio_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.ViewInAr,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "BUILD DIGITALLY IN 3D SPACE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Action 2: View Blueprint & Parts Checklist
          OutlinedButton(
            onClick = onNavigateToBlueprint,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LegoBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LegoBlue),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("view_blueprint_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "VIEW BLUEPRINT & PARTS CHECKLIST",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
