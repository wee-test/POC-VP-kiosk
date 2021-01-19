package wee.digital.sample.app

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import wee.digital.camera.RealSense
import wee.digital.camera.widget.OpenCVUtils
import wee.digital.library.Library

lateinit var app: App private set

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.app = this
        RealSense.app = this
        OpenCVUtils.initOpenCV(this)
    }

}
