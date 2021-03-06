package wee.digital.sample.ui.fragment.member

import android.net.Uri
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.document.*
import kotlinx.android.synthetic.main.document_select.*
import wee.dev.weewebrtc.WeeCaller
import wee.digital.library.extension.toArray
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.SocketData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.fragment.ocr.OcrVM
import wee.digital.sample.ui.fragment.register.RegisterVM
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class DocumentFragment : MainFragment() {

    private var disposable: Disposable? = null

    override fun layoutResource(): Int = R.layout.document

    override fun onViewCreated() {
        Voice.ins?.request(VoiceData.SELECT_ID_CARD)
    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when(v){
            documentRootNid -> {
                sendSocket(Configs.TYPE_NID)

                Shared.typeCardOcr.postValue(Configs.TYPE_NID)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootNid12 -> {
                sendSocket(Configs.TYPE_NID_12)

                Shared.typeCardOcr.postValue(Configs.TYPE_NID_12)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootCccd -> {
                sendSocket(Configs.TYPE_CCCD)

                Shared.typeCardOcr.postValue(Configs.TYPE_CCCD)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootPassport -> {
                sendSocket(Configs.TYPE_PASSPORT)

                Shared.typeCardOcr.postValue(Configs.TYPE_PASSPORT)
                navigate(MainDirections.actionGlobalPassportFragment())
            }
        }
    }

    private fun sendSocket(type :String){
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_1
        resp?.data?.type = type
        Socket.action.sendData(resp)
        Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
    }

    override fun onResume() {
        super.onResume()
        disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val urlPath = "android.resource://" + activity?.packageName + "/" + R.raw.card
                    documentTipGif.setVideoURI(Uri.parse(urlPath))
                    documentTipGif.start()
                    documentTipGif.setOnCompletionListener { it.start() }
                    addClickListener(documentRootNid, documentRootNid12, documentRootCccd, documentRootPassport)
                }, {})
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}