package com.pasindu.woundcareai.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCreateScreen(
    onPatientCreated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PatientViewModel = hiltViewModel()
) {
    var initials by remember { mutableStateOf("") }
    var mrn by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Patient") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = initials,
                onValueChange = { initials = it },
                label = { Text("Initials / Pseudonym") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = mrn,
                onValueChange = { mrn = it },
                label = { Text("MRN / Local ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (initials.isNotBlank() && mrn.isNotBlank()) {
                        viewModel.createPatient(initials, mrn, onPatientCreated)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Patient")
                }
            }
        }
    }
}