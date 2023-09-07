package com.pg.gajamap.api.retrofit

import com.pg.gajamap.api.service.KakaoAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 싱글톤 패턴 : 어떤 클래스의 인스턴스는 오직 하나임을 보장, 이 인스턴스는 전역에서 접근할 수 있는 디자인 패턴
// 프로세스가 메모리 상에 올라갈 때 곧바로 생성
object KakaoSearchClient {
    private val BASE_URL = "https://dapi.kakao.com/"
    private var kakaoSearchClient: Retrofit? = null

    fun getKakaoSearchClient() : Retrofit? {
        kakaoSearchClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return kakaoSearchClient
    }
    val kakaoSearchService = getKakaoSearchClient()?.create(KakaoAPI::class.java)
}