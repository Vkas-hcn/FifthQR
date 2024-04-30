package com.yellow.scan.linear.mole.fifthqr.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NetHelp {
    @SuppressLint("HardwareIds")
    fun blackData(context: Context): Map<String, Any> {
        return mapOf<String, Any>(
            //distinct_id
            "apogee" to AppData.uuid_fif,
            //client_ts
            "shaky" to (System.currentTimeMillis()),
            //device_model
            "crinkle" to Build.MODEL,
            //bundle_id
            "validate" to ("com.easy.fast.photo.scanner.text"),
            //os_version
            "covary" to Build.VERSION.RELEASE,
            //gaid
            "wacky" to AppData.gidData,
            //android_id
            "glimpse" to Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            //os
            "skylark" to "reign",
            //app_version
            "vicky" to (getAppVersion(context) ?: ""),
        )
    }

    private fun getAppVersion(context: Context): String? {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    val smileNetManager = FifNetHelp()
    fun getBlackData(context: Context) {
        val data = AppData.local_clock
        if (data.isEmpty()) {
            val params = blackData(context)
            try {
                smileNetManager.executeMapRequest(
                    "https://salsify.easyfastphoto.com/ohmic/afforest",
                    params,
                    object : FifNetHelp.Callback {
                        override fun onSuccess(response: String) {
                            AppData.local_clock = response
                            Log.e("TAG", "black-onSuccess: ${ AppData.local_clock}")
                        }

                        override fun onFailure(error: String) {
                            nextBlackFun(context)
                            Log.e("TAG", "black-onFailure: $error")

                        }
                    })
            } catch (e: Exception) {
                nextBlackFun(context)
            }
        }
    }

    private fun nextBlackFun(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(10000)
            getBlackData(context)
        }
    }
}