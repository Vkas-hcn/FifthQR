package com.yellow.scan.linear.mole.fifthqr.ui.result

import android.app.Activity
import android.content.Intent
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.base.QrAdLoad
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityFirstBinding
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityResultBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.model.ResultViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ActivityResult : BaseActivity<ActivityResultBinding, ResultViewModel>(
    R.layout.activity_result, ResultViewModel::class.java
) {
    var resultString = ""
    private var loadJob: Job? = null
    private var showBackAd = MutableLiveData<Any>()
    override fun intiView() {
        resultString = intent.getStringExtra("result_qr") ?: ""
        QrAdLoad.loadOf(AppData.QR_BACK_MAIN)
        showBackFun {
            finish()
        }
        NetHelp.postPotNet(App.getAppContext(), "scan5")
    }

    override fun initData() {
        binding.tvResult.text = resultString
        binding.imgBack.setOnClickListener {
            backFun()
            NetHelp.postPotNet(App.getAppContext(), "scan13")
        }
        binding.imgCopy.setOnClickListener {
            viewModel.copyTextToClipboard(this, resultString)
            NetHelp.postPotNet(App.getAppContext(), "scan11")
        }
        binding.imgSearch.setOnClickListener {
            viewModel.searchInBrowser(this, resultString)
            NetHelp.postPotNet(App.getAppContext(), "scan12")
        }
        binding.tvContinue.setOnClickListener {
            backFun()
            NetHelp.postPotNet(App.getAppContext(), "scan14")
        }
        binding.tvGenerateQr.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
            })
            backFun()
            NetHelp.postPotNet(App.getAppContext(), "scan15")
        }
        onBackPressedDispatcher.addCallback(this) {
            backFun()
            NetHelp.postPotNet(App.getAppContext(), "scan13")
        }
    }

    private fun backFun() {
        startAdLoad({
            showBackAd.value = it
        }, {
            finish()
        })
    }

    private fun startAdLoad(nextFun: (data: Any) -> Unit, timeOutFun: () -> Unit) {
        loadJob?.cancel()
        loadJob = null
        loadJob = lifecycleScope.launch(Dispatchers.Main) {
            try {
                withTimeout(5000L) {
                    binding.haveLoad = true
                    while (isActive) {
                        QrAdLoad.resultOf(AppData.QR_BACK_MAIN)?.let { res ->
                            loadJob?.cancel()
                            loadJob = null
                            binding.haveLoad = false
                            nextFun(res)
                        }
                        delay(500L)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                loadJob?.cancel()
                loadJob = null
                binding.haveLoad = false
                timeOutFun()
            }
        }
    }


    private fun showBackFun(nextFun: () -> Unit) {
        showBackAd.observe(this) {
            QrAdLoad.showFullScreenOf(
                where = AppData.QR_BACK_MAIN,
                context = this,
                res = it,
                preload = true,
                onShowCompleted = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        nextFun()
                    }
                }
            )
        }
    }
}