package com.yellow.scan.linear.mole.fifthqr.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class QrFlowBean(
    @SerializedName("pt")
    val black_type_qr: String = "",
)
