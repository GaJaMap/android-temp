package com.example.gajamap.ui.fragment.customerList


import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.text.Editable
import android.text.TextWatcher
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
import com.example.gajamap.data.model.GetAllClientResponse
import com.example.gajamap.data.model.GetGroupAllClientResponse
import com.example.gajamap.data.model.GetGroupClientResponse
import com.example.gajamap.data.model.GetRadiusResponse
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.databinding.FragmentPhoneBinding
import com.example.gajamap.ui.adapter.CustomerListAdapter
import com.example.gajamap.ui.fragment.customerAdd.CustomerInfoFragment
import com.example.gajamap.viewmodel.GetClientViewModel

class ListFragment : BaseFragment<FragmentListBinding> (R.layout.fragment_list) {
    // 검색창 dropdown list
    var searchList : Array<String> = emptyArray()
    private var radius = 0

    private val ACCESS_FINE_LOCATION = 1000

    private var cate1 = false
    private var cate2 = false
    private val cate3 = false
    // Request Code

    //더미데이터
    /*private var customerList: ArrayList<Customer> = arrayListOf(
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km"),
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km"),
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km")
    )*/

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@ListFragment
        binding.fragment = this@ListFragment
    }

    override fun onCreateAction() {
        //리사이클러뷰
        binding.listRv.addItemDecoration(CustomerListVerticalItemDecoration())
        val groupIdLogin = GajaMapApplication.prefs.getString("groupIdLogin", "")
        if(groupIdLogin != null){
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
        }

        binding.fragmentEditBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, EditListFragment()).addToBackStack(null).commit()
        }

        //최신순은 보라색으로 시작
        binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
        binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
        binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
        binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)

        //반경 스피너
        val itemList = listOf("반경", "3KM", "5KM")
        val adapterRadius = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.radiusSpinner.adapter = adapterRadius
        adapterRadius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.radiusSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                            viewModel.allNameRadius(searchName.toString(), radius.toDouble(), 33.12345, 127.7777)
                            viewModel.allNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })

                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupNameRadius(10, searchName.toString(), radius.toDouble(), 33.12345, 127.7777)
                            viewModel.groupNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                    }
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //입력 하기 전에 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.allRadius( radius.toDouble(), 33.12345, 127.7777)
                            viewModel.allRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })

                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupRadius(10, radius.toDouble(), 33.12345, 127.7777)
                            viewModel.groupRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }

                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //타이핑 되는 텍스트에 변화가 있으면 작동됩니다.
                        if(position == 0){
                            val searchName = binding.etSearch.text
                            viewModel.allNameRadius(searchName.toString(), radius.toDouble(), 33.12345, 127.7777)
                            viewModel.allNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })

                        }
                        if(position != 0){
                            val searchName = binding.etSearch.text
                            viewModel.groupNameRadius(10, searchName.toString(), radius.toDouble(), 33.12345, 127.7777)
                            viewModel.groupNameRadius.observe(viewLifecycleOwner, Observer {
                                listRadius(it)
                            })
                        }
                    }
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        // todo: 나중에 서버 연동 후 값 받아와서 넣어주는 것으로 수정 예정
        //그룹 스피너
        searchList = searchList.plus("전체")
        searchList = searchList.plus("서울특별시 고객들")
        val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, searchList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSearch.adapter = adapter
        binding.spinnerSearch.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

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
                            viewModel.getGroupAllClientName(searchName.toString(), 10)
                            viewModel.getGroupAllClientName.observe(viewLifecycleOwner, Observer {
                                GroupClientSearchRV(it)
                            })
                        }
                    }
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        //입력 하기 전에 작동됩니다.
                        if(position == 0){
                            viewModel.getGroupAllClient(10)
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
                            viewModel.getGroupAllClientName(searchName.toString(), 10)
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

        //최신순
        /*binding.fragmentListCategory1.setOnClickListener {

            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
        }
        //오래된순
        binding.fragmentListCategory2.setOnClickListener {
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
        }*/
        //GPS 위치권한
        binding.fragmentListCategory3.setOnClickListener {
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.list_distance_purple)
            binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
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

    private fun startTracking() {

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
            binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            customerListAdapter.updateData(it.clients)
        }
        binding.fragmentListCategory2.setOnClickListener {view->
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
            binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
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
                GajaMapApplication.prefs.setString("latitude", latitude.toString())
                GajaMapApplication.prefs.setString("longitude", longitude.toString())


                parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
            }
        })
    }

    fun ListRv(it : GetAllClientResponse){
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
            binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
            customerListAdapter.updateData(it.clients)
        }
        binding.fragmentListCategory2.setOnClickListener {view->
            binding.fragmentListCategory1.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
            binding.fragmentListCategory2.setBackgroundResource(R.drawable.list_distance_purple)
            binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)
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
                GajaMapApplication.prefs.setString("latitude", latitude.toString())
                GajaMapApplication.prefs.setString("longitude", longitude.toString())


                parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
            }
        })
    }


    fun GroupClientSearchRV(it : GetGroupAllClientResponse){

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
                GajaMapApplication.prefs.setString("latitude", latitude.toString())
                GajaMapApplication.prefs.setString("longitude", longitude.toString())


                parentFragmentManager.beginTransaction().replace(R.id.nav_fl, CustomerInfoFragment()).addToBackStack(null).commit()
            }
        })
    }



}