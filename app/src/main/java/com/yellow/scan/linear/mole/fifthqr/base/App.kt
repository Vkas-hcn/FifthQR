package com.yellow.scan.linear.mole.fifthqr.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAttribution
import com.adjust.sdk.AdjustConfig
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.yellow.scan.linear.mole.fifthqr.base.QrAdLoad.TAG
import com.yellow.scan.linear.mole.fifthqr.ui.first.FirstActivity
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class App : Application(), LifecycleObserver {
    companion object {
        var ad_activity_fif: Activity? = null
        var top_activity_fif: Activity? = null
        var top_activity_name: String? = null
        private lateinit var instance: App
        fun getAppContext() = instance
        var isBackDataSmile = false
        var isBoot = false
        var whetherBackgroundSmild = false
        var isAppRunning = false
    }

    var flag = 0
    var job_fif: Job? = null
    override fun onCreate() {
        super.onCreate()
        iniApp()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        appOnStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStopState() {
        appOnStop()
    }

    private fun setActivityLifecycleSmart(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity !is AdActivity) {
                    top_activity_fif = activity
                } else {
                    ad_activity_fif = activity
                }
            }

            override fun onActivityStarted(activity: Activity) {
                if (activity !is AdActivity) {
                    top_activity_fif = activity
                    top_activity_name = activity.javaClass.simpleName
                } else {
                    ad_activity_fif = activity
                }
                flag++
                isBackDataSmile = false
            }

            override fun onActivityResumed(activity: Activity) {
                Adjust.onResume()
                if (activity !is AdActivity) {
                    top_activity_fif = activity
                }
            }

            override fun onActivityPaused(activity: Activity) {
                Adjust.onPause()
                if (activity is AdActivity) {
                    ad_activity_fif = activity
                } else {
                    top_activity_fif = activity
                }
            }

            override fun onActivityStopped(activity: Activity) {
                flag--
                if (flag == 0) {
                    isBackDataSmile = true
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                ad_activity_fif = null
                top_activity_fif = null

            }
        })
    }

    private fun appOnStart() {
        job_fif?.cancel()
        job_fif = null
        if (whetherBackgroundSmild && !isBackDataSmile) {
            isBoot = false
            whetherBackgroundSmild = false
            val intent = Intent(top_activity_fif, FirstActivity::class.java)
            top_activity_fif?.startActivity(intent)
            isAppRunning = true
        }
    }

    private fun appOnStop() {
        job_fif = GlobalScope.launch {
            whetherBackgroundSmild = false
            delay(3000L)
            whetherBackgroundSmild = true
            ad_activity_fif?.finish()
            QrAdLoad.setShowingFullScreen(false)
            Log.d(TAG, "appOnStop: 1", )
            if (top_activity_fif is FirstActivity) {
                Log.d(TAG, "appOnStop: 2", )
                top_activity_fif?.finish()
            }
        }
    }


    private fun iniApp() {
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        setActivityLifecycleSmart(this)
        val data = AppData.uuid_fif
        if (data.isEmpty()) {
            AppData.uuid_fif = UUID.randomUUID().toString()
        }
        initAdJust(this)
        getGid(this)
        GlobalScope.launch(Dispatchers.IO) {
            NetHelp.getBlackData(this@App)
            getReferrerData(this@App)
        }
    }

    private fun initAdJust(application: Application) {
        val sessionId = "customer_user_id"
        val userId = AppData.uuid_fif
        Adjust.addSessionCallbackParameter(sessionId, userId)
        val appToken = "ih2pm2dr3k74"
        val environment = AdjustConfig.ENVIRONMENT_SANDBOX
        val config = createAdjustConfig(application, appToken, environment)
        config.needsCost = true
        setAttributionChangeListener(config)
        Adjust.onCreate(config)
    }

    private fun createAdjustConfig(application: Application, appToken: String, environment: String): AdjustConfig {
        return AdjustConfig(application, appToken, environment)
    }

    private fun setAttributionChangeListener(config: AdjustConfig) {
        config.setOnAttributionChangedListener { attribution ->
            handleAttributionChange(attribution)
        }
    }

    private fun handleAttributionChange(attribution: AdjustAttribution) {
        val emptyAdjustSmile = AppData.adjust_fif.isEmpty()
        val networkNotEmpty = attribution.network.isNotEmpty()
        val isNotOrganic = !attribution.network.contains("organic", true)

        if (emptyAdjustSmile && networkNotEmpty && isNotOrganic) {
            AppData.adjust_fif = attribution.network
            Log.e("TAG","adjust --data=${attribution}")
        }
    }
    fun getGid(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                AppData.gidData = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: ""
            }
        }
    }
    private fun getReferrerData(context: Context) {
        var installReferrer = ""
        val referrer = AppData.local_ref
        if (referrer.isNotBlank() && AppData.isInstall == "1") {
            return
        }
//        installReferrer = "facebook"
//        installReferrer = "utm_source=(not%20set)&utm_medium=(not%20set)"
//        AppData.local_ref = installReferrer
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    when (p0) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer =
                                referrerClient.installReferrer.installReferrer ?: ""
                            AppData.local_ref = installReferrer
                            referrerClient.installReferrer?.run {
                                NetHelp.postInstallData(getAppContext(),this)
                            }
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }.onFailure { e ->
        }
    }
}