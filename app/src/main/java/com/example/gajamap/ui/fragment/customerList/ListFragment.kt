package com.example.gajamap.ui.fragment.customerList

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.*
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.ui.adapter.CustomerListAdapter
import com.example.gajamap.ui.view.AddDirectActivity
import com.example.gajamap.ui.view.CustomerInfoActivity
import com.example.gajamap.ui.view.EditListActivity
import com.example.gajamap.viewmodel.GetClientViewModel

class ListFragment : BaseFragment<FragmentListBinding> (R.layout.fragment_list) {
    // 검색창 dropdown list
    //var searchList : Array<String> = emptyArray()
    private var groupId : Int = -1
    private var radius = 0

    private val ACCESS_FINE_LOCATION = 1000
    private val CALL_PHONE_PERMISSION_CODE = 101

    private var cate1 = false
    private var cate2 = false
    private val cate3 = false

    /*val viewModel2 by viewModels<MapViewModel> {
        //ClientViewModel.SettingViewModelFactory("tmp")
        MapViewModel.MapViewModelFactory("tmp")
    }*/

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@ListFragment
        binding.fragment = this@ListFragment
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateAction() {
        //리사이클러뷰
        binding.listRv.addItemDecoration(CustomerListVerticalItemDecoration())
        /*viewModel.getGroupAllClient(10)
        viewModel.getGroupAllClient.observe(viewLifecycleOwner, Observer {
            GroupClientSearchRV(it)
        })*/
        viewModel.getAllClient()
        viewModel.getAllClient.observe(this, Observer {
            ListRv(it)
        })
        /*if(groupIdLogin != null){
            viewModel.getAllClient()
            viewModel.getAllClient.observe(this, Observer {
                ListRv(it)
            })
        }
        else {
            viewModel.getGroupAllClient(groupIdLogin)
            viewModel.getGroupAllClient.observe(viewLifecycleOwner, Observer {
                GroupClientSearchRV(it)
            })
        }*/

        binding.fragmentEditBtn.setOnClickListener {
            // 고객 편집하기 activity로 이동
            val intent = Intent(getActivity(), EditListActivity::class.java)
            startActivity(intent)
        }

        //최신순은 보라색으로 시작
        binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
        binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
        binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
        //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)

        // todo: 나중에 서버 연동 후 값 받아와서 넣어주는 것으로 수정 예정
        viewModel.checkGroup()
        viewModel.checkGroup.observe(this, Observer {
            // GroupResponse에서 GroupInfoResponse의 groupName 속성을 추출하여 리스트로 변환합니다.
            val groupNames = mutableListOf<String>()
            // "전체"를 리스트의 첫 번째 요소로 추가합니다.
            groupNames.add("전체")
            // groupResponse의 groupInfos에서 각 GroupInfoResponse의 groupName을 추출하여 리스트에 추가합니다.
            it.groupInfos.forEach { groupInfo ->
                groupNames.add(groupInfo.groupName)
            }
            //그룹 스피너
            val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, groupNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSearch.adapter = adapter
        })

        binding.spinnerSearch.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
               // 스피너에서 선택한 아이템의 그룹 아이디를 가져옵니다.
                if(position != 0){
                    val selectedGroupInfoResponse: GroupInfoResponse = viewModel.checkGroup.value?.groupInfos?.get(position - 1) ?: return
                    groupId = selectedGroupInfoResponse.groupId
                    Log.d("groupId", groupId.toString())
                    GajaMapApplication.prefs.setString("groupIdSpinner", groupId.toString())
                }

                binding.etSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        //입력이 끝날 때 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.getAllClientName(searchName.toString())
                            viewModel.getAllClientName.observe(viewLifecycleOwner, Observer {
                                ListRv(it)
                            })
                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.getGroupAllClientName(searchName.toString(), groupId)
                            viewModel.getGroupAllClientName.observe(viewLifecycleOwner, Observer {
                                GroupClientSearchRV(it)
                            })
                        }
                    }
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //입력 하기 전에 작동됩니다.
                        if(position == 0){
                            viewModel.getGroupAllClient(groupId)
                            viewModel.getGroupAllClient.observe(viewLifecycleOwner, Observer {
                                GroupClientSearchRV(it)
                            })
                        }
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //타이핑 되는 텍스트에 변화가 있으면 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.getAllClientName(searchName.toString())
                            viewModel.getAllClientName.observe(viewLifecycleOwner, Observer {
                                ListRv(it)
                            })
                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.getGroupAllClientName(searchName.toString(), groupId)
                            viewModel.getGroupAllClientName.observe(viewLifecycleOwner, Observer {
                                GroupClientSearchRV(it)
                            })
                        }
                    }
                })
                Toast.makeText(requireContext(), "클릭클릭클릭", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //반경 스피너
        /*val userLatitude = GajaMapApplication.prefs.getString("userLatitude", "")
        val userLongitude = GajaMapApplication.prefs.getString("userLongitude", "")

        val itemList = listOf("반경", "3KM", "5KM")
        val adapterRadius = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.radiusSpinner.adapter = adapterRadius
        adapterRadius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.radiusSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 스피너에서 선택한 아이템의 그룹 아이디를 가져옵니다.
                if(position != 0){
                    val selectedGroupInfoResponse: GroupInfoResponse = viewModel.checkGroup.value?.groupInfos?.get(position - 1) ?: return
                    groupId = selectedGroupInfoResponse.groupId
                    Log.d("groupId", groupId.toString())
                }
                if(position != 0){
                    binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
                    binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
                    binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
                    binding.radiusSpinner.setBackgroundResource(R.drawable.list_distance_purple)
                }

                if(position == 1){
                    radius = 3000
                }
                if(position == 2){
                    radius = 5000
                }

                binding.etSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        //입력이 끝날 때 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.allNameRadius(searchName.toString(), radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.allNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupNameRadius(groupId, searchName.toString(), radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.groupNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                    }
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //입력 하기 전에 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.allRadius( radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.allRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupRadius(groupId, radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.groupRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }

                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //타이핑 되는 텍스트에 변화가 있으면 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.allNameRadius(searchName.toString(), radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.allNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupNameRadius(groupId, searchName.toString(), radius.toDouble(), userLatitude.toDouble(), userLongitude.toDouble())
                            viewModel.groupNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                    }
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }*/

        //GPS 위치권한
        binding.fragmentListCategory3.setOnClickListener {
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.list_distance_purple)
            //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                permissionCheck()
            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("확인") { dialog, which ->

                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            // 위치추적 시작하는 코드 추가
        }
    }


    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(requireContext(), "위치 권한 승인", Toast.LENGTH_SHORT).show()
                //startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(requireContext(), "위치 권한 거절", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    fun listRadius(it : GetRadiusResponse){
        //고객 리스트
        val customerListAdapter = CustomerListAdapter(it.clients)
        binding.listRv.apply {
            adapter = customerListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.fragmentListCategory1.setOnClickListener {view->
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
            //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            customerListAdapter.updateData(it.clients)
        }
        binding.fragmentListCategory2.setOnClickListener {view->
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
            //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            val reversedList = it.clients.reversed()
            customerListAdapter.updateData(reversedList)
        }

        //리사이클러뷰 클릭
        customerListAdapter.setOnItemClickListener(object :
            CustomerListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val clientId = it.clients[position].clientId
                GajaMapApplication.prefs.setString("clientId", clientId.toString())
                val name = it.clients[position].clientName
                val address1 = it.clients[position].address.mainAddress
                val address2 = it.clients[position].address.detail
                val phone = it.clients[position].phoneNumber
                val latitude = it.clients[position].location.latitude
                val longitude = it.clients[position].location.longitude
                GajaMapApplication.prefs.setString("name", name)
                GajaMapApplication.prefs.setString("address1", address1)
                GajaMapApplication.prefs.setString("address2", address2)
                GajaMapApplication.prefs.setString("phone", phone)
                GajaMapApplication.prefs.setString("latitude1", latitude.toString())
                GajaMapApplication.prefs.setString("longitude1", longitude.toString())

                // 고객 상세 정보 activity로 이동
                val intent = Intent(getActivity(), CustomerInfoActivity::class.java)
                startActivity(intent)
            }
        })
    }

    fun ListRv(it : GetAllClientResponse){
        GajaMapApplication.prefs.setString("imageUrlPrefix", it.imageUrlPrefix.toString())
        //고객 리스트
        val customerListAdapter = CustomerListAdapter(it.clients)
        binding.listRv.apply {
            adapter = customerListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.fragmentListCategory1.setOnClickListener {view->
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
            //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            customerListAdapter.updateData(it.clients)
        }
        binding.fragmentListCategory2.setOnClickListener {view->
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
            //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            val reversedList = it.clients.reversed()
            customerListAdapter.updateData(reversedList)
        }

        //리사이클러뷰 클릭
        customerListAdapter.setOnItemClickListener(object :
            CustomerListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val name = it.clients[position].clientName
                val address1 = it.clients[position].address.mainAddress
                val address2 = it.clients[position].address.detail
                val phone = it.clients[position].phoneNumber
                val latitude = it.clients[position].location.latitude
                val longitude = it.clients[position].location.longitude
                GajaMapApplication.prefs.setString("name", name)
                GajaMapApplication.prefs.setString("address1", address1)
                GajaMapApplication.prefs.setString("address2", address2)
                GajaMapApplication.prefs.setString("phone", phone)
                GajaMapApplication.prefs.setString("latitude1", latitude.toString())
                GajaMapApplication.prefs.setString("longitude1", longitude.toString())

                // 고객 상세 정보 activity로 이동
                val intent = Intent(getActivity(), CustomerInfoActivity::class.java)
                startActivity(intent)
            }
        })
    }


    fun GroupClientSearchRV(it : GetGroupAllClientResponse){
        GajaMapApplication.prefs.setString("imageUrlPrefix", it.imageUrlPrefix.toString())
        //고객 리스트
        val customerListAdapter = CustomerListAdapter(it.clients)
        binding.listRv.apply {
            adapter = customerListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.fragmentListCategory1.setOnClickListener {view->
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
            customerListAdapter.updateData(it.clients)
        }
        binding.fragmentListCategory2.setOnClickListener {view->
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
            val reversedList = it.clients.reversed()
            customerListAdapter.updateData(reversedList)
        }

        //리사이클러뷰 클릭
        customerListAdapter.setOnItemClickListener(object :
            CustomerListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val clientId = it.clients[position].clientId
                val groupId = it.clients[position].groupInfo.groupId
                GajaMapApplication.prefs.setString("clientId", clientId.toString())
                GajaMapApplication.prefs.setString("groupId", groupId.toString())
                val name = it.clients[position].clientName
                val address1 = it.clients[position].address.mainAddress
                val address2 = it.clients[position].address.detail
                val phone = it.clients[position].phoneNumber
                val latitude = it.clients[position].location.latitude
                val longitude = it.clients[position].location.longitude
                GajaMapApplication.prefs.setString("name", name)
                GajaMapApplication.prefs.setString("address1", address1)
                GajaMapApplication.prefs.setString("address2", address2)
                GajaMapApplication.prefs.setString("phone", phone)
                GajaMapApplication.prefs.setString("latitude1", latitude.toString())
                GajaMapApplication.prefs.setString("longitude1", longitude.toString())

                // 고객 상세 정보 activity로 이동
                val intent = Intent(getActivity(), CustomerInfoActivity::class.java)
                startActivity(intent)
            }
        })
    }
}