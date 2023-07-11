package com.example.gajamap.base

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.gajamap.BuildConfig

import com.kakao.sdk.common.KakaoSdk
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import okhttp3.*
import okhttp3.Response
import retrofit2.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.CookieManager
import java.util.concurrent.TimeUnit

class GajaMapApplication : Application() {

    val API_URL = "http://52.79.103.19:8080/"

    companion object {
        lateinit var instance: GajaMapApplication
            private set
        fun getApplication() = instance
        lateinit var prefs: PreferenceUtil

        // Retrofit 인스턴스, 앱 실행시 한번만 생성하여 사용합니다.
        lateinit var sRetrofit: Retrofit



    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "${BuildConfig.KAKAO_API_KEY}")
        instance = this
        prefs = PreferenceUtil(applicationContext)
        // 레트로핏 인스턴스 생성
        initRetrofitInstance()
    }


    private fun initRetrofitInstance(){

        val builder = OkHttpClient().newBuilder()
        val okHttpClient = builder
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            //.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()



        sRetrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(provideOkHttpClient(AppInterceptor()))
            .build()
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                try{
                    nextResponseBodyConverter.convert(value)
                }catch (e:Exception){
                    e.printStackTrace()
                    null
                }
            } else{
                null
            }
        }
    }

    /*private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
            = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val session = "JSESSIONID=" + GajaMapApplication.prefs.getString("session","")
            Log.d("application", session)
            //val session = GajaMapApplication.prefs.getString("session","")
            val newRequest = request().newBuilder()
                .addHeader("Set-Cookie", session)
                .build()
            proceed(newRequest)
        }
    }*/
    //

}