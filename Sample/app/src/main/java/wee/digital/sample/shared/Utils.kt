package wee.digital.sample.shared

import android.app.Activity
import android.graphics.Bitmap
import android.util.DisplayMetrics
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun checkSizeBitmap(bm : Bitmap?): Boolean{
        bm ?: return false
        val w = bm.width
        val h = bm.height
        return h > (w * 0.5).roundToInt()
    }

    fun getHeightScreen(act: Activity): Int {
        val displayMetrics = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun lengthNumberCard(type: String?): Int {
        type ?: return 9
        return when (type) {
            Configs.TYPE_NID -> 9
            Configs.TYPE_NID_12 -> 12
            Configs.TYPE_CCCD -> 12
            else -> 9
        }
    }

    fun checkPhoneInvalid(number: String): Boolean {
        if (number.isEmpty()) return true
        val first = number[0]
        return if (first.toString() == "8" && number.length == 11) {
            false
        } else !(first.toString() == "0" && number.length == 10)
    }

    fun getProvinceFromName(proName: String?): Selectable {
        proName ?: return Selectable()
        var pro = Selectable()
        Shared.provinceList?.forEach { model ->
            if (compareString(model.NAME, proName)) {
                pro = model
                return@forEach
            }
        }
        return pro
    }

    fun geListDistrictFromProvinceCode(code: String): List<Selectable> {
        return Shared.districtList!!.filter {
            it.PARENTCLASSIFICATIONVALUEID == code
        }
    }

    fun getDistrictFromName(name: String?): Selectable {
        name ?: return Selectable()
        var pro = Selectable()
        Shared.districtList?.forEach { model ->
            if (compareString(model.NAME, name)) {
                pro = model
                return@forEach
            }
        }
        return pro
    }

    fun geListWardFromDistrictCode(code: String): List<Selectable> {
        return Shared.wardList!!.filter {
            it.PARENTCLASSIFICATIONVALUEID == code
        }
    }

    private fun compareString(str1: String, str2: String): Boolean {
        val trimStr1 = trimStrCompare(str1)
        val trimStr2 = trimStrCompare(str2)
        val unVNStr1 = VNCharacterUtils.removeAccent(trimStr1)
        val unVNStr2 = VNCharacterUtils.removeAccent(trimStr2)
        return unVNStr1.equals(unVNStr2, ignoreCase = true)
    }

    private fun trimStrCompare(str: String): String {
        return str.toUpperCase(Locale.getDefault())
                .replaceFirst("TỈNH", "")
                .replaceFirst("TP", "")
                .replaceFirst("THÀNH PHỐ", "")
                .replace(" ", "")
    }

    fun getIssueDatePassport(exDate: String): String {
        return try {
            val listEx = exDate.split("/")
            val newYear = listEx.last().toInt() - 10
            "${listEx[0]}/${listEx[1]}/$newYear"
        } catch (e: Exception) {
            ""
        }
    }

    fun getNameNational(code: String?): String {
        code ?: return ""
        Shared.nationalList?.forEach {
            if (it.Code == code) return it.Name
        }
        return ""
    }

}