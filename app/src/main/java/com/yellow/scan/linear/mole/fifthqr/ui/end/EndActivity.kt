package com.yellow.scan.linear.mole.fifthqr.ui.end

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityEndBinding
import com.yellow.scan.linear.mole.fifthqr.model.EndViewModel
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CodeUtils

class EndActivity : BaseActivity<ActivityEndBinding, EndViewModel>(
    R.layout.activity_end, EndViewModel::class.java
) {
    var endQr = ""
    override fun intiView() {
        endQr = intent.getStringExtra("end_qr") ?: ""
        binding.imgBack.setOnClickListener {
            backFun()
        }
        binding.tvRebuild.setOnClickListener {
            NetHelp.postPotNet(App.getAppContext(), "scan22")
            finish()
        }
        onBackPressedDispatcher.addCallback(this) {
            backFun()
        }
        NetHelp.postPotNet(App.getAppContext(), "scan7")
    }
    private fun backFun() {
        NetHelp.postPotNet(App.getAppContext(), "scan21")
        finish()
    }
    override fun initData() {
        if (AppData.bitmapQr != null) {
            binding.imgQrcode.setImageBitmap(AppData.bitmapQr)
        } else {
            binding.imgQrcode.setImageBitmap(CodeUtils.createImage(endQr, 142, 142))
        }
        binding.tvDetails.text = endQr
    }
}
