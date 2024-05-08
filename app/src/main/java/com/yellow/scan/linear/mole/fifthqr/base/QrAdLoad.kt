package com.yellow.scan.linear.mole.fifthqr.base

import android.content.Context
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yellow.scan.linear.mole.fifthqr.bean.AdInformation
import com.yellow.scan.linear.mole.fifthqr.bean.QrAdBean
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity
import com.yellow.scan.linear.mole.fifthqr.ui.result.ActivityResult

import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.AppData.isVisible
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object QrAdLoad {
    var isLoadOpenFist = false
    val TAG = "QrAdLoad"
    var openAdData = AdInformation()

    var backAdData = AdInformation()

    var scanAdData = AdInformation()

    var creteAdData = AdInformation()
    fun init(context: Context) {
        GoogleAds.init(context) {
            preloadAds()
        }
    }

    fun loadOf(where: String) {
        Load.of(where)?.load()
    }

    fun resultOf(where: String): Any? {
        return Load.of(where)?.res
    }

    fun showFullScreenOf(
        where: String,
        context: AppCompatActivity,
        res: Any,
        preload: Boolean = false,
        onShowCompleted: () -> Unit
    ) {
        Show.of(where)
            .showFullScreen(
                context = context,
                res = res,
                callback = {
                    Load.of(where)?.let { load ->
                        load.clearCache()
                        if (preload) {
                            load.load()
                        }
                    }
                    onShowCompleted()
                }
            )
    }

    private fun preloadAds() {
        runCatching {
            Load.of(AppData.QR_OPEN)?.load()
            Load.of(AppData.QR_CLICK_CREATE)?.load()
            Load.of(AppData.QR_CLICK_SCAN)?.load()
            Load.of(AppData.QR_BACK_MAIN)?.load()
        }
    }

    fun setShowingFullScreen(isShowing: Boolean) {
        Show.isShowingFullScreen = isShowing
    }

    private class Load private constructor(private val where: String) {
        companion object {
            private val open by lazy { Load(AppData.QR_OPEN) }
            private val create by lazy { Load(AppData.QR_CLICK_CREATE) }
            private val back by lazy { Load(AppData.QR_BACK_MAIN) }
            private val scan by lazy { Load(AppData.QR_CLICK_SCAN) }

            fun of(where: String): Load? {
                return when (where) {
                    AppData.QR_OPEN -> open
                    AppData.QR_CLICK_CREATE -> create
                    AppData.QR_BACK_MAIN -> back
                    AppData.QR_CLICK_SCAN -> scan
                    else -> null
                }
            }

        }


        private var createdTime = 0L
        var res: Any? = null
            set
        var isLoading = false
            set

        private fun printLog(content: String) {
            Log.d(TAG, "${where} ---${content}: ")
        }

        fun load(
            context: Context = App.getAppContext(),
            requestCount: Int = 1,
            inst: QrAdBean = AppData.getAdJson(),
            isLoadType: Boolean = false
        ) {

            AppData.isAppGreenSameDayGreen()
            if (isLoading) {
                printLog("is requesting")
                return
            }

            val cache = res
            val cacheTime = createdTime
            if (cache != null) {
                if (cacheTime > 0L
                    && ((System.currentTimeMillis() - cacheTime) > (1000L * 50L * 60L))
                ) {
                    printLog("cache is expired")
                    Log.e(TAG, "load: clearCache")
                    clearCache()
                } else {
                    printLog("Existing cache")
                    return
                }
            }
            if ((cache == null || cache == "") && AppData.isThresholdReached()) {
                printLog("The ad reaches the go-live")
//                SmileNetHelp.postPotNet(context, "oom15", "oo", AppData.overrunType())
                res = ""
                return
            }

            if ((where == AppData.QR_BACK_MAIN || where == AppData.QR_CLICK_CREATE || where == AppData.QR_CLICK_SCAN) && !AppData.showAdBlacklist()) {
                res = ""
                return
            }
            isLoading = true
            val listData = when (where) {
                AppData.QR_OPEN -> {
                    inst.open_qr
                }

                AppData.QR_CLICK_CREATE -> inst.crete_qr

                AppData.QR_BACK_MAIN -> inst.back_qr

                AppData.QR_CLICK_SCAN -> inst.scan_qr
                else -> emptyList()
            }
            val redListData = sortArrayByWeight(listData as MutableList)
            printLog("load started-data=${redListData}")
            doRequest(
                context, redListData
            ) {
                val isSuccessful = it != null
                printLog("load complete, result=$isSuccessful")
                if (isSuccessful) {
                    res = it
                    createdTime = System.currentTimeMillis()
                }
                isLoading = false
                if (!isSuccessful && where == AppData.QR_OPEN && requestCount < 2) {
                    load(context, requestCount + 1, inst)
                }
            }
        }

        fun sortArrayByWeight(items: MutableList<AdInformation>): MutableList<AdInformation> {
            val priorityMap = hashMapOf<String, Int>()
            ('a'..'z').forEachIndexed { index, string ->
                priorityMap[string.toString()] = index
                priorityMap[string.toUpperCase().toString()] = index
            }

            items.sortBy { priorityMap[it.we_scan] }

            return items
        }

        private fun doRequest(
            context: Context,
            units: List<AdInformation>,
            startIndex: Int = 0,
            callback: ((result: Any?) -> Unit)
        ) {
            val unit = units.getOrNull(startIndex)
            if (unit == null) {
                callback(null)
                return
            }
            printLog("${where},on request: $unit")
            GoogleAds(where).load(context, unit) {
                if (it == null)
                    doRequest(context, units, startIndex + 1, callback)
                else
                    callback(it)
            }
        }

        fun clearCache() {
            res = null
            createdTime = 0L
        }
    }

    private class Show private constructor(private val where: String) {
        companion object {
            var isShowingFullScreen = false

            fun of(where: String): Show {
                return Show(where)
            }

        }

        fun showFullScreen(
            context: AppCompatActivity,
            res: Any,
            callback: () -> Unit
        ) {
            if (isShowingFullScreen || !context.isVisible()) {
                callback()
                return
            }
            isShowingFullScreen = true
            Log.e(TAG, "showFullScreen: ")
            GoogleAds(where)
                .showFullScreen(
                    context = context,
                    res = res,
                    callback = {
                        isShowingFullScreen = false
                        callback()
                    }
                )
        }
    }

    private class GoogleAds(private val where: String) {
        private class GoogleFullScreenCallback(
            private val where: String,
            private val callback: () -> Unit
        ) : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "${where} ---dismissed")
                onAdComplete()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.d(TAG, "${where} ---fail to show, message=${p0.message}")
                onAdComplete()
            }

            private fun onAdComplete() {
                callback()
            }

            override fun onAdShowedFullScreenContent() {
                AppData.recordNumberOfAdDisplaysGreen()
                Log.d(TAG, "${where}--showed")
                val adBean = when (where) {
                    AppData.QR_OPEN -> openAdData
                    AppData.QR_BACK_MAIN -> backAdData
                    AppData.QR_CLICK_CREATE -> creteAdData
                    AppData.QR_CLICK_SCAN -> scanAdData
                    else -> AdInformation()
                }
                NetHelp.postPotNet(
                    App.getAppContext(),
                    "scan26",
                    "qr",
                    "${adBean.name_scan}+${adBean.id_scan}+${App.top_activity_name}"
                )
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "${where}插屏广告点击")
                AppData.recordNumberOfAdClickGreen()
            }
        }

        companion object {
            fun init(context: Context, onInitialized: () -> Unit) {
                MobileAds.initialize(context) {
                    onInitialized()
                }
            }

        }

        fun load(
            context: Context,
            unit: AdInformation,
            callback: ((result: Any?) -> Unit)
        ) {
            NetHelp.postPotNet(
                context,
                "scan24",
                "qr",
                "${unit.name_scan}+${unit.id_scan}+${App.top_activity_name}"
            )
            val requestContext = context.applicationContext
            when (unit.name_scan) {
                AppData.QR_OPEN -> {
                    openAdData.id_scan = unit.id_scan
                    openAdData.type_scan = unit.type_scan
                    openAdData.name_scan = unit.name_scan
                    AppOpenAd.load(
                        requestContext,
                        unit.id_scan.toString(),
                        AdRequest.Builder().build(),
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                        object :
                            AppOpenAd.AppOpenAdLoadCallback() {
                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.d(TAG, "${where} ---request fail: ${loadAdError.message}")
                                callback(null)
                            }

                            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                                callback(appOpenAd)
                                NetHelp.postPotNet(
                                    context,
                                    "scan25",
                                    "qr",
                                    "${unit.name_scan}+${unit.id_scan}+${App.top_activity_name}"
                                )
                                appOpenAd.setOnPaidEventListener { adValue ->
                                    adValue.let {
                                        NetHelp.postAdData(
                                            App.getAppContext(),
                                            adValue,
                                            appOpenAd.responseInfo,
                                            openAdData
                                        )
                                    }
                                    val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                                    adRevenue.setRevenue(
                                        adValue.valueMicros / 1000000.0,
                                        adValue.currencyCode
                                    )
                                    adRevenue.setAdRevenueNetwork(appOpenAd.responseInfo.mediationAdapterClassName)
                                    Adjust.trackAdRevenue(adRevenue)
                                }
                            }
                        })
                }

                AppData.QR_CLICK_CREATE, AppData.QR_BACK_MAIN, AppData.QR_CLICK_SCAN -> {
                    if (unit.name_scan == AppData.QR_CLICK_CREATE) {
                        creteAdData.id_scan = unit.id_scan
                        creteAdData.type_scan = unit.type_scan
                        creteAdData.name_scan = unit.name_scan
                    }
                    if (unit.name_scan == AppData.QR_BACK_MAIN) {
                        backAdData.id_scan = unit.id_scan
                        backAdData.type_scan = unit.type_scan
                        backAdData.name_scan = unit.name_scan
                    }
                    if (unit.name_scan == AppData.QR_CLICK_SCAN) {
                        scanAdData.id_scan = unit.id_scan
                        scanAdData.type_scan = unit.type_scan
                        scanAdData.name_scan = unit.name_scan
                    }
                    InterstitialAd.load(
                        requestContext,
                        unit.id_scan.toString(),
                        AdRequest.Builder().build(),
                        object : InterstitialAdLoadCallback() {
                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.d(TAG, "${where} ---request fail: ${loadAdError.message}")
                                callback(null)
                            }

                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                NetHelp.postPotNet(
                                    context,
                                    "scan25",
                                    "qr",
                                    "${unit.name_scan}+${unit.id_scan}+${App.top_activity_name}"
                                )
                                callback(interstitialAd)
                                interstitialAd.setOnPaidEventListener { adValue ->
                                    val bean = when (unit.name_scan) {
                                        AppData.QR_CLICK_CREATE -> {
                                            creteAdData
                                        }

                                        AppData.QR_BACK_MAIN -> {
                                            backAdData
                                        }

                                        AppData.QR_CLICK_SCAN -> {
                                            scanAdData
                                        }

                                        else -> {
                                            null
                                        }
                                    }
                                    adValue.let {
                                        bean?.let { it1 ->
                                            NetHelp.postAdData(
                                                App.getAppContext(),
                                                adValue,
                                                interstitialAd.responseInfo,
                                                it1
                                            )
                                        }
                                        val adRevenue =
                                            AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                                        adRevenue.setRevenue(
                                            adValue.valueMicros / 1000000.0,
                                            adValue.currencyCode
                                        )
                                        adRevenue.setAdRevenueNetwork(interstitialAd.responseInfo.mediationAdapterClassName)
                                        Adjust.trackAdRevenue(adRevenue)
                                    }
                                }
                            }
                        }
                    )
                }

                else -> {
                    callback(null)
                }
            }
        }

        fun showFullScreen(
            context: AppCompatActivity,
            res: Any,
            callback: () -> Unit
        ) {
            when (res) {
                is AppOpenAd -> {
                    res.fullScreenContentCallback = GoogleFullScreenCallback(where, callback)
                    res.show(context)
                }

                is InterstitialAd -> {

                    if (!AppData.showAdBlacklist()) {
                        callback.invoke()
                        return
                    }
                    context.lifecycleScope.launch(Dispatchers.Main) {
                        res.fullScreenContentCallback = GoogleFullScreenCallback(where, callback)
                        res.show(context)
                    }
                }

                else -> callback()
            }
        }
    }
}