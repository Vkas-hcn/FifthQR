package com.yellow.scan.linear.mole.fifthqr.ui.mid

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.base.BaseActivity
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityEndBinding
import com.yellow.scan.linear.mole.fifthqr.databinding.ActivityMidBinding
import com.yellow.scan.linear.mole.fifthqr.model.BaseViewModel
import com.yellow.scan.linear.mole.fifthqr.model.EndViewModel
import com.yellow.scan.linear.mole.fifthqr.ui.end.EndActivity
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.zxing.activity.CodeUtils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

class MidActivity : BaseActivity<ActivityMidBinding, BaseViewModel>(
    R.layout.activity_mid, BaseViewModel::class.java
) {
    var endQr = ""
    lateinit var bgAdapter: BackgroundListAdapter
    lateinit var colorAdapter: ColorListAdapter
    private val REQUEST_PERMISSION_CODE = 125433

    override fun intiView() {
        endQr = intent.getStringExtra("mid_qr") ?: ""
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.tvBackground.setOnClickListener {
            binding.haveColor = false
        }
        binding.tvColor.setOnClickListener {
            binding.haveColor = true
        }
        binding.tvNext.setOnClickListener {
            if (hasWritePermission()) {
                downloadAndSaveImage()
            } else {
                requestWritePermission()
            }

        }
    }

    override fun initData() {
        binding.imgQrBg.setImageResource(AppData.getBgDataList()[AppData.list_pos])
        binding.imgQrcode.setImageBitmap(CodeUtils.createImage(endQr, 142, 142))
        setColorAdapter()
        setBackgroundAdapter()
    }

    private fun setBackgroundAdapter() {
        bgAdapter = BackgroundListAdapter()
        binding.recyclerBackground.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerBackground.adapter = bgAdapter
        bgAdapter.setOnItemClickListener(object : BackgroundListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                binding.imgQrBg.setImageResource(AppData.getBgDataList()[position])
            }
        })
    }

    private fun setColorAdapter() {
        colorAdapter = ColorListAdapter()
        binding.recyclerColor.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerColor.adapter = colorAdapter
        colorAdapter.setOnItemClickListener(object : ColorListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                binding.imgQrcode.setImageBitmap(
                    CodeUtils.createImage(
                        endQr,
                        142,
                        142,
                        ContextCompat.getColor(App.getAppContext(), AppData.colorsArray[position])
                    )
                )
            }
        })
    }

    private fun hasWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadAndSaveImage()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showPermissionDeniedDialog() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setMessage("To save the image to the gallery, go to the settings page to grant permission.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    this,
                    "There is no storage permission to save the picture.",
                    Toast.LENGTH_SHORT
                ).show()
            }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    fun creteBitMap() {
        val viewData = binding.cardQrcode
        viewModel.convertCardViewToBitmap(viewData) { bitmap ->
            AppData.bitmapQr = bitmap
        }

    }

    fun jumpToNext() {
        val intent = Intent(this, EndActivity::class.java)
        intent.putExtra("end_qr", endQr)
        startActivity(intent)
        finish()
    }

    private fun downloadAndSaveImage() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.haveLoad = true
                creteBitMap()
                delay(2000)
                binding.haveLoad = false
                jumpToNext()
            }
            val savedUri =
                AppData.bitmapQr?.let { saveImageToGallery(it, "qrcode_${System.currentTimeMillis()}", "") }
            if (savedUri != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MidActivity,
                        "The picture has been saved to the gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MidActivity,
                        "Failed to save the image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, title: String, description: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, title)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DESCRIPTION, description)
        }

        val contentResolver: ContentResolver = contentResolver
        val imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let {
            val outputStream: OutputStream? = contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                return imageUri
            }
        }
        return null
    }

}

class BackgroundListAdapter : RecyclerView.Adapter<BackgroundListAdapter.ViewHolder>() {
    private var data = AppData.getIcDataList()
    var selectedItemPositionFun: Int = AppData.list_pos

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bg, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgQrBg.setImageResource(data[position])
        holder.isSelected = position == selectedItemPositionFun
        holder.imgCheek.visibility = if (holder.isSelected) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (!holder.isSelected) {
                setSelectedItemPosition(holder.adapterPosition)
            }
        }
    }

    private fun setSelectedItemPosition(position: Int) {
        val oldPosition = selectedItemPositionFun
        selectedItemPositionFun = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedItemPositionFun)
        listener?.onItemClick(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgQrBg: ImageView = itemView.findViewById(R.id.img_qr_bg)
        val imgCheek: ImageView = itemView.findViewById(R.id.img_cheek)
        var isSelected: Boolean = false
    }


    override fun getItemCount(): Int {
        return data.size
    }

}

class ColorListAdapter : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {
    private var data = AppData.colorsArray
    var selectedItemPositionFun: Int = 4

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.llColor.background = ContextCompat.getDrawable(App.getAppContext(), data[position])
        holder.isSelected = position == selectedItemPositionFun
        holder.imgCheek.visibility = if (holder.isSelected) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (!holder.isSelected) {
                setSelectedItemPosition(holder.adapterPosition)
            }
        }
    }

    private fun setSelectedItemPosition(position: Int) {
        val oldPosition = selectedItemPositionFun
        selectedItemPositionFun = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedItemPositionFun)
        listener?.onItemClick(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llColor: LinearLayout = itemView.findViewById(R.id.ll_color)
        val imgCheek: ImageView = itemView.findViewById(R.id.img_cheek)
        var isSelected: Boolean = false
    }


    override fun getItemCount(): Int {
        return data.size
    }

}