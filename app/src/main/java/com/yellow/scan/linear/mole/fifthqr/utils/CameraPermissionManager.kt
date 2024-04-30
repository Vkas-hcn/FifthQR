package com.yellow.scan.linear.mole.fifthqr.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CameraPermissionManager(private val context: Context) {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    fun checkCameraPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                )
            ) {
                showPermissionRationaleDialog(activity)
            } else {
                requestCameraPermission(activity)
            }
        }
    }

    private fun showPermissionRationaleDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Camera Permission Required")
            .setMessage("This app requires camera permission to function properly.")
            .setPositiveButton("Grant") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                requestCameraPermission(activity)
            }
            .setNegativeButton("Deny") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private fun requestCameraPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    fun handleCameraPermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                showPermissionDeniedDialog(context)
            }
        }
    }

    private fun showPermissionDeniedDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("Please grant camera permission in app settings to use this feature.")
            .setPositiveButton("Settings") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                openAppSettings(context)
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}

