package com.bhavya.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.Fragment
import util.hasPermission

abstract class PermissionFragment:Fragment() {
    val TAG = "PermissionFragment"
    private val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34

    /**
     * Request permission from the user
     */
    fun requestFineLocationPermission() {
        val permissionApproved =
            context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ?: return

        if (permissionApproved) {
            onPermissionGrantedAlready()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive an empty array.
                    Log.d(TAG, "User interaction was cancelled.")
                    onPermissionDenied()
                }
                grantResults[0]  == PackageManager.PERMISSION_GRANTED ->
                    onPermissionGranted()

            }
        }
    }

    abstract fun onPermissionGranted()

    abstract fun onPermissionDenied()

    abstract fun onPermissionGrantedAlready()

}