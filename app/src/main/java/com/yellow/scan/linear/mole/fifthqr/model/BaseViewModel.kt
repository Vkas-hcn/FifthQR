package com.yellow.scan.linear.mole.fifthqr.model

import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

class BaseViewModel : ViewModel() {


    fun convertCardViewToBitmap(cardView: View, callback: (Bitmap) -> Unit) {
        cardView.post {
            // 获取 CardView 布局的宽高
            val widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(cardView.width, View.MeasureSpec.EXACTLY)
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(cardView.height, View.MeasureSpec.EXACTLY)
            cardView.measure(widthMeasureSpec, heightMeasureSpec)
            cardView.layout(0, 0, cardView.measuredWidth, cardView.measuredHeight)

            // 创建一个与 CardView 大小相同的 Bitmap 对象
            val bitmap =
                Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)

            // 创建一个 Canvas 对象，并将 Bitmap 与其关联
            val canvas = Canvas(bitmap)

            // 将 CardView 布局绘制到 Bitmap 上
            cardView.draw(canvas)

            // 调用回调函数，将 Bitmap 传递出去
            callback(bitmap)
        }
    }


}