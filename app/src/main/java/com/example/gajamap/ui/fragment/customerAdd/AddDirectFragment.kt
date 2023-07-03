package com.example.gajamap.ui.fragment.customerAdd

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentAddDirectBinding
import com.example.gajamap.viewmodel.AddViewModel
import java.io.File

class AddDirectFragment: BaseFragment<FragmentAddDirectBinding>(R.layout.fragment_add_direct) {

    override val viewModel by viewModels<AddViewModel> {
        AddViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@AddDirectFragment
        binding.fragment = this@AddDirectFragment
    }
    var imageFile : File? = null
    private var isBtnActivated = false // 버튼 활성화 되었는지 여부, true면 활성화, false면 비활성화

    companion object {
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1
    }

    override fun onCreateAction() {
        //스피너
        val itemList = listOf("서울특별시 고객들", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.infoProfileGroup.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.infoProfileGroup.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                //binding.result.text = data[pos] //배열이라서 []로 된다.
                //textView를 위에서 선언한 리스트(data)와 연결. [pos]는 리스트에서 선택된 항목의 위치값.

                if(pos != 0) Toast.makeText(requireContext(), itemList[pos], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }



        binding.infoProfileCameraBtn.setOnClickListener {
            selectGallery()
        }
        chkInputData()
        onContentAdd()

        binding.topBackBtn.setOnClickListener {

        }
    }

    // 필수 입력사항에 값이 변경될 때 확인 버튼 활성화 시킬 함수 호출
    private fun onContentAdd(){
        binding.infoProfileNameEt.addTextChangedListener {
            chkBtnActivate()
        }
        binding.infoProfilePhoneEt.addTextChangedListener{
            chkBtnActivate()
        }
    }

    private fun chkInputData() = binding.infoProfileNameEt.text.isNotEmpty() && binding.infoProfilePhoneEt.text.isNotEmpty()

    // 필수 입력사항을 모두 작성하였을 때 확인 버튼 활성화시키기
    private fun chkBtnActivate() {
        // 버튼이 활성화되어 있지 않은 상황에서 확인
        if (!isBtnActivated && chkInputData()) {
            isBtnActivated = !isBtnActivated
            binding.btnSubmit.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.fragment_add_bottom_purple)
            }
        }
    }

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
                    .into(binding.infoProfileImg)
            }
        }
    }
    // 이미지 실제 경로 반환
    fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if(buildName.equals("Xiaomi")){
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, proj, null,null,null)
        if(cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }
    // 갤러리를 부르는 메서드
    private fun selectGallery(){
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

        // 권한 확인
        if(writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED){
            // 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
        }else{
            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // intent의 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*"
            )
            imageResult.launch(intent)
        }
    }
}