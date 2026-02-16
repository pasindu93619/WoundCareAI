package com.pasindu.woundcareai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pasindu.woundcareai.feature.camera.CameraCaptureScreen
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
                    // TEMP: Bypass Wound/Visit creation to test Camera Module immediately.
                    // In the final app, this will go: Patient -> Wound List -> Create Visit -> Camera.
                    val tempVisitId = "visit_${System.currentTimeMillis()}"
                    navController.navigate(
                        Routes.CAMERA_CAPTURE.replace("{visitId}", tempVisitId)
                    )
                }
            )
        }

        composable(Routes.PATIENT_CREATE) {
            PatientCreateScreen(
                onPatientCreated = { _ ->
                    navController.popBackStack() // Go back to search
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.CAMERA_CAPTURE,
            arguments = listOf(navArgument("visitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val visitId = backStackEntry.arguments?.getString("visitId") ?: ""
            CameraCaptureScreen(
                visitId = visitId,
                onImageCaptured = { imagePath ->
                    // Capture complete.
                    // TODO: Navigate to "Real-time capture guidance" or "Analyze Screen" in next steps.
                    // For now, pop back to simulate finishing the capture task.
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}