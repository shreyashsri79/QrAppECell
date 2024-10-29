package com.example.qrcodeecelltest2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.qrcodeecelltest2.ui.states.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> =_uiState.asStateFlow()

    fun expandSend(){
        _uiState.value = _uiState.value.copy(
            isSendExpanded = !_uiState.value.isSendExpanded
        )
    }

    fun contractSend(){
        _uiState.value = _uiState.value.copy(
            isScanned = false
        )
    }

    fun updateResult( newResult : String){
        _uiState.value = _uiState.value.copy(
            scannedData = newResult
        )
    }

    fun showSend(){
        _uiState.value = _uiState.value.copy(
            isScanned = true
        )

    }

    fun showDataScreen(){
        _uiState.value = _uiState.value.copy(
            isDataScreen = true
        )

    }

    fun exitDataScreen(){
        _uiState.value = _uiState.value.copy(
            isDataScreen = false
        )
    }
}