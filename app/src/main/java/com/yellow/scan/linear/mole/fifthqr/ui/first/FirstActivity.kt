package com.yellow.scan.linear.mole.fifthqr.ui.first

import android.content.Intent
import android.util.Log
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.yellow.scan.linear.mole.fifthqr.BuildConfig
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.base.QrAdLoad
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityFirstBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope


import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class FirstActivity : BaseActivity<ActivityFirstBinding, BaseViewModel>(
    R.layout.activity_first, BaseViewModel::class.java
) {
    private var jumpToMain = MutableLiveData(false)
    private var jobFirst: Job? = null
    private var jobOpenQr: Job? = null
    val showOpen = MutableLiveData<Any>()
    private lateinit var consentInformation: ConsentInformation
    override fun intiView() {
        updateUserOpinions()
        jumpToMain.observe(this) {
            if (it) {
                startActivityFirst<MainActivity>()
                finish()
            }
        }
    }

    var count = 0
    override fun initData() {
        showOpenFun()
        getFileBaseData {
            QrAdLoad.isLoadOpenFist = false
            QrAdLoad.init(this)
            waitForTheOpenAdToAppear()
        }
        onBackPressedDispatcher.addCallback(this) {
        }
        NetHelp.postSessionData(this@FirstActivity)
    }

    override fun onResume() {
        super.onResume()
        countDown()
    }

    private fun countDown() {
        jobFirst?.cancel()
        jobFirst = null
        jobFirst = lifecycleScope.launch {
            delay(200)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            while (isActive) {
                count += 1
                binding.tvProgress.text = "${count}%"
                if (count >= 99) {
                    jobFirst?.cancel()
                    jobFirst = null
                    break
                }
                delay(140)
            }
        }
    }

    private fun getFileBaseData(loadAdFun: () -> Unit) {
        lifecycleScope.launch {
            var isCa = false
            if (!BuildConfig.DEBUG) {
                val auth = Firebase.remoteConfig
                auth.fetchAndActivate().addOnSuccessListener {
                    AppData.online_qr_ad_data = auth.getString(AppData.online_qr_ad)
                    AppData.online_qr_config_data = auth.getString(AppData.online_qr_config)
                    isCa = true
                }
            }
            try {
                withTimeout(4000L) {
                    while (isActive) {
                        if (isCa) {
                            loadAdFun()
                            cancel()
                        }
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                loadAdFun()
            }
        }
    }

    private fun loadOpenAd() {
        AppData.isAppGreenSameDayGreen()
        if (AppData.isThresholdReached()) {
            Log.d("TAG", "The ad reaches the go-live")
            jumpToMain.postValue(true)
            return
        }
        jobOpenQr?.cancel()
        jobOpenQr = null
        jobOpenQr = lifecycleScope.launch {
            try {
                withTimeout(10000L) {
                    while (isActive) {
                        QrAdLoad.resultOf(AppData.QR_OPEN)?.let { res ->
                            showOpen.value = res
                            cancel()
                            jobOpenQr = null
                        }
                        delay(500L)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                jobOpenQr = null
                jumpToMain.postValue(true)
            }
        }
    }

    private fun showOpenFun() {
        showOpen.observe(this) {
            QrAdLoad.showFullScreenOf(
                where = AppData.QR_OPEN,
                context = this,
                res = it,
                preload = true,
                onShowCompleted = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        jumpToMain.postValue(true)
                    }
                }
            )
        }
    }
    private fun waitForTheOpenAdToAppear() {
        GlobalScope.launch {
            while (isActive) {
                if (AppData.ump_data_dialog) {
                    loadOpenAd()
                    cancel()
                }
                delay(500)
            }
        }
    }

    private fun updateUserOpinions() {
        if (AppData.ump_data_dialog) {
            return
        }

        val debugSettings =
            ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("437866A57B0FAD333A37E294AF07BB1D")
                .build()
        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params, {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { loadAndShowError ->
                    if (consentInformation.canRequestAds()) {
                        AppData.ump_data_dialog = true
                    }
                }
            },
            {
                AppData.ump_data_dialog = true
            }
        )
    }
}