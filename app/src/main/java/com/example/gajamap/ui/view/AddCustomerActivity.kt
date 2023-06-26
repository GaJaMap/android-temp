package com.example.gajamap.ui.view

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.databinding.ActivityAddCustomerBinding
import com.example.gajamap.viewmodel.AddCustomerViewModel
import java.io.File

class AddCustomerActivity: BaseActivity<ActivityAddCustomerBinding>(R.layout.activity_add_customer) {
    override val viewModel by viewModels<AddCustomerViewModel> {
        AddCustomerViewModel.AddCustomerViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@AddCustomerActivity
        binding.viewModel = this.viewModel
    }

    var imageFile : File? = null

    companion object {
        const val REVIEW_MIN_LENGTH = 10
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1
        // API 호출시 Parameter Key 값
        const val PARAM_KEY_IMAGE = "image"
        const val PARAM_KEY_PRODUCT_ID = "product_id"
        const val PARAM_KEY_REVIEW = "review_content"
        const val PARAM_KEY_RATING = "rating"
    }

    override fun onCreateAction() {

    }
/*
    // 이미지를 결과값으로 받는 변수
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK){
            // 이미지를 받으면 ImageView에 적용
            val imageUri = result.data?.data
            imageUri?.let{
                // 서버 업로드를 위해 파일 형태로 변환
                imageFile = File(getRealPathFromURI(it))

                // 이미지를 불러온다
                Glide.with(this)
                    .load(imageUri)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .into(binding.ivProfile)
            }
        }
    }*/

}