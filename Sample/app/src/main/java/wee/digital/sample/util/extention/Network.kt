package wee.digital.sample.util.extention

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: FEKiosk
 * @Created: Huy 2020/10/11
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
fun retrofit(baseUrl: String, block: Retrofit.Builder.() -> Unit): Retrofit {
    val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).also {
                it.block()
            }
    return retrofit.build()
}