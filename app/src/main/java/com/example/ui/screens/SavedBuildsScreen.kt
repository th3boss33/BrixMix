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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.LegoCreation
import com.example.ui.BuildViewModel
import com.example.ui.components.LegoBadge
import com.example.ui.components.LegoStudIcon
import com.example.ui.components.StudBackground
import com.example.ui.components.TierBadge
import com.example.ui.theme.LegoBlue
import com.example.ui.theme.LegoGreen
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopCardBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant

@Composable
fun SavedBuildsScreen(
  viewModel: BuildViewModel,
  onNavigateToBlueprint: () -> Unit,
  onNavigateToStudio: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val savedList by viewModel.savedCreations.collectAsState()

  var editingCreation by remember { mutableStateOf<LegoCreation?>(null) }
  var editNotesText by remember { mutableStateOf("") }

  StudBackground(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

      // Header Banner
      Surface(
        color = WorkshopSurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(16.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            tint = LegoYellow,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "MY BUILD DIARY & FAVORITES",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "${savedList.size} Saved Blueprints",
              color = TextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }

      if (savedList.isEmpty()) {
        // Empty State
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Surface(
              color = WorkshopSurfaceVariant,
              shape = CircleShape,
              modifier = Modifier.size(72.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                LegoStudIcon(color = LegoYellow, modifier = Modifier.size(36.dp))
              }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "No Saved Blueprints Yet",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Generate a creation in the Forge and tap the bookmark icon to save it to your offline build collection.",
              color = TextSecondary,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier
            .fillMaxSize()
            .testTag("saved_builds_list")
        ) {
          items(savedList, key = { it.id }) { creation ->
            SavedBuildItemCard(
              creation = creation,
              onOpen = {
                viewModel.loadCreation(creation)
                onNavigateToBlueprint()
              },
              onToggleBuilt = {
                viewModel.updateSavedCreation(creation.copy(isBuilt = !creation.isBuilt))
              },
              onEditNotes = {
                editingCreation = creation
                editNotesText = creation.personalNotes
              },
              onDelete = {
                viewModel.deleteSavedCreation(creation.id)
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(24.dp))
          }
        }
      }
    }
  }

  // Edit Notes Dialog
  editingCreation?.let { creation ->
    AlertDialog(
      onDismissRequest = { editingCreation = null },
      containerColor = WorkshopSurface,
      title = {
        Text(
          text = "Builder Notes: ${creation.title}",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = "Record parts modifications, build date, or where you stored this model:",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editNotesText,
            onValueChange = { editNotesText = it },
            placeholder = { Text("e.g. Built with 1990s Space Lego pieces", color = TextMuted) },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateSavedCreation(creation.copy(personalNotes = editNotesText))
            editingCreation = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = LegoYellow, contentColor = Color(0xFF261D00))
        ) {
          Text("Save Notes")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingCreation = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
private fun SavedBuildItemCard(
  creation: LegoCreation,
  onOpen: () -> Unit,
  onToggleBuilt: () -> Unit,
  onEditNotes: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
    border = androidx.compose.foundation.BorderStroke(1.2.dp, WorkshopCardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {

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
          TierBadge(tier = creation.tier)
          LegoBadge(
            text = "${creation.pieceCount} Pcs",
            backgroundColor = LegoYellow,
            textColor = Color(0xFF261D00),
            showStud = true
          )
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = LegoRed.copy(alpha = 0.8f)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = creation.title,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Combination #${creation.combinationNumber} • ${creation.category.displayName}",
        color = TextSecondary,
        fontSize = 12.sp
      )

      if (creation.personalNotes.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = WorkshopSurfaceVariant,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Notes: ${creation.personalNotes}",
            color = Color(0xFFD6DFEC),
            fontSize = 11.sp,
            modifier = Modifier.padding(8.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status and Actions Row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {

        // Built Status Switcher
        Surface(
          color = if (creation.isBuilt) LegoGreen.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (creation.isBuilt) LegoGreen else Color(0xFF384254)
          ),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleBuilt() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = if (creation.isBuilt) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
              contentDescription = null,
              tint = if (creation.isBuilt) LegoGreen else TextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (creation.isBuilt) "Built in Real Life!" else "Wishlist Plan",
              color = if (creation.isBuilt) LegoGreen else TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(onClick = onEditNotes, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Notes",
              tint = LegoYellow,
              modifier = Modifier.size(18.dp)
            )
          }

          Button(
            onClick = onOpen,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LegoBlue, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Blueprint", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
