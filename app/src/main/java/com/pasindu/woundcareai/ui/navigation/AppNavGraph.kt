package com.pasindu.woundcareai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pasindu.woundcareai.ui.screens.home.HomeScreen
import com.pasindu.woundcareai.ui.screens.patient.PatientCreateScreen
import com.pasindu.woundcareai.ui.screens.patient.PatientSearchScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToPatientSearch = { navController.navigate(Routes.PATIENT_SEARCH) },
                onNavigateToAnalytics = { navController.navigate(Routes.ANALYTICS) }
            )
        }

        composable(Routes.PATIENT_SEARCH) {
            PatientSearchScreen(
                onNavigateToCreate = { navController.navigate(Routes.PATIENT_CREATE) },
                onPatientSelected = { patientId ->
                    // Navigate to wound list for this patient (Next Step)
                    // navController.navigate(Routes.WOUND_LIST + "/$patientId")
                }
            )
        }

        composable(Routes.PATIENT_CREATE) {
            PatientCreateScreen(
                onPatientCreated = { patientId ->
                    navController.popBackStack() // Go back to search
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}