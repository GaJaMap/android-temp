package com.pg.gajamap.base

import android.app.Application
import android.util.Log
import com.pg.gajamap.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import okhttp3.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
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

        private const val PREF_NAME = "SessionPref"
        private const val KEY_SESSION_ID = "session_id"


    }

    private var sessionCookie: Cookie? = null

    private fun saveSessionCookie(sessionId: String, url: HttpUrl) {
        sessionCookie = Cookie.Builder()
            .domain(url.host)
            .path("/")
            .name("SESSION")
            .value(sessionId)
            .build()
    }

    private fun getSessionCookie(): Cookie? {
        return sessionCookie
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

        val cookieJar = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

                val cookie = extractSessionValue(cookies)
                Log.d("sessioncookies", cookie.toString())


            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
               //val sessionId = getSavedSessionId()
                val sessionId = GajaMapApplication.prefs.getString("session","")
                Log.d("sessionId", sessionId.toString())
                return if (sessionId != null) {
                    val sessionCookie = Cookie.Builder()
                        .domain(url.host)
                        .path("/")
                        .name("SESSION")
                        .value(sessionId)
                        .build()
                    listOf(sessionCookie)
                } else {
                    emptyList()
                }

            }

        }

        val builder = OkHttpClient().newBuilder()
        val okHttpClient = builder
            //.cookieJar(JavaNetCookieJar(CookieManager()))
            .cookieJar(cookieJar)
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
            val session = GajaMapApplication.prefs.getString("session","")
            Log.d("application", session)
            //val session = GajaMapApplication.prefs.getString("session","")
            val newRequest = request().newBuilder()
                .addHeader("Set-Cookie", session)
                .build()
            proceed(newRequest)
        }

    }*/

    fun extractSessionValue(cookieString: List<Cookie>): String? {
        val regex = Regex("SESSION=([A-Za-z0-9]+);")
        val matchResult = regex.find(cookieString.toString())
        return matchResult?.value
    }

    

}