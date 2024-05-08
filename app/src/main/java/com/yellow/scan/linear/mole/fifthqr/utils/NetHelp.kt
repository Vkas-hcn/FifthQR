package com.yellow.scan.linear.mole.fifthqr.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.WebSettings
import com.android.installreferrer.api.ReferrerDetails
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.yellow.scan.linear.mole.fifthqr.BuildConfig
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.bean.AdInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Currency
import java.util.Locale
import java.util.UUID

object NetHelp {
    private fun postAdOnline(adValue: Long) {
        if (BuildConfig.DEBUG) {
            return
        }
        AppEventsLogger.newLogger(App.getAppContext()).logPurchase(
            (adValue / 1000000.0).toBigDecimal(), Currency.getInstance("USD")
        )
    }

    private fun getWebDefaultUserAgent(context: Context): String {
        return try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getLimitTracking(context: Context): String {
        return try {
            if (AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled) {
                "dutchess"
            } else {
                "shivery"
            }
        } catch (e: Exception) {
            "shivery"
        }
    }

    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getLastUpdateTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun getPrecisionType(precisionType: Int): String {
        return when (precisionType) {
            0 -> {
                "UNKNOWN"
            }

            1 -> {
                "ESTIMATED"
            }

            2 -> {
                "PUBLISHER_PROVIDED"
            }

            3 -> {
                "PRECISE"
            }

            else -> {
                "UNKNOWN"
            }
        }
    }

    private fun getNetworkInfo(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val carrierName = telephonyManager.networkOperatorName
        val networkOperator = telephonyManager.networkOperator
        val mcc = if (networkOperator.length >= 3) networkOperator.substring(0, 3) else ""
        val mnc = if (networkOperator.length >= 5) networkOperator.substring(3) else ""

        return """
        Carrier Name: $carrierName
        MCC: $mcc
        MNC: $mnc
    """.trimIndent()
    }

    private fun firstJsonData(context: Context): JSONObject {
        val jsonData = JSONObject()
        //log_id
        jsonData.put("infant", UUID.randomUUID().toString())
        //device_model
        jsonData.put("crinkle", Build.MODEL)
        //android_id
        jsonData.put(
            "glimpse",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //gaid
        jsonData.put("wacky", AppData.gidData)
        //system_language
        jsonData.put("raphael", "${Locale.getDefault().language}_${Locale.getDefault().country}")
        //os
        jsonData.put("skylark", "reign")
        //manufacturer
        jsonData.put("flashy", Build.MODEL)
        //bundle_id
        jsonData.put("validate", context.packageName)
        //app_version
        jsonData.put("vicky", getAppVersion(context))
        //operator
        jsonData.put("undulate", getNetworkInfo(context))
        //distinct_id
        jsonData.put(
            "apogee",
            AppData.uuid_fif
        )
        //client_ts
        jsonData.put("shaky", System.currentTimeMillis())
        //os_version
        jsonData.put("covary", Build.VERSION.RELEASE)
        return jsonData
    }

    private fun getSessionJson(context: Context): String {
        val topLevelJson = firstJsonData(context)
        topLevelJson.apply {
            put("graphic", JSONObject())
        }
        return topLevelJson.toString()
    }

    private fun getInstallJson(rd: ReferrerDetails, context: Context): String {
        val topLevelJson = firstJsonData(context)
        topLevelJson.apply {
            //build
            put("appear", "build/${Build.ID}")

            //referrer_url
            put("irritate", rd.installReferrer)

            //install_version
            put("cesare", rd.installVersion)

            //user_agent
            put("corsica", getWebDefaultUserAgent(context))

            //lat
            put("inertia", getLimitTracking(context))

            //referrer_click_timestamp_seconds
            put("wherever", rd.referrerClickTimestampSeconds)

            //install_begin_timestamp_seconds
            put("throb", rd.installBeginTimestampSeconds)

            //referrer_click_timestamp_server_seconds
            put("lavish", rd.referrerClickTimestampServerSeconds)

            //install_begin_timestamp_server_seconds
            put("p", rd.installBeginTimestampServerSeconds)

            //install_first_seconds
            put("oakley", getFirstInstallTime(context))

            //last_update_seconds
            put("roentgen", getLastUpdateTime(context))
            put("rogue", "anderson")
        }
        return topLevelJson.toString()
    }

    private fun getAdJson(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        adInformation: AdInformation
    ): String {
        val topLevelJson = firstJsonData(context)
        topLevelJson.apply {
            put("waltham", JSONObject().apply {
                //ad_pre_ecpm
                put("neo", adValue.valueMicros)
                //currency
                put("cowbird", adValue.currencyCode)
                //ad_network
                put(
                    "perez",
                    responseInfo.mediationAdapterClassName
                )
                //ad_source
                put("raleigh", "admob")
                //ad_code_id
                put("glucose", adInformation.id_scan)
                //ad_pos_id
                put("barrow", adInformation.name_scan)
                //ad_format
                put("elan", adInformation.type_scan)
                //precision_type
                put("betrayal", getPrecisionType(adValue.precisionType))
            })
        }


        return topLevelJson.toString()
    }

    fun getTbaDataJson(context: Context, name: String): String {
        return firstJsonData(context).apply {
            put("rogue", name)
        }.toString()
    }

    fun getTbaTimeDataJson(
        context: Context,
        name: String,
        parameterName: String,
        time: String?,
    ): String {
        return firstJsonData(context).apply {
            put("rogue", name)
            put(name, JSONObject().apply {
                put(parameterName, time)
            })
        }.toString()
    }

    private fun getTbaTimeListDataJson(
        context: Context,
        name: String,
        pName1: String?,
        pValue1: String?,
        pName2: String?,
        pValue2: String?
    ): String {
        return firstJsonData(context).apply {
            put("rogue", name)
            put(name, JSONObject().apply {
                if (pName1 != null) {
                    put(pName1, pValue1)
                }
                if (pName2 != null) {
                    put(pName2, pValue2)
                }
            })
        }.toString()
    }

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
                            Log.e("TAG", "black-onSuccess: ${AppData.local_clock}")
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

    fun postPotNet(
        context: Context,
        name: String,
        key: String? = null,
        keyValue: String? = null,
        successFun: () -> Unit,
    ) {
        val data = if (key != null) {
            getTbaTimeDataJson(context, name, key, keyValue)
        } else {
            getTbaDataJson(context, name)
        }
        Log.e("TAG", "postPotNet--${name}: data=${data}")
        try {
            smileNetManager.postPutData(
                AppData.put_data_url,
                data,
                object : FifNetHelp.Callback {
                    override fun onSuccess(response: String) {
                        Log.e("TAG", "postPotNet--${name}: onSuccess=${response}")
                        successFun()
                    }

                    override fun onFailure(error: String) {
                        Log.e("TAG", "postPotNet--${name}: onFailure=${error}")
//                        val bean = PotIntInfo()
//                        bean.name = name
//                        bean.parameterName = key
//                        bean.parameterValue = keyValue
//                        addAnErrorMessage(bean)
                    }
                })
        } catch (e: Exception) {
            Log.e("TAG", "postPotNet--${name}: Exception=${e}")
        }
    }


    private fun postPotNet(
        context: Context,
        name: String,
        key1: String? = null,
        keyValue1: String? = null,
        key2: String? = null,
        keyValue2: String? = null,
        successFun: () -> Unit,
    ) {
        val data =
            getTbaTimeListDataJson(
                context,
                name,
                key1,
                keyValue1,
                key2,
                keyValue2
            )

        Log.e("TAG", "postPotNet--${name}: data=${data}")
        try {
            smileNetManager.postPutData(
                AppData.put_data_url,
                data,
                object : FifNetHelp.Callback {
                    override fun onSuccess(response: String) {
                        Log.e("TAG", "postPotNet--${name}: onSuccess=${response}")
                        successFun()
                    }

                    override fun onFailure(error: String) {
                        Log.e("TAG", "postPotNet--${name}: onFailure=${error}")
//                        val bean = PotIntInfo()
//                        bean.name = name
//                        bean.parameterName = key1
//                        bean.parameterValue = keyValue1
//                        bean.parameterName2 = key2
//                        bean.parameterValue2 = keyValue2
//                        bean.parameterName3 = key3
//                        bean.parameterValue3 = keyValue3
//                        addAnErrorMessage(bean)
                    }
                })
        } catch (e: Exception) {
            Log.e("TAG", "postPotNet--${name}: Exception=${e}")
        }
    }


    fun postPotNet(
        context: Context,
        name: String,
        key1: String? = null,
        keyValue1: String? = null,
        key2: String? = null,
        keyValue2: String? = null
    ) {
        if (key2 == null) {
            postPotNet(context, name, key1, keyValue1) {}
        } else {
            postPotNet(context, name, key1, keyValue1, key2, keyValue2) {}
        }
    }


    fun postSessionData(context: Context) {
        val data = getSessionJson(context)
        Log.e("TAG", "postSessionData: data=${data}")

        smileNetManager.postPutData(
            AppData.put_data_url,
            data,
            object : FifNetHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.e("TAG", "postSessionData: onSuccess=${response}")
                }

                override fun onFailure(error: String) {
                    Log.e("TAG", "postSessionData: onFailure=${error}")

                }
            })
    }

    fun postAdData(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        adInformation: AdInformation
    ) {
        val data = getAdJson(context, adValue, responseInfo, adInformation)
        Log.e("TAG", "${adInformation.name_scan}-postAdData: data=${data}")
        smileNetManager.postPutData(
            AppData.put_data_url,
            data,
            object : FifNetHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.e("TAG", "${adInformation.name_scan}-postAdData: onSuccess=${response}")
                }

                override fun onFailure(error: String) {
                    Log.e("TAG", "${adInformation.name_scan}-postAdData: onFailure=${error}")
                }
            })
        postAdOnline(adValue.valueMicros)
    }

    fun postInstallData(context: Context, rd: ReferrerDetails) {
        if (AppData.isInstall == "1") {
            return
        }
        val data = getInstallJson(rd, context)
        Log.e("TAG", "postInstallData: data=${data}")

        try {
            smileNetManager.postPutData(
                AppData.put_data_url,
                data,
                object : FifNetHelp.Callback {
                    override fun onSuccess(response: String) {
                        AppData.isInstall = "1"
                        Log.e("TAG", "postInstallData: onSuccess=${response}")

                    }

                    override fun onFailure(error: String) {
                        AppData.isInstall = "0"
                        Log.e("TAG", "postInstallData: onFailure=${error}")

                    }
                })
        } catch (e: Exception) {
            AppData.isInstall = "0"
            Log.e("TAG", "postInstallData: Exception=${e}")
        }
    }
}