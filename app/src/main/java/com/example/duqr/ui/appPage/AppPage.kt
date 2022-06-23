package com.example.duqr.ui.appPage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.duqr.constants.collectFlowAsState
import com.example.duqr.ui.generateQrPage.GeneratorPage

@Composable
fun AppPage() {
    val appViewModel: AppViewModel = hiltViewModel()

    val appPageState =
        appViewModel.appPageState.collectFlowAsState(initial = BottomNavigationState.GeneratorPage)

    val navItems = listOf<BottomNavigationState>(
        BottomNavigationState.GeneratorPage,
        BottomNavigationState.GalleryPage
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxHeight(0.93f)) {
            when (appPageState.value) {
                BottomNavigationState.GalleryPage -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }
                BottomNavigationState.GeneratorPage -> {
                    GeneratorPage()
                }
            }
        }
        BottomNavigationBar(
            modifier = Modifier
                .fillMaxSize(),
            navItems = navItems
        ) { _selectedNavItem ->
            appViewModel.onBottomNavigationItemSelected(navItems[_selectedNavItem])
        }
    }
}