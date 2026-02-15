package com.pasindu.woundcareai.ui.navigation

object Routes {
    const val HOME = "home"
    const val PATIENT_SEARCH = "patient_search"
    const val PATIENT_CREATE = "patient_create"
    const val WOUND_CREATE = "wound_create/{patientId}"
    const val VISIT_TIMELINE = "visit_timeline/{woundId}"
    const val CAMERA_CAPTURE = "camera_capture/{visitId}"
    const val ANALYZE_IMAGE = "analyze_image/{assetId}"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
}