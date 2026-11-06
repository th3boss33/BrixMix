package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.BuildViewModel
import com.example.ui.screens.BlueprintScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.MatrixScreen
import com.example.ui.screens.SavedBuildsScreen
import com.example.ui.theme.LegoRed
import com.example.ui.theme.LegoYellow
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WorkshopSurface
import com.example.util.configureAppCheck

import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.outlined.ViewInAr
import com.example.ui.screens.Digital3DStudioScreen

enum class AppScreen(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  GENERATOR("Forge", Icons.Filled.Casino, Icons.Outlined.Casino),
  STUDIO3D("3D Studio", Icons.Filled.ViewInAr, Icons.Outlined.ViewInAr),
  BLUEPRINT("Blueprint", Icons.Filled.FormatListNumbered, Icons.Outlined.FormatListNumbered),
  MATRIX("1100 Matrix", Icons.Filled.Calculate, Icons.Outlined.Calculate),
  SAVED("My Builds", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

class MainActivity : ComponentActivity() {

  private val viewModel: BuildViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    configureAppCheck(this, intent)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        var currentScreen by remember { mutableStateOf(AppScreen.GENERATOR) }

        // BackHandler to navigate back to Forge / Generator screen
        BackHandler(enabled = currentScreen != AppScreen.GENERATOR) {
          currentScreen = AppScreen.GENERATOR
        }

        Scaffold(
          bottomBar = {
            NavigationBar(
              containerColor = WorkshopSurface,
              modifier = Modifier.testTag("bottom_nav_bar")
            ) {
              AppScreen.entries.forEach { screen ->
                val isSelected = currentScreen == screen
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { currentScreen = screen },
                  icon = {
                    Icon(
                      imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                      contentDescription = screen.label
                    )
                  },
                  label = {
                    Text(
                      text = screen.label,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF241C00),
                    selectedTextColor = LegoYellow,
                    indicatorColor = LegoYellow,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextSecondary
                  )
                )
              }
            }
          },
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          when (currentScreen) {
            AppScreen.GENERATOR -> GeneratorScreen(
              viewModel = viewModel,
              onNavigateToBlueprint = { currentScreen = AppScreen.BLUEPRINT },
              onNavigateToStudio = { currentScreen = AppScreen.STUDIO3D },
              modifier = Modifier.padding(innerPadding)
            )
            AppScreen.STUDIO3D -> Digital3DStudioScreen(
              viewModel = viewModel,
              modifier = Modifier.padding(innerPadding)
            )
            AppScreen.BLUEPRINT -> BlueprintScreen(
              viewModel = viewModel,
              onNavigateToStudio = { currentScreen = AppScreen.STUDIO3D },
              modifier = Modifier.padding(innerPadding)
            )
            AppScreen.MATRIX -> MatrixScreen(
              viewModel = viewModel,
              onNavigateToBlueprint = { currentScreen = AppScreen.BLUEPRINT },
              modifier = Modifier.padding(innerPadding)
            )
            AppScreen.SAVED -> SavedBuildsScreen(
              viewModel = viewModel,
              onNavigateToBlueprint = { currentScreen = AppScreen.BLUEPRINT },
              onNavigateToStudio = { currentScreen = AppScreen.STUDIO3D },
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }
}
