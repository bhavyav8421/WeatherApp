package util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun getFormatedTime(value: Long): String {
    val date = Date(value*1000)
    val format = SimpleDateFormat("HH:mm a")
    return format.format(date)
}

fun getFormatedDateTime(value: Long): String {
    val date = Date(value*1000)
    val format = SimpleDateFormat("MMM dd HH:mm a")
    return format.format(date)
}