package com.example.gajamap.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.GroupInfoResponse
import com.example.gajamap.databinding.ActivityAddDirectBinding
import com.example.gajamap.ui.fragment.map.MapFragment
import com.example.gajamap.viewmodel.ClientViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddDirectActivity : BaseActivity<ActivityAddDirectBinding>(R.layout.activity_add_direct) {
    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    private var groupId : Int = -1
    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@AddDirectActivity
        binding.viewModel = this.viewModel
    }

    var imageFile : File? = null
    private var isBtnActivated = false // 버튼 활성화 되었는지 여부, true면 활성화, false면 비활성화
    private var isCamera = false

    companion object {
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1
    }
    override fun onCreateAction() {
        binding.topBackBtn.setOnClickListener {
            finish()
        }
        //주소 데이터 가져오기
        val address = GajaMapApplication.prefs.getString("address", "")
        binding.infoProfileAddressTv1.text = address

        //스피너
        viewModel.checkGroup()
        viewModel.checkGroup.observe(this, Observer {
            // GroupResponse에서 GroupInfoResponse의 groupName 속성을 추출하여 리스트로 변환합니다.
            val groupNames = mutableListOf<String>()
            // groupResponse의 groupInfos에서 각 GroupInfoResponse의 groupName을 추출하여 리스트에 추가합니다.
            it.groupInfos.forEach { groupInfo ->
                groupNames.add(groupInfo.groupName)
            }
            //groupNames.add(groupNames.size, "그룹 선택")
            groupNames.add(0,"그룹 선택")
            //그룹 스피너
            /*val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, groupNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.infoProfileGroup.adapter = adapter*/
            val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_list, groupNames) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val textView = super.getView(position, convertView, parent) as TextView
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black)) // 검정색으로 변경
                    return textView
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val textView = super.getDropDownView(position, convertView, parent) as TextView
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black)) // 검정색으로 변경
                    return textView
                }

            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.infoProfileGroup.adapter = adapter

        })

        /*val itemList = listOf("그룹선택", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.infoProfileGroup.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/

        binding.infoProfileGroup.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                //binding.result.text = data[pos] //배열이라서 []로 된다.
                //textView를 위에서 선언한 리스트(data)와 연결. [pos]는 리스트에서 선택된 항목의 위치값.
                // 스피너에서 선택한 아이템의 그룹 아이디를 가져옵니다.
                //if (pos == 0) return

                if(pos != 0){
                    val selectedGroupInfoResponse: GroupInfoResponse = viewModel.checkGroup.value?.groupInfos?.get(pos - 1) ?: return
                    groupId = selectedGroupInfoResponse.groupId
                    Log.d("groupId", groupId.toString())
                    GajaMapApplication.prefs.setString("groupIdSpinner", groupId.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.infoProfileCameraBtn.setOnClickListener {
            selectGallery()
            isCamera = true
        }
        if(!isCamera){
            sendImage1()
        }

        chkInputData()
        onContentAdd()

        binding.topBackBtn.setOnClickListener {
            // 지도 fragment로 이동
            finish()
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
        if (result.resultCode == Activity.RESULT_OK){
            // 이미지를 받으면 ImageView에 적용
            val imageUri = result.data?.data
            Log.d("imgUrl", imageUri.toString())
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
        val cursor = contentResolver.query(uri, proj, null,null,null)
        if(cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }
    // 갤러리를 부르는 메서드
    private fun selectGallery(){
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        // 권한 확인
        if(writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED){
            // 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
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
            val groupId1 = GajaMapApplication.prefs.getString("groupIdSpinner", "")
            val groupId = groupId1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber1 = binding.infoProfilePhoneEt.text
            val phoneNumber = phoneNumber1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val mainAddress1 = GajaMapApplication.prefs.getString("address", "")
            val mainAddress = mainAddress1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val detail1 = binding.infoProfileAddressTv2.text
            val detail = detail1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude1 = GajaMapApplication.prefs.setString("latitude", "")
            Log.d("send", latitude1.toString())
            val latitude = latitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude1 = GajaMapApplication.prefs.setString("longtitude", "")
            Log.d("send", longitude1.toString())
            val longitude = longitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isBasicImage1 = false
            val isBasicImage = isBasicImage1.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.postClient( clientName, groupId, phoneNumber, mainAddress , detail, latitude, longitude, clientImage, isBasicImage)
            viewModel.postClient.observe(this, Observer {
                Log.d("postAddDirect", it.body().toString())
            })
            finish()
            //parentFragmentManager.beginTransaction().replace(R.id.nav_fl, MapFragment()).commit()
        }

    }

    private fun sendImage1(){
        //확인 버튼
        binding.btnSubmit.setOnClickListener {
            val clientName1 = binding.infoProfileNameEt.text
            val clientName = clientName1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val groupId1 = GajaMapApplication.prefs.getString("groupIdSpinner", "")
            val groupId = groupId1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber1 = binding.infoProfilePhoneEt.text
            val phoneNumber = phoneNumber1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val mainAddress1 = GajaMapApplication.prefs.getString("address", "")
            val mainAddress = mainAddress1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val detail1 = binding.infoProfileAddressTv2.text
            val detail = detail1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude1 = GajaMapApplication.prefs.setString("latitude", "")
            val latitude = latitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude1 = GajaMapApplication.prefs.setString("longtitude", "")
            val longitude = longitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isBasicImage1 = true
            val isBasicImage = isBasicImage1.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.postClient( clientName, groupId, phoneNumber, mainAddress , detail, latitude, longitude, null, isBasicImage)
            viewModel.postClient.observe(this, Observer {
                Log.d("postAddDirect", it.body().toString())
            })
            finish()
            // parentFragmentManager.beginTransaction().replace(R.id.nav_fl, MapFragment()).commit()
        }

    }
}