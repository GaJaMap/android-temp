package com.example.gajamap.ui.fragment.setting

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.data.model.Clients
import com.example.gajamap.data.model.PostKakaoPhoneRequest
import com.example.gajamap.databinding.FragmentKakaoProfileBinding
import com.example.gajamap.ui.adapter.KakaoFriendAdapter
import com.example.gajamap.viewmodel.ClientViewModel
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient

class KakaoProfileFragment: BaseFragment<FragmentKakaoProfileBinding>(R.layout.fragment_kakao_profile){

    // 선택된 클라이언트들을 저장하기 위한 리스트
    private var selectedClients: MutableList<Clients?> = mutableListOf()

    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@KakaoProfileFragment
        binding.fragment = this@KakaoProfileFragment
    }

    override fun onCreateAction() {

        binding.topBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, SettingFragment()).addToBackStack(null).commit()
        }
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

                Log.d("phonekakao", friends.elements.toString())

                //카카오 친구목록 리사이클러뷰
                val kakaoFriendAdapter = friends.elements?.let { KakaoFriendAdapter(it) }
                binding.phoneListRv.apply {
                    adapter = kakaoFriendAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(PhoneListVerticalItemDecoration())
                }

                //전체선택
                binding.settingPhoneCheckEvery.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedClients.addAll(friends.elements?.map {
                            it.profileNickname?.let { it1 -> Clients(it1, "010-1111-1111") }
                        } ?: emptyList())
                    }
                    else {
                        selectedClients.clear()
                    }
                    kakaoFriendAdapter?.setAllItemsChecked(isChecked)
                }

                kakaoFriendAdapter?.setOnItemClickListener(object :
                    KakaoFriendAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int) {
                        // 아이템 클릭시 해당 아이템의 선택 여부를 토글하고 선택된 클라이언트 리스트 업데이트
                        val item = friends.elements?.get(position)
                        Log.d("selectItem", item.toString())
                        item?.let {
                            if (kakaoFriendAdapter.isChecked(position)) {
                                it.profileNickname?.let { nickname ->
                                    selectedClients.add(Clients(nickname, "010-1111-1111"))
                                }
                            } else {
                                it.profileNickname?.let { nickname ->
                                    selectedClients.remove(Clients(nickname, "010-1111-1111"))
                                }
                            }
                        }
                        /*val item = friends.elements?.get(position)
                        item?.let {
                            selectedClients = kakaoFriendAdapter.getSelectedClients().toMutableList()
                        }*/
                    }
                })


            }
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.postKakaoPhoneClient(PostKakaoPhoneRequest(selectedClients, 10))
            Log.d("select", selectedClients.toString())
            viewModel.postKakaoPhoneClient.observe(this, Observer {

            })
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, SettingFragment()).addToBackStack(null).commit()
        }


    }
}