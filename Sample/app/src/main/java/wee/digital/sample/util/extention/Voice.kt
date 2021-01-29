package wee.digital.sample.util.extention

import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.annotation.StringDef
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST
import wee.digital.library.extension.put
import wee.digital.library.extension.str
import wee.digital.sample.app.app
import wee.digital.sample.shared.Configs
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private interface VoiceService {
    @POST("v1/text:synthesize?key=AIzaSyCWtf_bRqiFgOKUPYOvrHWvl_zWU0H4TQw")
    fun getVoice(@Body body: JsonObject): Single<JsonObject>
}

private val voiceService: VoiceService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    retrofit("https://texttospeech.googleapis.com/") {
        client(OkHttpClient().newBuilder().callTimeout(2,TimeUnit.SECONDS).build())
    }.create(VoiceService::class.java)
}

@StringDef(Lang.US, Lang.EN, Lang.VI)
annotation class Lang {
    companion object {
        const val US = "us"
        const val EN = "us"//""en"
        const val VI = "vi"
    }
}

@Suppress("DEPRECATION")
class Voice {

    var isSpeech = false

    var mediaPlayer: MediaPlayer? = null

    var onSpeaking: () -> Unit = {
        println("onSpeaking")
    }

    var onSpeakCompleted: () -> Unit = {}

    private var voice = JsonObject().apply {
        put("languageCode", "vi-VN")
        put("name", "vi-VN-Wavenet-A")
    }

    private val config = JsonObject().apply {
        put("audioEncoding", "MP3")
        put("pitch", 0)
        put("speakingRate", "1")
    }

    fun request(text: String, onCompleted: ()->Unit = {}) {
        onSpeakCompleted = onCompleted
        if (Configs.isMute) {
            onSpeaking()
            onSpeakCompleted()
            return
        }
        if (isSpeech) return
        isSpeech = true
        val name = text.hashCode().toString()+"_${voice["name"].asString}"
        val temp = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path+"/VPKiosk/Voice")
        if(!temp.exists()) temp.mkdirs()
        var tempMp3:File?=null
        if(temp.listFiles()!=null) {
            for (file in temp.listFiles()!!) {
                if(file.name.toString().contains("$name.mp3")){
                    tempMp3 = file
                    break
                }
            }
        }
        if(tempMp3!=null){
            Log.e("Voice","Play File")
            updateMediaPlayer(tempMp3.readBytes(), text)
        }else{
            Log.e("Voice","Play Cloud $voice")
            val body = getTextToSpeechRequest(text)
            voiceService.getVoice(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(object : SimpleSingleObserver<JsonObject> {
                        override fun onError(e: Throwable) {
                            isSpeech = false
                        }

                        override fun onSuccess(it: JsonObject) {
                            val audioContent = it.str("audioContent") ?: return
                            val voice = Base64.decode(audioContent, Base64.NO_WRAP)
                            //--- Save File
                            try {
                                val fileMp3 = File(temp.path+"/$name.mp3")
                                fileMp3.deleteOnExit()
                                fileMp3.createNewFile()
                                val fos = FileOutputStream(fileMp3)
                                fos.write(voice)
                                fos.close()
                            }catch (e: IOException){
                                e.printStackTrace()
                            }
                            //----
                            updateMediaPlayer(voice, text)
                        }
                    })
        }


    }

    fun release() {
        isSpeech = false
        /*onSpeaking = {
            Log.e("Voice","onSpeaking")
        }
        onSpeakCompleted = {}*/
        try {
            mediaPlayer?.release()
        } catch (e: IllegalStateException) {
        }
    }

    private fun updateMediaPlayer(mp3: ByteArray, text: String) {
        try {
            mediaPlayer?.release()
            //mediaPlayer?.stop()
        } catch (e: IllegalStateException) {
        }
        createMediaPlayer(mp3, text)
                .subscribe(object : SimpleSingleObserver<File> {
                    override fun onError(e: Throwable) {
                        isSpeech = false
                    }

                    override fun onSuccess(file: File) {
                        mediaPlayer = MediaPlayer.create(app, Uri.fromFile(file)).also {
                            it.setOnCompletionListener {
                                isSpeech = false
                                onSpeakCompleted()
                            }
                            it.setOnPreparedListener { player ->
                                player.start()
                                onSpeaking()
                            }
                        }
                    }
                })
    }

    private fun createMediaPlayer(mp3: ByteArray, text: String): Single<File> {
        return Single
                .fromCallable {
                    val s = text.encodeUTF8()
                    val prefix = if (s.length < 15) s.substring(0, s.lastIndex) else s.substring(0, 15)
                    val file = File.createTempFile(prefix, ".mp3", app.cacheDir)
                    file.deleteOnExit()
                    FileOutputStream(file).apply {
                        write(mp3)
                        close()
                    }
                    return@fromCallable file
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getTextToSpeechRequest(text: String): JsonObject {
        val input = JsonObject().apply {
            put("text", text)
        }
        return JsonObject().apply {
            put("audioConfig", config)
            put("input", input)
            put("voice", voice)
        }
    }

    private fun String.encodeUTF8(): String {
        return Base64.encodeToString(this.toByteArray(Charset.forName("UTF-8")), Base64.NO_WRAP)
    }

    fun setLanguage(@Lang lang: String) {
        voice = JsonObject()
        voice.apply {
            when (lang) {
                Lang.US -> {
                    put("languageCode", "en-US")
                    put("name", "en-US-Wavenet-D")
                }
                Lang.VI -> {
                    put("languageCode", "vi-VN")
                    put("name", "vi-VN-Wavenet-A")
                }
                else -> { // vi
                    put("languageCode", "vi-VN")
                    put("name", "vi-VN-Wavenet-A")
                }
            }
        }
        Log.e("Voice","setLanguage: $voice")
    }

    companion object{
        private var voice: Voice? = null
        val ins: Voice? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { if(voice ==null) Voice() else voice }

    }


}