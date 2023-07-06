package com.example.gajamap.ui.fragment.setting

import android.content.ContentValues.TAG
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentKakaoProfileBinding
import com.example.gajamap.ui.adapter.KakaoFriendAdapter
import com.example.gajamap.viewmodel.SettingViewModel
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KakaoProfileFragment: BaseFragment<FragmentKakaoProfileBinding>(R.layout.fragment_kakao_profile){

    override val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@KakaoProfileFragment
        binding.fragment = this@KakaoProfileFragment
    }

    override fun onCreateAction() {

        // 친구 목록 요청
        TalkApiClient.instance.friends { friends, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
                // 사용자 정보 요청 (추가 동의)

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("kakao", "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        var scopes = mutableListOf<String>()

                        if (user.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }
                        if (user.kakaoAccount?.birthdayNeedsAgreement == true) { scopes.add("birthday") }
                        if (user.kakaoAccount?.birthyearNeedsAgreement == true) { scopes.add("birthyear") }
                        if (user.kakaoAccount?.genderNeedsAgreement == true) { scopes.add("gender") }
                        if (user.kakaoAccount?.phoneNumberNeedsAgreement == true) { scopes.add("phone_number") }
                        if (user.kakaoAccount?.profileNeedsAgreement == true) { scopes.add("profile") }
                        if (user.kakaoAccount?.ageRangeNeedsAgreement == true) { scopes.add("age_range") }
                        if (user.kakaoAccount?.ciNeedsAgreement == true) { scopes.add("account_ci") }

                        if (scopes.isNotEmpty()) {
                            Log.d("kakao", "사용자에게 추가 동의를 받아야 합니다.")

                            // OpenID Connect 사용 시
                            // scope 목록에 "openid" 문자열을 추가하고 요청해야 함
                            // 해당 문자열을 포함하지 않은 경우, ID 토큰이 재발급되지 않음
                            // scopes.add("openid")

                            //scope 목록을 전달하여 카카오 로그인 요청
                            UserApiClient.instance.loginWithNewScopes(requireContext(), scopes) { token, error ->
                                if (error != null) {
                                    Log.e("kakao", "사용자 추가 동의 실패", error)
                                } else {
                                    Log.d("kakao", "allowed scopes: ${token!!.scopes}")

                                    // 사용자 정보 재요청
                                    UserApiClient.instance.me { user, error ->
                                        if (error != null) {
                                            Log.e("kakao", "사용자 정보 요청 실패", error)
                                        }
                                        else if (user != null) {
                                            Log.i("kakao", "사용자 정보 요청 성공")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (friends != null) {
                //Log.i("kakaoprofile", "카카오톡 친구 목록 가져오기 성공 \n${friends.elements?.joinToString("\n")}")
                Log.i("kakaoprofile", "$friends")

                //카카오 친구목록 리사이클러뷰
                val kakaoFriendAdapter = friends.elements?.let { KakaoFriendAdapter(it) }
                binding.phoneListRv.apply {
                    adapter = kakaoFriendAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(PhoneListVerticalItemDecoration())
                }


            }
        }


    }
}