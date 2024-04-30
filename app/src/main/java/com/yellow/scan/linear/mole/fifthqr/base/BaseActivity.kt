package com.yellow.scan.linear.mole.fifthqr.base


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<B : ViewDataBinding, VM : ViewModel>(
    private val layoutId: Int,
    private val viewModelClass: Class<VM>
) : AppCompatActivity() {

    lateinit var binding: B
        private set

    protected val viewModel: VM by lazy {
        ViewModelProvider(this).get(viewModelClass)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        intiView()
        initData()
    }

    abstract fun intiView()
    abstract fun initData()

    inline fun <reified T : AppCompatActivity> startActivityFirst() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }

    inline fun <reified T : AppCompatActivity> startActivityWithParamsFirst(params: Bundle) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(params)
        startActivity(intent)
    }

    inline fun <reified T : AppCompatActivity> startActivityWithReFirst(params: Bundle?=null,code:Int) {
        val intent = Intent(this, T::class.java)
        if (params != null) {
            intent.putExtras(params)
        }
        startActivityForResult(intent,code)
    }
     private fun hideStatusBarBase() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = window.insetsController
            controller?.hide(WindowInsetsCompat.Type.statusBars())
            return
        }
         window.decorView.systemUiVisibility = (
                 View.SYSTEM_UI_FLAG_IMMERSIVE
                         or View.SYSTEM_UI_FLAG_FULLSCREEN
                         or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                         or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                         or View.SYSTEM_UI_FLAG_LOW_PROFILE
                         or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                 )
    }
}