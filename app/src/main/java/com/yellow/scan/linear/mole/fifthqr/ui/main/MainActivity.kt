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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.ui.scan.ScanFragment
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CaptureFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>(
    R.layout.activity_main, BaseViewModel::class.java
) {
    private lateinit var cameraPermissionManager: CameraPermissionManager

        lateinit var fragmentScan: ScanFragment
//    lateinit var fragmentScan: CaptureFragment
    lateinit var fragmentCrete: CreateFragment
    lateinit var fragmentMore: MoreFragment
    lateinit var imagePicker: ImagePicker
    lateinit var flashlightManager: FlashlightManager

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
            if(binding.haveLoad==true){return@setOnClickListener}
            cameraPermissionManager.checkCameraPermission(this)
            loadFragment(fragmentScan)
            binding.atvMore.setTextColor(resources.getColor(R.color.dis_setele_color))
            binding.atvCreate.setTextColor(resources.getColor(R.color.dis_setele_color))
        }
        binding.atvCreate.setOnClickListener {
            loadFragment(fragmentCrete)
            binding.atvCreate.setTextColor(resources.getColor(R.color.setele_color))
            binding.atvMore.setTextColor(resources.getColor(R.color.dis_setele_color))
        }
        binding.atvMore.setOnClickListener {
            loadFragment(fragmentMore)
            binding.atvCreate.setTextColor(resources.getColor(R.color.dis_setele_color))
            binding.atvMore.setTextColor(resources.getColor(R.color.setele_color))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        lifecycleScope.launch {
            if(fragment is ScanFragment){
                binding.haveLoad = true
                delay(1000)
                binding.haveLoad = false
            }
            supportFragmentManager.beginTransaction().replace(R.id.frag_main, fragment).commitNow()
        }
    }

    private fun toCreteFragment() {
        loadFragment(fragmentCrete)
        binding.atvCreate.setTextColor(resources.getColor(R.color.setele_color))
        binding.atvMore.setTextColor(resources.getColor(R.color.dis_setele_color))
    }

    fun shareApp(context: Context) {
        val shareText = "https://play.google.com/store/apps/details?id=${context.packageName}"

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)

        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun openAppInPlayStore(context: Context) {
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

    fun jumToResultPage(result: String) {
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

    // 启动图片选择器
    fun pickImage(callback: (Uri?) -> Unit) {
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
                override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
                    if (result.isNullOrEmpty()) {
                        Toast.makeText(
                            activity, "Identification failed, please try again", Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    activity.jumToResultPage(result)
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
