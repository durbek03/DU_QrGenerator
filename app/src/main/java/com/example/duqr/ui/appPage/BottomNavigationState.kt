package com.example.duqr.ui.appPage

import com.example.duqr.R

sealed class BottomNavigationState(val icon: Int) {
    object GeneratorPage : BottomNavigationState(R.drawable.ic_qr)
    object GalleryPage : BottomNavigationState(R.drawable.ic_gallery)
}