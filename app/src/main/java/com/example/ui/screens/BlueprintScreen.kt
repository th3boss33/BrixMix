package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.BuildCategory
import com.example.model.ComplexityTier
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BuildStep
import com.example.model.LegoPart
import com.example.model.PartCategory
import com.example.ui.BuildViewModel
import com.example.ui.components.LegoBadge
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

@Composable
fun BlueprintScreen(
  viewModel: BuildViewModel,
  onNavigateToStudio: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val creation by viewModel.currentCreation.collectAsState()
  val checkedPartIds by viewModel.checkedPartIds.collectAsState()
  val completedSteps by viewModel.completedStepNumbers.collectAsState()

  var selectedTabIndex by remember { mutableIntStateOf(0) }

  StudBackground(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

      // Blueprint Header Card
      Surface(
        color = WorkshopSurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              TierBadge(tier = creation.tier)
              LegoBadge(
                text = "${creation.pieceCount} Pieces",
                backgroundColor = LegoYellow,
                textColor = Color(0xFF261D00),
                showStud = true
              )
            }

            IconButton(
              onClick = { viewModel.toggleSaveCurrentCreation() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = if (creation.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (creation.isSaved) "Saved" else "Save",
                tint = if (creation.isSaved) LegoYellow else TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          val previewImageRes = remember(creation.id, creation.pieceCount, creation.category) {
            when {
              creation.pieceCount <= 75 || creation.category == BuildCategory.SPACE_MECH && creation.pieceCount < 250 -> R.drawable.img_micro_spaceship
              creation.pieceCount >= 600 || creation.tier == ComplexityTier.EXTREME -> R.drawable.img_titan_mech
              creation.category == BuildCategory.VEHICLES -> R.drawable.img_cyber_buggy
              else -> if (creation.combinationNumber % 2 == 0) R.drawable.img_micro_spaceship else R.drawable.img_cyber_buggy
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Image(
              painter = painterResource(id = previewImageRes),
              contentDescription = "Build Preview",
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, WorkshopCardBorder, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = creation.title,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "Combo #${creation.combinationNumber} • ${creation.category.displayName}",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            Button(
              onClick = onNavigateToStudio,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = LegoYellow, contentColor = Color(0xFF221A00)),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("3D Build", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }

      // Tab Navigation (Parts Checklist vs Build Steps)
      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = WorkshopSurfaceVariant,
        contentColor = LegoYellow,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = LegoYellow,
            height = 3.dp
          )
        }
      ) {
        Tab(
          selected = selectedTabIndex == 0,
          onClick = { selectedTabIndex = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.ViewList,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Parts Checklist (${checkedPartIds.size}/${creation.parts.size})",
                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        )

        Tab(
          selected = selectedTabIndex == 1,
          onClick = { selectedTabIndex = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.FormatListNumbered,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Build Steps (${completedSteps.size}/${creation.steps.size})",
                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        )
      }

      // Content depending on selected tab
      if (selectedTabIndex == 0) {
        PartsChecklistContent(
          parts = creation.parts,
          checkedIds = checkedPartIds,
          onToggleCheck = { viewModel.togglePartChecked(it) },
          onCheckAll = { viewModel.checkAllParts() },
          onClearChecks = { viewModel.clearPartChecks() }
        )
      } else {
        BuildStepsContent(
          steps = creation.steps,
          completedSteps = completedSteps,
          onToggleStep = { viewModel.toggleStepCompleted(it) }
        )
      }
    }
  }
}

@Composable
private fun PartsChecklistContent(
  parts: List<LegoPart>,
  checkedIds: Set<String>,
  onToggleCheck: (String) -> Unit,
  onCheckAll: () -> Unit,
  onClearChecks: () -> Unit
) {
  val totalTypes = parts.size
  val checkedCount = checkedIds.size
  val progress = if (totalTypes > 0) checkedCount.toFloat() / totalTypes.toFloat() else 0f

  val partsByCategory = remember(parts) {
    parts.groupBy { it.category }
  }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier
      .fillMaxSize()
      .testTag("parts_checklist_list")
  ) {

    // Progress Banner
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                text = "Brick Bin Gathering Progress",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "$checkedCount of $totalTypes part groups collected (${(progress * 100).toInt()}%)",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedButton(
                onClick = onCheckAll,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text("All", fontSize = 11.sp, color = LegoYellow)
              }
              OutlinedButton(
                onClick = onClearChecks,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text("Reset", fontSize = 11.sp, color = TextMuted)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          LinearProgressIndicator(
            progress = { progress },
            color = if (progress == 1f) LegoGreen else LegoYellow,
            trackColor = Color(0xFF283040),
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
          )
        }
      }
    }

    // Categorized Groups
    PartCategory.entries.forEach { category ->
      val categoryParts = partsByCategory[category]
      if (!categoryParts.isNullOrEmpty()) {
        item {
          Text(
            text = category.displayName,
            color = LegoYellow,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
          )
        }

        items(categoryParts, key = { it.id }) { part ->
          val isChecked = checkedIds.contains(part.id)
          PartRowItem(
            part = part,
            isChecked = isChecked,
            onToggle = { onToggleCheck(part.id) }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PartRowItem(
  part: LegoPart,
  isChecked: Boolean,
  onToggle: () -> Unit
) {
  val rowColor by animateColorAsState(
    targetValue = if (isChecked) WorkshopSurfaceVariant.copy(alpha = 0.6f) else WorkshopSurface,
    label = "rowBg"
  )

  val partColorInt = remember(part.colorHex) {
    try {
      Color(android.graphics.Color.parseColor(part.colorHex))
    } catch (_: Exception) {
      Color.Gray
    }
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = rowColor),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isChecked) LegoGreen.copy(alpha = 0.4f) else WorkshopCardBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onToggle() }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
    ) {

      // Checkbox
      Checkbox(
        checked = isChecked,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(
          checkedColor = LegoGreen,
          checkmarkColor = Color.White,
          uncheckedColor = Color(0xFF4A5568)
        ),
        modifier = Modifier.size(24.dp)
      )

      Spacer(modifier = Modifier.width(12.dp))

      // Quantity Pill
      Surface(
        color = LegoRed.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LegoRed.copy(alpha = 0.5f))
      ) {
        Text(
          text = "${part.quantity}x",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Black,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Part Name and Details
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = part.name,
          color = if (isChecked) TextMuted else TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(partColorInt)
              .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${part.colorName} • Element #${part.elementNumber}",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
private fun BuildStepsContent(
  steps: List<BuildStep>,
  completedSteps: Set<Int>,
  onToggleStep: (Int) -> Unit
) {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier
      .fillMaxSize()
      .testTag("build_steps_list")
  ) {
    items(steps, key = { it.stepNumber }) { step ->
      val isDone = completedSteps.contains(step.stepNumber)

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDone) WorkshopSurfaceVariant.copy(alpha = 0.7f) else WorkshopSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.2.dp,
          if (isDone) LegoGreen.copy(alpha = 0.6f) else WorkshopCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {

          // Step Header
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                color = if (isDone) LegoGreen else LegoYellow,
                shape = CircleShape,
                modifier = Modifier.size(26.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  if (isDone) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  } else {
                    Text(
                      text = "${step.stepNumber}",
                      color = Color(0xFF261D00),
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Black
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = step.phaseName,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(
              onClick = { onToggleStep(step.stepNumber) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (isDone) "Done" else "Mark Done",
                tint = if (isDone) LegoGreen else TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Detailed Instruction
          Text(
            text = step.instruction,
            color = if (isDone) TextMuted else TextPrimary,
            fontSize = 13.sp,
            lineHeight = 19.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Focus Pieces
          Surface(
            color = WorkshopSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Key Assembly Pieces:",
                color = LegoBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = step.focusPieces,
                color = TextSecondary,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Master Builder Tip in Amber Callout
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(LegoOrange.copy(alpha = 0.12f))
              .border(1.dp, LegoOrange.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = LegoOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Builder Tip: ${step.builderTip}",
              color = Color(0xFFFFD180),
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
