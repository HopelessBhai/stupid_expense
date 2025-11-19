package com.example.stupidexpense.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stupidexpense.data.TotalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable snapshot of everything the UI needs to render.
 */
data class TotalUiState(
    val total: Float = 0f,
    val input: String = ""
)

/**
 * ViewModel that coordinates between the UI and the repository layer.
 */
class TotalViewModel(private val repository: TotalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TotalUiState())
    val uiState: StateFlow<TotalUiState> = _uiState

    init {
        // Observe the saved total so configuration changes and process deaths stay in sync.
        viewModelScope.launch {
            repository.totalStream.collect { savedTotal ->
                _uiState.update { current -> current.copy(total = savedTotal) }
            }
        }
    }

    /** Handles every change that happens in the amount text field. */
    fun onInputChange(newValue: String) {
        _uiState.update { state -> state.copy(input = newValue) }
    }

    /** Validates the user input, updates running total, and persists it. */
    fun addAmount() {
        val amount = _uiState.value.input.trim().toFloatOrNull()
        if (amount == null) {
            // Ignore invalid numbers but clear the field to keep the widget tidy.
            _uiState.update { it.copy(input = "") }
            return
        }

        val updatedTotal = _uiState.value.total + amount
        _uiState.update { it.copy(total = updatedTotal, input = "") }

        viewModelScope.launch {
            repository.saveTotal(updatedTotal)
        }
    }

    /** Resets totals when the user confirms the action from the settings page. */
    fun resetTotal() {
        _uiState.update { it.copy(total = 0f, input = "") }
        viewModelScope.launch {
            repository.resetTotal()
        }
    }
}

/** Factory that wires repository instances into the ViewModel. */
class TotalViewModelFactory(private val repository: TotalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TotalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TotalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
