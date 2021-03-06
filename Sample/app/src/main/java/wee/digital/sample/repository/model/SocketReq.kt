package wee.digital.sample.repository.model

data class SocketReq(
        var cmd: String = "",
        var data: SocketData? = SocketData()
)

data class SocketData(
        var type: String = "",
        var photo: PhotoCardInfo? = null,
        var idCardNumber: String = "",
        var fullName: String = "",
        var dateOfBirth: String = "",
        var gender: Int = 0,
        var hometown: String = "",
        var permanentAddress: String = "",
        var issuedDate: String = "",
        var issuedPlace: String = "",
        var expiredDate: String = "",
        var nationality: String = "Việt Nam",
        var faceImage: String = "",
        var cardType: String = "",
        var branchInfo: BranchInfo? = null,
        var homeInfo: HomeInfo? = null,
        var isConfirmed: Boolean = false,
        var reviewType: Int = 0,
        var phoneNumber : String = "",
        var email : String = "",
        var methodOfReceivingType : Int = 1,
        var idCardMatched : Boolean = false,
        var passportNumber : String = "",
        var passportType : String = "",
        var passportCode : String = ""
)