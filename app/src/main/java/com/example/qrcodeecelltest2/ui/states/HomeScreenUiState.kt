package com.example.qrcodeecelltest2.ui.states

data class HomeScreenUiState(
    val isSendExpanded : Boolean = false,
    val scannedData : String = "",
    val isScanned : Boolean = false,
    val isDataScreen : Boolean = false
)
