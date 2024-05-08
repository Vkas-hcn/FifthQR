package com.yellow.scan.linear.mole.fifthqr.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yellow.scan.linear.mole.fifthqr.BuildConfig
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.bean.QrAdBean
import com.yellow.scan.linear.mole.fifthqr.bean.QrFlowBean
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object AppData {
    var put_data_url = if (BuildConfig.DEBUG) {
        "https://test-sarah.easyfastphoto.com/giddy/avian/beaten"
    } else {
        "https://sarah.easyfastphoto.com/parakeet/societe/oak"
    }
    var pp_url = "https://www.baidu.com"
    var bitmapQr: Bitmap? = null
    var qr_crete_data = """
        Text,Url,Location,Email,Wi-Fi
    """.trimIndent()

    val QR_OPEN = "op"
    val QR_BACK_MAIN = "bckIV"
    val QR_CLICK_SCAN = "scnIV"
    val QR_CLICK_CREATE = "crtIV"

    val online_qr_ad = "easy"
    val online_qr_config = "pt"
    private val sharedPreferences by lazy {
        App.getAppContext().getSharedPreferences(
            "fif_key",
            Context.MODE_PRIVATE
        )
    }

    var bg_type = 1
        set(value) {
            sharedPreferences.edit().run {
                putInt("bg_type", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getInt("bg_type", 1)

    var list_pos = 0
        set(value) {
            sharedPreferences.edit().run {
                putInt("list_pos", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getInt("list_pos", 0)

    var list_color_pos = 4
        set(value) {
            sharedPreferences.edit().run {
                putInt("list_color_pos", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getInt("list_color_pos", 4)
    var local_clock = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("local_clock", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("local_clock", "") ?: ""

    var uuid_fif = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("uuid_fif", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("uuid_fif", "") ?: ""
    var gidData =""
        set(value) {
            sharedPreferences.edit().run {
                putString("gidData", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("gidData", "") ?: ""
    var isInstall = "isInstall"
        set(value) {
            sharedPreferences.edit().run {
                putString("isInstall", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("isInstall", "").toString()
    var local_ref = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("local_ref", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("local_ref", "").toString()

    var online_qr_ad_data = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("online_qr_ad_data", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("online_qr_ad_data", "") ?: ""

    var online_qr_config_data = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("online_qr_config_data", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("online_qr_config_data", "") ?: ""
    var current_qr_date = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("current_qr_date", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("current_qr_date", "").toString()


    var clicks_qr_count = 0
        set(value) {
            sharedPreferences.edit().run {
                putInt("clicks_qr_count", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getInt("clicks_qr_count", 0)

    var show_qr_count = 0
        set(value) {
            sharedPreferences.edit().run {
                putInt("show_qr_count", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getInt("show_qr_count", 0)

    var ump_data_dialog = false
        set(value) {
            sharedPreferences.edit().run {
                putBoolean("ump_data_dialog", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getBoolean("ump_data_dialog", false)
    var adjust_fif = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("adjust_fif", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("adjust_fif", "").toString()

    fun getQrCreteData(): List<String> {
        return qr_crete_data.split(",")
    }

    fun getNowBgDataList(): List<Int> {
        return cartoon_bg
    }

    fun getNowIcDataList(): List<Int> {
        return cartoon_ic
    }

    val cartoon_bg = listOf(
        R.drawable.bg_cartoon_1,
        R.drawable.bg_cartoon_2,
        R.drawable.bg_cartoon_3,
        R.drawable.bg_cartoon_4,
        R.drawable.bg_cartoon_5,
        R.drawable.bg_cartoon_6,
        R.drawable.bg_cartoon_7,
        R.drawable.bg_cartoon_8,
        R.drawable.bg_cartoon_9,
        R.drawable.bg_cartoon_10,
        R.drawable.bg_cartoon_12,
        R.drawable.bg_cartoon_13,
        R.drawable.bg_cartoon_14,
        R.drawable.bg_cartoon_58,
        R.drawable.bg_cartoon_59,
        R.drawable.bg_cartoon_60,
        R.drawable.bg_cartoon_61,
        R.drawable.bg_cartoon_62,
        R.drawable.bg_cartoon_63,
        R.drawable.bg_cartoon_64,
        R.drawable.bg_cartoon_65,
        R.drawable.bg_cartoon_66,
        R.drawable.bg_cartoon_67,
        R.drawable.bg_cartoon_68,
    )
    val cartoon_ic = listOf(
        R.drawable.ic_cartoon_1,
        R.drawable.ic_cartoon_2,
        R.drawable.ic_cartoon_3,
        R.drawable.ic_cartoon_4,
        R.drawable.ic_cartoon_5,
        R.drawable.ic_cartoon_6,
        R.drawable.ic_cartoon_7,
        R.drawable.ic_cartoon_8,
        R.drawable.ic_cartoon_9,
        R.drawable.ic_cartoon_10,
        R.drawable.ic_cartoon_12,
        R.drawable.ic_cartoon_13,
        R.drawable.ic_cartoon_14,
        R.drawable.ic_cartoon_58,
        R.drawable.ic_cartoon_59,
        R.drawable.ic_cartoon_60,
        R.drawable.ic_cartoon_61,
        R.drawable.ic_cartoon_62,
        R.drawable.ic_cartoon_63,
        R.drawable.ic_cartoon_64,
        R.drawable.ic_cartoon_65,
        R.drawable.ic_cartoon_66,
        R.drawable.ic_cartoon_67,
        R.drawable.ic_cartoon_68,
    )
    val te_bg = listOf(
        R.drawable.bg_te01,
        R.drawable.bg_te02,
        R.drawable.bg_te03,
        R.drawable.bg_te04,
        R.drawable.bg_te05,
        R.drawable.bg_te06,
        R.drawable.bg_te07,
        R.drawable.bg_te08,
        R.drawable.bg_te09,
        R.drawable.bg_te10,

        R.drawable.bg_te11,
        R.drawable.bg_te12,
        R.drawable.bg_te13,
        R.drawable.bg_te14,
        R.drawable.bg_te15,
        R.drawable.bg_te16,
        R.drawable.bg_te17,
        R.drawable.bg_te18,
        R.drawable.bg_te19,
        R.drawable.bg_te20,

        R.drawable.bg_te21,
        R.drawable.bg_te22,
        R.drawable.bg_te23,
        R.drawable.bg_te24,
    )
    val te_ic = listOf(
        R.drawable.ic_te01,
        R.drawable.ic_te02,
        R.drawable.ic_te03,
        R.drawable.ic_te04,
        R.drawable.ic_te05,
        R.drawable.ic_te06,
        R.drawable.ic_te07,
        R.drawable.ic_te08,
        R.drawable.ic_te09,
        R.drawable.ic_te10,

        R.drawable.ic_te11,
        R.drawable.ic_te12,
        R.drawable.ic_te13,
        R.drawable.ic_te14,
        R.drawable.ic_te15,
        R.drawable.ic_te16,
        R.drawable.ic_te17,
        R.drawable.ic_te18,
        R.drawable.ic_te19,
        R.drawable.ic_te20,

        R.drawable.ic_te21,
        R.drawable.ic_te22,
        R.drawable.ic_te23,
        R.drawable.ic_te24,
    )
    val pop_bg = listOf(
        R.drawable.bg_pop_1,
        R.drawable.bg_pop_2,
        R.drawable.bg_pop_3,
        R.drawable.bg_pop_4,
        R.drawable.bg_pop_5,
        R.drawable.bg_pop_6,
        R.drawable.bg_pop_7,
        R.drawable.bg_pop_8,
        R.drawable.bg_pop_9,
        R.drawable.bg_pop_10,
        R.drawable.bg_pop_11,
        R.drawable.bg_pop_12,
    )
    val pop_ic = listOf(
        R.drawable.ic_pop_1,
        R.drawable.ic_pop_2,
        R.drawable.ic_pop_3,
        R.drawable.ic_pop_4,
        R.drawable.ic_pop_5,
        R.drawable.ic_pop_6,
        R.drawable.ic_pop_7,
        R.drawable.ic_pop_8,
        R.drawable.ic_pop_9,
        R.drawable.ic_pop_10,
        R.drawable.ic_pop_12,
    )
    val colorsArray = intArrayOf(
        R.color.color0,
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6,
        R.color.color7,
        R.color.color8,
        R.color.color9,
        R.color.color10,
        R.color.color11,
        R.color.color12,
        R.color.color13,
        R.color.color14,
        R.color.color15,
    )


    fun getBgDataList(): List<Int> {
        val additionalList = listOf( R.drawable.bg_10 )
        return when (bg_type) {
            1 -> additionalList + pop_bg
            2 -> additionalList + te_bg
            3 -> additionalList + cartoon_bg
            else -> additionalList + pop_bg
        }
    }

    fun getIcDataList(): List<Int> {
        val additionalList = listOf( R.drawable.ic_vector )
        return when (bg_type) {
            1 -> additionalList + pop_ic
            2 -> additionalList + te_ic
            3 -> additionalList + cartoon_ic
            else -> additionalList + pop_ic
        }
    }

    fun getMoreIcDataList(): List<Int> {
        return when (bg_type) {
            1 -> pop_ic
            2 -> te_ic
            3 -> cartoon_ic
            else -> pop_ic
        }
    }


    val local_qr_ad_data = """
          {
            "op": [
                {
                    "we_scan": "c",
                    "id_scan": "ca-app-pub-3940256099942544/9257395921",
                    "name_scan": "op",
                    "type_scan": "Open"
                },{
                    "we_scan": "a",
                    "id_scan": "ca-app-pub-3940256099942544/9257395921x",
                    "name_scan": "op",
                    "type_scan": "Open"
                },{
                    "we_scan": "b",
                    "id_scan": "ca-app-pub-3940256099942544/9257395921x",
                    "name_scan": "op",
                    "type_scan": "Open"
                }
            ],
            "crtIV": [
                {
                    "we_scan": "b",
                    "id_scan": "ca-app-pub-3940256099942544/1033173712",
                    "name_scan": "crtIV",
                    "type_scan": "Interstitial"
                }
            ],
            "scnIV": [
                {
                    "we_scan": "c",
                    "id_scan": "ca-app-pub-3940256099942544/1033173712",
                    "name_scan": "scnIV",
                    "type_scan": "Interstitial"
                }
            ],
            "bckIV": [
                {
                    "we_scan": "c",
                    "id_scan": "ca-app-pub-3940256099942544/8691691433",
                    "name_scan": "bckIV",
                    "type_scan": "Interstitial"
                }
            ],
            "cc": 2,
            "sh": 20
        }
    """.trimIndent()

    val flowBeanData = """
{
    "pt": "00"
}
    """.trimIndent()
    fun getAdJson(): QrAdBean {
        val dataJson = online_qr_ad_data.let {
            if (it.isEmpty()) {
                local_qr_ad_data
            } else {
                decodeBase64(it)
            }
        }
        return try {
            Gson().fromJson(dataJson, object : TypeToken<QrAdBean>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            Gson().fromJson(
                local_qr_ad_data,
                object : TypeToken<QrAdBean>() {}.type
            )
        }
    }
    fun getFlowJson(): QrFlowBean {
        val dataJson = online_qr_config_data.let {
            if (it.isEmpty()) {
                flowBeanData
            } else {
                decodeBase64(it)
            }
        }
        return try {
            Gson().fromJson(dataJson, object : TypeToken<QrFlowBean>() {}.type)
        } catch (e: Exception) {
            Gson().fromJson(
                flowBeanData,
                object : TypeToken<QrFlowBean>() {}.type
            )
        }
    }
    fun decodeBase64(str: String): String {
        return String(android.util.Base64.decode(str, android.util.Base64.DEFAULT))
    }



    fun recordNumberOfAdDisplaysGreen() {
        var showCount = show_qr_count
        showCount++
        show_qr_count = showCount
    }

    fun recordNumberOfAdClickGreen() {
        var clicksCount = clicks_qr_count
        clicksCount++
        clicks_qr_count = clicksCount
    }

    fun isAppGreenSameDayGreen() {
        if (current_qr_date == "") {
            current_qr_date = formatDateNow()
        } else {
            if (dateAfterDate(current_qr_date, formatDateNow())) {
                current_qr_date = formatDateNow()
                clicks_qr_count = 0
                show_qr_count = 0
            }
        }
    }

    private fun formatDateNow(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return simpleDateFormat.format(date)
    }

    fun dateAfterDate(startTime: String?, endTime: String?): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            val startDate: Date = format.parse(startTime)
            val endDate: Date = format.parse(endTime)
            val start: Long = startDate.getTime()
            val end: Long = endDate.getTime()
            if (end > start) {
                return true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        return false
    }
    fun isThresholdReached(): Boolean {
        return clicks_qr_count >= getAdJson().cc || show_qr_count >= getAdJson().sh
    }
    fun AppCompatActivity.isVisible(): Boolean {
        return lifecycle.currentState == Lifecycle.State.RESUMED
    }
    fun showAdBlacklist(): Boolean {
        val blackData = local_clock != "drapery"
        return when (getFlowJson().black_type_qr) {
            "00" -> {
                !blackData
            }

            "2" -> {
                true
            }

            else -> {
                true
            }
        }
    }
}