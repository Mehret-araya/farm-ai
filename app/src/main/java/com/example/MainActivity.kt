package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FarmBottomNavBar
import com.example.ui.components.FarmScreen
import com.example.ui.components.FarmTopBar
import com.example.ui.screens.AgronomistChatScreen
import com.example.ui.screens.CropGuidesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiseaseDiagnosisScreen
import com.example.ui.screens.FarmerProfileDialog
import com.example.ui.screens.MyCropsScreen
import com.example.ui.theme.FarmAITheme
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FarmAITheme {
        FarmApp()
      }
    }
  }
}

@Composable
fun FarmApp(viewModel: FarmViewModel = viewModel()) {
  val isAmharic by viewModel.isAmharic.collectAsState()
  var currentScreen by remember { mutableStateOf(FarmScreen.DASHBOARD) }
  var showProfileDialog by remember { mutableStateOf(false) }

  // Back handling: return to dashboard if on sub-screens
  BackHandler(enabled = currentScreen != FarmScreen.DASHBOARD) {
    currentScreen = FarmScreen.DASHBOARD
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      FarmTopBar(
        currentScreen = currentScreen,
        isAmharic = isAmharic,
        onToggleLanguage = { viewModel.toggleLanguage() },
        onOpenProfile = { showProfileDialog = true }
      )
    },
    bottomBar = {
      FarmBottomNavBar(
        currentScreen = currentScreen,
        isAmharic = isAmharic,
        onScreenSelected = { currentScreen = it }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        FarmScreen.DASHBOARD -> DashboardScreen(
          viewModel = viewModel,
          onNavigate = { currentScreen = it },
          onOpenProfile = { showProfileDialog = true }
        )
        FarmScreen.CROPS -> MyCropsScreen(viewModel = viewModel)
        FarmScreen.DIAGNOSE -> DiseaseDiagnosisScreen(viewModel = viewModel)
        FarmScreen.CHAT -> AgronomistChatScreen(viewModel = viewModel)
        FarmScreen.GUIDES -> CropGuidesScreen(viewModel = viewModel)
      }
    }
  }

  if (showProfileDialog) {
    FarmerProfileDialog(
      viewModel = viewModel,
      onDismiss = { showProfileDialog = false }
    )
  }
}
