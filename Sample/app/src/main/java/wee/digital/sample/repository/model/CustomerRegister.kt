package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class CustomerRegisterReq(
        @SerializedName("kioskID")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("faceId")
        var faceId: String = "",

        @SerializedName("customerInfo")
        var customerInfo: CustomerInfoRegister? = null,

        @SerializedName("cardType")
        var cardType: String = "",

        @SerializedName("ekycType")
        var ekycType: Int = 0,

        @SerializedName("videoCallId")
        var videoCallId: String = "",

        @SerializedName("methodOfReceiving")
        var methodOfReceiving: MethodOfReceiving? = null
)

data class CustomerInfoRegister(

        @SerializedName("customerType")
        var customerType: Int = 0,

        @SerializedName("identityCardInfo")
        var identityCardInfo: IdentifyCardInfo? = null,

        @SerializedName("phoneNumber")
        var phoneNumber: String = "",

        @SerializedName("createAt")
        var createAt: String = ""
)

data class IdentifyCardInfo(
        @SerializedName("type")
        var type: String = "",

        @SerializedName("photo")
        var photo: PhotoCardInfo? = null,

        @SerializedName("idCardNumber")
        var number: String = "",

        @SerializedName("fullName")
        var fullName: String = "",

        @SerializedName("dateOfBirth")
        var dateOfBirth: String = "",

        @SerializedName("gender")
        var gender: Int = 0,

        @SerializedName("hometown")
        var hometown: String = "",

        @SerializedName("permanentAddress")
        var permanentAddress: String = "",

        @SerializedName("issuedDate")
        var issuedDate: String = "",

        @SerializedName("issuedPlace")
        var issuedPlace: String = "",

        @SerializedName("expiredDate")
        var expiredDate: String = "",

        @SerializedName("nationality")
        var nationality: String = "",

        @SerializedName("phoneNumber")
        var phone: String = ""
)

data class PhotoCardInfo(
        @SerializedName("front")
        var cardFront: String = "",

        @SerializedName("back")
        var cardBack: String = "",
)

data class MethodOfReceiving(
        @SerializedName("type")
        var type: Int = 0,

        @SerializedName("branchCode")
        var branchCode: String = "",

        @SerializedName("homeInfo")
        var homeInfo: HomeInfo? = null
)

data class HomeInfo(
        @SerializedName("fullName")
        var fullName: String = "",

        @SerializedName("phoneNumber")
        var phoneNumber: String = "",

        @SerializedName("province")
        var province: String = "",

        @SerializedName("district")
        var district: String = "",

        @SerializedName("wards")
        var wards: String = "",

        @SerializedName("apartmentNumber")
        var apartmentNumber: String = ""
)

data class BranchInfo(
        @SerializedName("id")
        var id: String = "",

        @SerializedName("code")
        var code: String = "",

        @SerializedName("name")
        var name: String = "",

        @SerializedName("address")
        var address: String = ""
)