package com.pasindu.woundcareai.ui.screens.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasindu.woundcareai.data.local.entity.Patient
import com.pasindu.woundcareai.domain.usecase.patient.CreatePatientUseCase
import com.pasindu.woundcareai.domain.usecase.patient.SearchPatientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchActive: Boolean = false
)

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val createPatientUseCase: CreatePatientUseCase,
    private val searchPatientsUseCase: SearchPatientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> = _uiState.asStateFlow()

    fun searchPatients(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val results = searchPatientsUseCase(query)
                _uiState.update { it.copy(patients = results, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun createPatient(initials: String, mrn: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = createPatientUseCase(initials, mrn)
            result.onSuccess { id ->
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(id)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}