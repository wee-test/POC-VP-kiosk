package wee.digital.sample.app

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import vplib.Lib
import wee.dev.weeocr.utils.OpenCVUtils
import wee.digital.camera.RealSense
import wee.digital.library.Library

lateinit var app: App private set

var lib : Lib? = null

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.app = this
        RealSense.app = this
        OpenCVUtils.initOpenCV(this)
    }

}
