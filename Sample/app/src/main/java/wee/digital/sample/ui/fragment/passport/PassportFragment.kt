package wee.digital.sample.ui.fragment.passport

import android.graphics.Bitmap
import android.graphics.Camera
import android.util.Base64
import kotlinx.android.synthetic.main.passport.*
import wee.dev.weeocr.WeeOCR
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.PhotoCardInfo
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class PassportFragment : MainFragment(), FrameStreamListener {

    private var camera: CameraSource? = null

    private var complete: Boolean? = false

    private var weeOcr: WeeOCR? = null

    private var processing = false

    override fun layoutResource(): Int = R.layout.passport

    override fun onViewCreated() {
        complete = false
        processing = false
        Configs.configWeeOcr()
        weeOcr = WeeOCR(requireActivity())
    }

    override fun onLiveDataObserve() {
    }

    private fun startCamera() {
        try {
            camera = CameraSource(requireActivity(), passportGraphicOverlay).also {
                it.setFacing(CameraConfig.facing)
                passportPreview.start(it, passportGraphicOverlay)
                it.setFrameProcessorListener(this)
            }
        } catch (e: Exception) {
            toast("start camera error : ${e.message}")
        }
    }

    private fun release() {
        try {
            camera?.release()
            passportPreview?.release()
            weeOcr?.destroy()
            camera = null
        } catch (e: Exception) {
            toast("stop camera error : ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        release()
    }

    /**
     * listener frame camera
     */
    override fun onFrame(byteArray: ByteArray) {
        if (processing) return
        processing = true
        weeOcr?.checkPassport(byteArray, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) {
            if (it.frame != null) {
                sendSocket(it.frame)
                Shared.passportData.postValue(it)
                Shared.frameCardData.postValue(
                        PhotoCardInfo(
                                Base64.encodeToString(it.frame.toBytes(), Base64.NO_WRAP),
                                Base64.encodeToString(it.frame.toBytes(), Base64.NO_WRAP)
                        )
                )
                navigate(MainDirections.actionGlobalPassportInfoFragment())
                return@checkPassport
            }
            processing = false
        }
    }

    private fun sendSocket(bitmap: Bitmap?) {
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_2
        resp?.data?.photo = PhotoCardInfo(cardFront = bitmap.toBytes().toStringBase64(), cardBack = bitmap.toBytes().toStringBase64())
        Socket.action.sendData(resp)
        Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
    }

}