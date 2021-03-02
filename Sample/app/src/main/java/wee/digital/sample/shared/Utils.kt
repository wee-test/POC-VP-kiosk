package wee.digital.sample.shared

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import java.io.File
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun checkSizeBitmap(bm: Bitmap?): Boolean{
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

    fun getUUIDRandom(): String {
        return UUID.randomUUID().toString()
    }

    fun randomAccountNumber(): String {
        var accountNumber = ""
        for (i in 0..13) {
            val ran = Random().nextInt(9)
            accountNumber += ran
            if (accountNumber.length >= 12) {
                return accountNumber
            }
        }
        return accountNumber
    }

    fun spaceAccountNumber(card: String): String {
        try{
            if (card.length != 16) return card
            val str1 = card.substring(0, 4) + "   "
            val str2 = card.substring(4, 8) + "   "
            val str3 = card.substring(8, 12) + "   "
            val str4 = card.substring(12, 16)
            return str1 + str2 + str3 + str4
        } catch (e: Exception) {
            return card
        }
    }

    fun deleteFileVideo(context: Context) {
        try {
            val path = "${context.externalCacheDir}"
            val listFile = File(path).listFiles()
            if (listFile.isNullOrEmpty()) return
            for (model in listFile) {
                val name = model.toString().split(".")
                if (name.last() == "mp4") {
                    deleteFile(model.toString())
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFile(path: String) {
        try {
            val file = File(path, "")
            if (file.exists()) {
                val result = file.delete()
            } else {
                Log.e("deleteFile", "file not exists")
            }
        } catch (e: java.lang.Exception) {
            Log.e("deleteFile", e.message.toString())
        }
    }

    fun deleteDirectory(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return true
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectory(files[i])
                } else {
                    files[i].delete()
                }
            }
        }
        return path.delete()
    }

}