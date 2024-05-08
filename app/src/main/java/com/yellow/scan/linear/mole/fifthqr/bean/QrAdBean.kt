package com.yellow.scan.linear.mole.fifthqr.bean
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class QrAdBean(
    @SerializedName("op")
    val open_qr: MutableList<AdInformation> = ArrayList(),

    @SerializedName("crtIV")
    val crete_qr: MutableList<AdInformation> = ArrayList(),

    @SerializedName("bckIV")
    val back_qr: MutableList<AdInformation> = ArrayList(),

    @SerializedName("scnIV")
    val scan_qr: MutableList<AdInformation> = ArrayList(),

    val clickNum:Int,
    val showNum:Int,
)

@Keep
data class AdInformation(
    var id_scan: String? = null,
    var name_scan: String? = null,
    var type_scan: String? = null,
    var we_scan: String? = null,
)