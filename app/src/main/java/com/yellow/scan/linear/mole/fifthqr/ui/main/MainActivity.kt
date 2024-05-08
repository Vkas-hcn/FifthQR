package com.yellow.scan.linear.mole.fifthqr.ui.main

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityMainBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.create.CreateFragment
import com.yellow.scan.linear.mole.fifthqr.ui.more.MoreFragment
import com.yellow.scan.linear.mole.fifthqr.ui.result.ActivityResult
import com.yellow.scan.linear.mole.fifthqr.utils.CameraPermissionManager
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CodeUtils
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CodeUtils.analyzeBitmap
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.base.QrAdLoad
import com.yellow.scan.linear.mole.fifthqr.ui.scan.ScanFragment
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CaptureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>(
    R.layout.activity_main, BaseViewModel::class.java
) {
    private lateinit var cameraPermissionManager: CameraPermissionManager
    lateinit var fragmentScan: ScanFragment
    lateinit var fragmentCrete: CreateFragment
    lateinit var fragmentMore: MoreFragment
    lateinit var imagePicker: ImagePicker
    lateinit var flashlightManager: FlashlightManager
    private var loadJob: Job? = null
    private var showCreteAd = MutableLiveData<Any>()
    private var showScanAd = MutableLiveData<Any>()
    private var showBackAd = MutableLiveData<Any>()

    var result = ""
    private var isClickType = 1 // 1: scan, 2: create, 3: more
    override fun intiView() {
        cameraPermissionManager = CameraPermissionManager(this)
        cameraPermissionManager.checkCameraPermission(this)
        imagePicker = ImagePicker(this)
        flashlightManager = FlashlightManager(this)

        binding.tvShare.setOnClickListener {
            shareApp(this)
        }
        binding.tvRate.setOnClickListener {
            openAppInPlayStore(this)
        }
        binding.tvPrivacy.setOnClickListener {
            val webUri = Uri.parse(AppData.pp_url)
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
        binding.imgSetting.setOnClickListener {
            binding.drawerMain.open()
            NetHelp.postPotNet(App.getAppContext(), "scan4")
        }

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                toCreteFragment()
            }
        }

    override fun initData() {
        fragmentScan = ScanFragment()
        fragmentCrete = CreateFragment()
        fragmentMore = MoreFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frag_main, fragmentScan).commit()
        binding.imgScan.setOnClickListener {
            lifecycleScope.launch {
                if (binding.haveLoad == true) {
                    return@launch
                }
                QrAdLoad.loadOf(AppData.QR_CLICK_SCAN)
                if (!cameraPermissionManager.checkCameraPermission(this@MainActivity)) {
                    return@launch
                }
                if (QrAdLoad.resultOf(AppData.QR_BACK_MAIN) == "") {
                    binding.haveLoad = true
                    binding.tvLoading.text = "Loading..."
                    delay(1000)
                    binding.tvLoading.text = "Ad Loading..."
                    binding.haveLoad = false
                    toScanFragment()
                    return@launch
                }
                showBackAdFun {
                    toScanFragment()
                }
                isClickType = 1
            }
        }
        binding.atvCreate.setOnClickListener {
            isClickType = 2
            toCreteFragment()
        }
        binding.atvMore.setOnClickListener {
            showBackAdFun {
                toMoreFragment()
            }
            isClickType = 3
        }
        showScanFun {
            jumToResultPage()
        }
        showCreteFun {
            fragmentCrete.jumToMidActivity()
        }
        showBackFun {
            if (isClickType == 1) {
                toScanFragment()
            }
            if (isClickType == 3) {
                toMoreFragment()
            }
        }
    }


    private fun loadFragment(fragment: Fragment) {
        lifecycleScope.launch {
            supportFragmentManager.beginTransaction().replace(R.id.frag_main, fragment).commitNow()
        }
    }

    private fun toCreteFragment() {
        loadFragment(fragmentCrete)
        binding.atvCreate.setTextColor(resources.getColor(R.color.setele_color))
        binding.atvMore.setTextColor(resources.getColor(R.color.dis_setele_color))
    }

    private fun toScanFragment() {
        loadFragment(fragmentScan)
        binding.atvCreate.setTextColor(resources.getColor(R.color.dis_setele_color))
        binding.atvMore.setTextColor(resources.getColor(R.color.dis_setele_color))
    }

    private fun toMoreFragment() {
        loadFragment(fragmentMore)
        binding.atvCreate.setTextColor(resources.getColor(R.color.dis_setele_color))
        binding.atvMore.setTextColor(resources.getColor(R.color.setele_color))
    }

    fun shareApp(context: Context) {
        val shareText = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun openAppInPlayStore(context: Context) {
        val packageName = context.packageName
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        }
    }


    private fun startAdLoad(adType: String, nextFun: (data: Any) -> Unit, timeOutFun: () -> Unit) {
        loadJob?.cancel()
        loadJob = null
        loadJob = lifecycleScope.launch(Dispatchers.Main) {
            try {
                withTimeout(5000L) {
                    while (isActive) {
                        QrAdLoad.resultOf(adType)?.let { res ->
                            loadJob?.cancel()
                            loadJob = null
                            nextFun(res)
                        }
                        delay(500L)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                loadJob?.cancel()
                loadJob = null
                timeOutFun()
            }
        }
    }


    private fun showCreteFun(nextFun: () -> Unit) {
        showCreteAd.observe(this) {
            QrAdLoad.showFullScreenOf(
                where = AppData.QR_CLICK_CREATE,
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

    private fun showScanFun(nextFun: () -> Unit) {
        showScanAd.observe(this) {
            QrAdLoad.showFullScreenOf(
                where = AppData.QR_CLICK_SCAN,
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

    private fun showBackAdFun(nextFun: () -> Unit) {
        if (isClickType == 2) {
            startAdLoad(AppData.QR_BACK_MAIN, {
                showBackAd.value = it
            }, {
                nextFun()
            })
            NetHelp.postPotNet(App.getAppContext(), "scan16")
        } else {
            nextFun()
        }
    }

    fun showCreteAdFun() {
        startAdLoad(AppData.QR_CLICK_CREATE, {
            showCreteAd.value = it
        }, {
            fragmentCrete.jumToMidActivity()
        })
    }

    fun showScanAdFun() {
        NetHelp.postPotNet(App.getAppContext(), "scan8")
        startAdLoad(AppData.QR_CLICK_SCAN, {
            showScanAd.value = it
        }, {
            jumToResultPage()
        })
    }

    private fun jumToResultPage() {
        val intent = Intent(this, ActivityResult::class.java)
        val bundle = Bundle()
        bundle.putString("result_qr", result)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraPermissionManager.handleCameraPermissionResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(this, requestCode, resultCode, data)

    }
}

class ImagePicker(private val activity: AppCompatActivity) {
    companion object {
        private const val REQUEST_IMAGE_PICKER = 1001
    }

    private var imagePickerCallback: ((Uri?) -> Unit)? = null

    fun pickImage(callback: (Uri?) -> Unit) {
        App.whetherBackgroundSmild = false
        imagePickerCallback = callback
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    fun onActivityResult(activity: MainActivity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            App.whetherBackgroundSmild = false
            val selectedImageUri = data?.data
            imagePickerCallback?.invoke(selectedImageUri)
        }
    }

    fun setImageUri(activity: MainActivity, selectedImageUri: Uri) {
        val cr = activity.contentResolver
        try {
            val mBitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri)
            analyzeBitmap(mBitmap, object : CodeUtils.AnalyzeCallback {
                override fun onAnalyzeSuccess(mBitmap: Bitmap?, resultData: String?) {
                    if (resultData.isNullOrEmpty()) {
                        Toast.makeText(
                            activity, "Identification failed, please try again", Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    activity.result = resultData
                    activity.showScanAdFun()
                }

                override fun onAnalyzeFailed() {
                    Toast.makeText(
                        activity, "Identification failed, please try again", Toast.LENGTH_LONG
                    ).show()
                }
            })
            mBitmap?.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //show ad load


}


class FlashlightManager(private val context: Context) {
    private var isFlashlightOn: Boolean = false
    fun toggleFlashlight(): Boolean {
        isFlashlightOn = !isFlashlightOn
        return try {
            CodeUtils.isLightEnable(isFlashlightOn)
            isFlashlightOn
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isFlashlightOn(): Boolean {
        return isFlashlightOn
    }
}
