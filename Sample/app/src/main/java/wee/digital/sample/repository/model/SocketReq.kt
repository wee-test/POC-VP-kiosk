package wee.digital.sample.repository.model

data class SocketReq(
        val cmd: String = "",
        val data: SocketData? = null
)

data class SocketData(
        var type: String = "",
        var photo: PhotoCardInfo? = null,
        var idCardNumber: String = "",
        var passportNumber: String = "",
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
        var reviewType: Int = 0
)