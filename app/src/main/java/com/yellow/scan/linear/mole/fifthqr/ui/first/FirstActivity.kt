package com.yellow.scan.linear.mole.fifthqr.ui.first

import android.content.Intent
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityFirstBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity


import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstActivity : BaseActivity<ActivityFirstBinding, BaseViewModel>(
    R.layout.activity_first, BaseViewModel::class.java
) {
    private var jumpToMain = MutableLiveData(false)
    private var jobFirst:Job?=null
    override fun intiView() {
        jumpToMain.observe(this) {
            if (it) {
                startActivityFirst<MainActivity>()
                finish()
            }
        }
    }

    var count = 0
    override fun initData() {
        onBackPressedDispatcher.addCallback(this) {
        }
    }

    override fun onResume() {
        super.onResume()
        countDown()
    }
    private fun countDown() {
        jobFirst?.cancel()
        jobFirst= null
        jobFirst= lifecycleScope.launch {
            delay(200)
            if(lifecycle.currentState !=Lifecycle.State.RESUMED){
                return@launch
            }
            while (true) {
                count += 1
                binding.tvProgress.text = "${count}%"
                if (count >= 100) {
                    jobFirst?.cancel()
                    jobFirst= null
                    jumpToMain.postValue(true)
                    break
                }
                delay(20)
            }
        }
    }


}