//package com.example.healthtracker
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.checkSelfPermission
//import androidx.fragment.app.FragmentActivity
//object PermissionManager {
//
//    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
//    private const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE = 2
//
//    fun isNotificationPermissionGranted(context: Context): Boolean {
//        return checkSelfPermission(
//            context,
//            Manifest.permission.POST_NOTIFICATIONS
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    // Check if activity recognition permission is granted
//    fun isActivityRecognitionPermissionGranted(context: Context): Boolean {
//        return checkSelfPermission(
//            context,
//            Manifest.permission.ACTIVITY_RECOGNITION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    // Request notification permission
//    fun requestNotificationPermission(activity: FragmentActivity) {
//        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            // Handle the result if needed
//        }.launch(Manifest.permission.RECEIVE_NOTIFICATIONS)
//    }
//
//    // Request activity recognition permission
//    fun requestActivityRecognitionPermission(activity: FragmentActivity) {
//        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            // Handle the result if needed
//        }.launch(Manifest.permission.ACTIVITY_RECOGNITION)
//    }
//}