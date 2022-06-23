package com.example.duqr.ui.appPage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.duqr.R
import okio.IOException

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navItems: List<BottomNavigationState>,
    onNavItemSelected: (Int) -> Unit
) {
    val selectedNavItem = remember {
        mutableStateOf<Int>(0)
    }
    if (navItems.size > 5) {
        throw IOException(Throwable(message = "BottomNavigationBar can only hold up to 5 items"))
    }

    val selectedNavItemColor = Color(0xFF1D1925)
    val unselectedNavItemColor = Color(0x59221E2C)

    Box {
        Box(
            modifier = modifier
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 1.5.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(MaterialTheme.colors.onBackground)
        ) {
            for (i in navItems.indices) {
                Icon(
                    contentDescription = "Gallery page",
                    modifier = modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            selectedNavItem.value = i
                            onNavItemSelected.invoke(i)
                        }
                        .padding(15.dp),
                    painter = painterResource(navItems[i].icon),
                    tint = if (i == selectedNavItem.value) selectedNavItemColor else unselectedNavItemColor
                )
            }
        }
    }
}