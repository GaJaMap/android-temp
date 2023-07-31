package com.example.gajamap.ui.fragment.customerAdd

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.ListFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.data.model.BaseResponse
import com.example.gajamap.data.model.PutClientRequest
import com.example.gajamap.databinding.FragmentEditProfileBinding
import com.example.gajamap.ui.fragment.setting.SettingFragment
import com.example.gajamap.viewmodel.ClientViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {

    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@EditProfileFragment
        binding.fragment = this@EditProfileFragment
    }

    var imageFile : File? = null

    companion object {
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1
    }

    override fun onCreateAction() {
        binding.topBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
        }

        binding.btnSubmit.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
        }

        //스피너
        val itemList = listOf("그룹선택", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.infoProfileGroup.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.infoProfileGroup.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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

    }

    // 이미지를 결과값으로 받는 변수
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            // 이미지를 받으면 ImageView에 적용
            val imageUri = result.data?.data
            Log.d("img", imageUri.toString())
            imageUri?.let{
                // 서버 업로드를 위해 파일 형태로 변환
                val file = File(getRealPathFromURI(it))
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("clientImage", file.name, requestFile)
                sendImage(body)

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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                AddDirectFragment.REQ_GALLERY
            )
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



    private fun sendImage(clientImage: MultipartBody.Part){
        //확인 버튼
        binding.btnSubmit.setOnClickListener {
            val clientName1 = binding.infoProfileNameEt.text
            val clientName = clientName1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val groupId1 = 10
            val groupId = groupId1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber1 = binding.infoProfilePhoneEt.text
            val phoneNumber = phoneNumber1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val mainAddress1 = "서울시 노원구 상상상상"
            val mainAddress = mainAddress1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val detail1 = "2층 205호"
            val detail = detail1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude1 = 33.12345
            val latitude = latitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude1 = 127.7777
            val longitude = longitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isBasicImage1 = false
            val isBasicImage = isBasicImage1.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.putClient(10, 78, clientName, groupId, phoneNumber, mainAddress , detail, latitude, longitude, clientImage, isBasicImage)
            viewModel.putClient.observe(viewLifecycleOwner, Observer {
                Log.d("edit", it.toString())
            })
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
        }

    }
}