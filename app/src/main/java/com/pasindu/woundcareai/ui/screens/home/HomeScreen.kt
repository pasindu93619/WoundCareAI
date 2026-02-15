package com.pasindu.woundcareai.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToPatientSearch: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "WoundCare AI")
        Text(text = "Redmi Note 14 5G Edition")

        Button(
            onClick = onNavigateToPatientSearch,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Manage Patients")
        }

        Button(
            onClick = onNavigateToAnalytics,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Analytics")
        }
    }
}