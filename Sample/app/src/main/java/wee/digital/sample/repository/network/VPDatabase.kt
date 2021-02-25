package wee.digital.sample.repository.network

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import wee.digital.sample.repository.model.VPKioskInfo
import wee.digital.sample.shared.Configs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class VPDatabase {

    companion object {
        val ins: VPDatabase by lazy { VPDatabase() }
    }

    fun insertKiosk(){
        if(Configs.KIOSK_CODE.isEmpty()) return
        val database = Firebase.database

        val myRef = database.reference.child("vpKiosk").child("kiosk").child(Configs.KIOSK_CODE)
        val data = VPKioskInfo(time = getTime())
        myRef.setValue(data)

        myRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                print("")
            }

            override fun onCancelled(error: DatabaseError) {
                print("The read failed : ${error.code}")
            }

        })
    }

    fun updateCheckVersionKiosk(){
        val database = Firebase.database
        val myRef = database.reference.child("vpKiosk").child("kiosk").child(Configs.KIOSK_CODE)
        val data = HashMap<String, Any>()
        data.put("time", getTime())
        data.put("check", false)
        myRef.updateChildren(data).addOnSuccessListener {}
    }

    private fun getTime(): String {
        val simpleDate = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val date = Date(System.currentTimeMillis())
        return simpleDate.format(date)
    }

}