package com.example.gajamap.ui.fragment.customerAdd

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.base.UserData
import com.example.gajamap.base.UserData.clientListResponse
import com.example.gajamap.data.model.Client
import com.example.gajamap.data.model.GroupInfoResponse
import com.example.gajamap.databinding.FragmentEditProfileBinding
import com.example.gajamap.ui.fragment.map.MapFragment
import com.example.gajamap.ui.fragment.setting.SettingFragment
import com.example.gajamap.viewmodel.ClientViewModel
import com.kakao.sdk.user.model.User
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
    var clientList =  UserData.clientListResponse?.clients as? MutableList<Client>

    private var groupId : Int = -1
    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@EditProfileFragment
        binding.fragment = this@EditProfileFragment
    }

    var imageFile : File? = null
    private var isCamera = false

    companion object {
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1
    }

    override fun onCreateAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
            }
        })

        binding.topBackBtn.setOnClickListener {
            // todo: editprofilefragment도 activity로 변경 후 아래 코드 수정해야 함
            // parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
            parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
        }

        //스피너
        /*viewModel.checkGroup()
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
            val adapter = object : ArrayAdapter<String>(requireActivity(), R.layout.spinner_list, groupNames) {

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

        binding.infoProfileGroup.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
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
        }*/

        val name = GajaMapApplication.prefs.getString("name", "")
        val address1 = GajaMapApplication.prefs.getString("address1", "")
        val address2 = GajaMapApplication.prefs.getString("address2", "")
        val phone = GajaMapApplication.prefs.getString("phone", "")
        binding.infoProfileNameEt.setText(name)
        binding.infoProfileAddressTv1.text = address1
        binding.infoProfileAddressTv2.setText(address2)
        binding.infoProfilePhoneEt.setText(phone)

        binding.infoProfileCameraBtn.setOnClickListener {
            selectGallery()
            isCamera = true
        }
        if(!isCamera){
            sendImage1()
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
                EditProfileFragment.REQ_GALLERY
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
            val clientId = GajaMapApplication.prefs.getString("clientId", "")
            val clientName1 = binding.infoProfileNameEt.text
            Log.d("edit", clientName1.toString())
            val clientName = clientName1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val groupId1 = GajaMapApplication.prefs.getString("groupId", "")
            Log.d("edit", groupId1.toString())
            val groupId = groupId1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber1 = binding.infoProfilePhoneEt.text
            Log.d("edit", phoneNumber1.toString())
            val phoneNumber = phoneNumber1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val mainAddress1 = GajaMapApplication.prefs.getString("address1", "")
            Log.d("edit", mainAddress1.toString())
            val mainAddress = mainAddress1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val detail1 = binding.infoProfileAddressTv2.text
            Log.d("edit", detail1.toString())
            val detail = detail1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude1 = GajaMapApplication.prefs.getString("latitude1", "")
            Log.d("edit", latitude1.toString())
            val latitude = latitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude1 = GajaMapApplication.prefs.getString("longitude1", "")
            val longitude = longitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isBasicImage1 = false
            val isBasicImage = isBasicImage1.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.putClient( groupId1.toInt(), clientId.toInt(), clientName, groupId, phoneNumber, mainAddress , detail, latitude, longitude, clientImage, isBasicImage)
            viewModel.putClient.observe(viewLifecycleOwner, Observer {
                Log.d("editwhy", it.toString())
                // 클라이언트 리스트 가져오기
                //val clientList = UserData.clientListResponse
                val targetClientId = GajaMapApplication.prefs.getString("clientId", "")

                // 클라이언트 리스트가 null이 아니고, clients가 null이 아닌 경우에만 처리
                clientList?.let { clients ->
                    // 특정 clientId에 해당하는 클라이언트 찾기
                    val targetClient = clients.find { it.clientId == targetClientId.toInt() }

                    // 해당 clientId의 클라이언트를 찾았을 경우 값 변경
                    targetClient?.apply {
                        this.clientId = it.clientId
                        this.groupInfo.groupId = it.groupInfo.groupId
                        this.groupInfo.groupName = it.groupInfo.groupName
                        this.clientName = it.clientName
                        this.phoneNumber = it.phoneNumber
                        this.address.mainAddress = it.address.mainAddress
                        this.address.detail = it.address.detail
                        this.location.latitude = it.location.latitude
                        this.location.longitude = it.location.longitude
                        this.image.filePath = it.image.filePath
                        this.image.originalFileName = it.image.originalFileName
                        this.distance = it.distance
                        this.createdAt = it.createdAt

                        // 변경된 클라이언트 정보를 클라이언트 리스트에 업데이트
                        clientList!!.indexOf(this).let { index ->
                            clientList!![index] = this
                        }
                        val bundle = Bundle()
                        bundle.putString("clientName", this.clientName)
                        bundle.putString("address1", it.address.mainAddress)
                        bundle.putString("address2", it.address.detail)
                        bundle.putString("phone", it.phoneNumber)
                        bundle.putString("image", it.image.filePath)

                        val customerInfoFragment = CustomerInfoFragment()
                        customerInfoFragment.arguments = bundle
                        parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
                    }
                }
                Log.d("editlist", clientList.toString())
                //parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
            })
            //parentFragmentManager.beginTransaction().replace(R.id.nav_fl, MapFragment()).commit()
            //parentFragmentManager.beginTransaction().remove(EditProfileFragment()).commit()
        }

    }

    private fun sendImage1(){
        //확인 버튼
        binding.btnSubmit.setOnClickListener {
            val clientId = GajaMapApplication.prefs.getString("clientId", "")
            val clientName1 = binding.infoProfileNameEt.text
            Log.d("edit", clientName1.toString())
            val clientName = clientName1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val groupId1 = GajaMapApplication.prefs.getString("groupId", "")
            Log.d("edit", groupId1.toString())
            val groupId = groupId1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber1 = binding.infoProfilePhoneEt.text
            Log.d("edit", phoneNumber1.toString())
            val phoneNumber = phoneNumber1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val mainAddress1 = GajaMapApplication.prefs.getString("address1", "")
            Log.d("edit", mainAddress1.toString())
            val mainAddress = mainAddress1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val detail1 = binding.infoProfileAddressTv2.text
            Log.d("edit", detail1.toString())
            val detail = detail1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude1 = GajaMapApplication.prefs.getString("latitude1", "")
            Log.d("edit", latitude1.toString())
            val latitude = latitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude1 = GajaMapApplication.prefs.getString("longitude1", "")
            val longitude = longitude1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isBasicImage1 = true
            val isBasicImage = isBasicImage1.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.putClient( groupId1.toInt(), clientId.toInt(), clientName, groupId, phoneNumber, mainAddress , detail, latitude, longitude, null, isBasicImage)
            viewModel.putClient.observe(viewLifecycleOwner, Observer {
                Log.d("editwhy", it.toString())
                Log.d("postAddDirect", it.toString())
                // 클라이언트 리스트 가져오기
                //val clientList = UserData.clientListResponse
                val targetClientId = GajaMapApplication.prefs.getString("clientId", "")

                // 클라이언트 리스트가 null이 아니고, clients가 null이 아닌 경우에만 처리
                clientList?.let { clients ->
                    // 특정 clientId에 해당하는 클라이언트 찾기
                    val targetClient = clients.find { it.clientId == targetClientId.toInt() }

                    // 해당 clientId의 클라이언트를 찾았을 경우 값 변경
                    targetClient?.apply {
                        this.clientId = it.clientId
                        this.groupInfo.groupId = it.groupInfo.groupId
                        this.groupInfo.groupName = it.groupInfo.groupName
                        this.clientName = it.clientName
                        this.phoneNumber = it.phoneNumber
                        this.address.mainAddress = it.address.mainAddress
                        this.address.detail = it.address.detail
                        this.location.latitude = it.location.latitude
                        this.location.longitude = it.location.longitude
                        this.image.filePath = it.image.filePath
                        this.image.originalFileName = it.image.originalFileName
                        this.distance = it.distance
                        this.createdAt = it.createdAt

                        // 변경된 클라이언트 정보를 클라이언트 리스트에 업데이트
                        clientList!!.indexOf(this).let { index ->
                            clientList!![index] = this
                        }
                        GajaMapApplication.prefs.setString("name", this.clientName)
                        GajaMapApplication.prefs.setString("address1", this.address.mainAddress)
                        GajaMapApplication.prefs.setString("address2", this.address.detail)
                        GajaMapApplication.prefs.setString("phone", this.phoneNumber)
                        this.image.filePath?.let { it1 ->
                            GajaMapApplication.prefs.setString("image",
                                it1
                            )
                        }
                        /*val bundle = Bundle()
                        bundle.putString("clientName", this.clientName)
                        bundle.putString("address1", it.address.mainAddress)
                        bundle.putString("address2", it.address.detail)
                        bundle.putString("phone", it.phoneNumber)
                        bundle.putString("image", it.image.filePath)

                        val customerInfoFragment = CustomerInfoFragment()
                        customerInfoFragment.arguments = bundle
                        parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()*/
                    }
                }
                Log.d("editlist", clientList.toString())
                parentFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
            })
            //parentFragmentManager.beginTransaction().replace(R.id.nav_fl, MapFragment()).commit()
            //parentFragmentManager.beginTransaction().remove(EditProfileFragment()).commit()
        }

    }
}