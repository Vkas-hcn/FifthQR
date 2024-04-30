package com.yellow.scan.linear.mole.fifthqr.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
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
                top_activity_fif = activity

            }

            override fun onActivityStarted(activity: Activity) {
                top_activity_fif = activity
                top_activity_name = activity.javaClass.simpleName

                flag++
                isBackDataSmile = false
            }

            override fun onActivityResumed(activity: Activity) {
                top_activity_fif = activity
            }

            override fun onActivityPaused(activity: Activity) {
                top_activity_fif = activity
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
            if (top_activity_fif is FirstActivity) {
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
        GlobalScope.launch(Dispatchers.IO) {
            NetHelp.getBlackData(this@App)
        }
    }
//    fun getGid(context: Context) {
//        GlobalScope.launch(Dispatchers.IO) {
//            runCatching {
//                AppData.gidData = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: ""
//            }
//        }
//    }

}