package com.example.gajamap.ui.view

import com.kakao.sdk.auth.model.OAuthToken

data class KakaoResponse(
    var token: OAuthToken?,
    var error: Throwable?
)
