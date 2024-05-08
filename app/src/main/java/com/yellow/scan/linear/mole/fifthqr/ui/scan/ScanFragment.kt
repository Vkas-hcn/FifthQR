package com.yellow.scan.linear.mole.fifthqr.ui.scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.databinding.FragmentLayoutScanBinding
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CaptureFragment
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CodeUtils


class ScanFragment : Fragment() {
    private lateinit var binding: FragmentLayoutScanBinding
    private var captureFragment: CaptureFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layout_scan, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickFunction()
        setQrScan()
        NetHelp.postPotNet(App.getAppContext(), "scan1")
    }

    override fun onResume() {
        super.onResume()
        startCameraPreview()
    }

    override fun onStop() {
        super.onStop()
        CodeUtils.isLightEnable(false)
        updateFlashlightIcon(false)
    }
    private fun clickFunction() {
        binding.imgImage.setOnClickListener {
            (activity as MainActivity).imagePicker.pickImage { selectedImageUri ->
                selectedImageUri?.let {
                    (activity as MainActivity).imagePicker.setImageUri(activity = activity as MainActivity, selectedImageUri = it)
                }
            }
            NetHelp.postPotNet(App.getAppContext(), "scan9")
        }
        binding.imgFlash.setOnClickListener {
            (activity as MainActivity).flashlightManager.apply {
                val isFlashlightOn = toggleFlashlight()
                updateFlashlightIcon(isFlashlightOn)
            }
            NetHelp.postPotNet(App.getAppContext(), "scan10")
        }
    }

    private fun updateFlashlightIcon(isFlashlightOn: Boolean) {
        if (isFlashlightOn) {
            binding.imgFlash.setImageResource(R.drawable.ic_flashlight)
        } else {
            binding.imgFlash.setImageResource(R.drawable.ic_flashlight_dis)
        }
    }

    private fun setQrScan() {
        val analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
            override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
                if (result.isNullOrEmpty()) {
                    Toast.makeText(
                        activity, "Identification failed, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                (activity as MainActivity).result = result
                (activity as MainActivity).showScanAdFun()
            }

            override fun onAnalyzeFailed() {
                Toast.makeText(
                    activity, "Identification failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        captureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment!!, R.layout.auto_qr)
        captureFragment!!.analyzeCallback = analyzeCallback
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fl_my_container, captureFragment!!)?.commit()
    }

    private fun startCameraPreview() {
        captureFragment?.let {
            activity?.supportFragmentManager?.beginTransaction()?.remove(it)?.commitAllowingStateLoss()
            captureFragment = null
        }
        setQrScan()
    }
    fun stopCameraPreview(){
        captureFragment?.let {
            activity?.supportFragmentManager?.beginTransaction()?.remove(it)?.commitAllowingStateLoss()
            captureFragment = null
        }
    }
}

