package com.yellow.scan.linear.mole.fifthqr.ui.result

import android.app.Activity
import android.content.Intent
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityFirstBinding
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityResultBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.model.ResultViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity

class ActivityResult: BaseActivity<ActivityResultBinding, ResultViewModel>(
    R.layout.activity_result, ResultViewModel::class.java
)  {
    var resultString =""
    override fun intiView() {
        resultString = intent.getStringExtra("result_qr")?:""
    }

    override fun initData() {
        binding.tvResult.text = resultString
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgCopy.setOnClickListener {
            viewModel.copyTextToClipboard(this,resultString)
        }
        binding.imgSearch.setOnClickListener {
            viewModel.searchInBrowser(this,resultString)
        }
        binding.tvContinue.setOnClickListener {
            finish()
        }
        binding.tvGenerateQr.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
            })
            finish()
        }
    }
}