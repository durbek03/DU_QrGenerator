package com.example.duqr

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


class ConnectivityHelper @Inject constructor(@ApplicationContext val context: Context) {

    private val TAG = "ConnectivityHelper"
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun internetAvailable(): Boolean {
        if (isNetworkAvailable()) {
            try {
                val urlc: HttpURLConnection =
                    URL("http://www.google.com").openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Test")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1500
                urlc.connect()
                return urlc.responseCode == 200
            } catch (e: IOException) {
                Log.e(TAG, "Error checking internet connection", e)
            }
        } else {
            Log.d(TAG, "No network available!")
        }
        return false
    }
}