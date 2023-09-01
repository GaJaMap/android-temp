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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.example.gajamap.base.UserData
import com.example.gajamap.data.model.*
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.ui.adapter.CustomerListAdapter
import com.example.gajamap.ui.view.AddDirectActivity
import com.example.gajamap.ui.view.CustomerInfoActivity
import com.example.gajamap.ui.view.EditListActivity
import com.example.gajamap.viewmodel.GetClientViewModel
import com.kakao.sdk.navi.Constants
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : BaseFragment<FragmentListBinding> (R.layout.fragment_list) {
    // 검색창 dropdown list
    //var searchList : Array<String> = emptyArray()
    private var groupId : Int = -1
    private var radius = 0
    private var clientList = UserData.clientListResponse
    private var groupInfo = UserData.groupinfo

    private val ACCESS_FINE_LOCATION = 1000
    private val CALL_PHONE_PERMISSION_CODE = 101

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
        CoroutineScope(Dispatchers.IO).launch {
            setView()
        }

        //리사이클러뷰
        binding.listRv.addItemDecoration(CustomerListVerticalItemDecoration())

        binding.fragmentEditBtn.setOnClickListener {
            // 고객 편집하기 activity로 이동
            val intent = Intent(activity, EditListActivity::class.java)
            startActivity(intent)
        }

        //최신순은 보라색으로 시작
        binding.fragmentListCategory1.setBackgroundResource(R.drawable.list_distance_purple)
        binding.fragmentListCategory3.setBackgroundResource(R.drawable.fragment_list_category_background)
        binding.fragmentListCategory2.setBackgroundResource(R.drawable.fragment_list_category_background)
        //binding.radiusSpinner.setBackgroundResource(R.drawable.fragment_list_category_background)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                //입력이 끝날 때 작동됩니다.
                val searchText = editable.toString().trim()
                filterClientList(searchText)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력 하기 전에 작동됩니다.

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //타이핑 되는 텍스트에 변화가 있으면 작동됩니다.

            }
        })

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


    private suspend fun setView(){
        withContext(Dispatchers.Main){
            /*viewModel.getAllClient()
        viewModel.getAllClient.observe(this, Observer {
            ListRv(it)
        })*/
            // 자동 로그인 response 데이터 값 받아오기
            //clientList = UserData.clientListResponse
            //groupInfo = UserData.groupinfo
            clientList?.let { ListRv(it) }

            if (groupInfo != null) {
                binding.spinnerSearch.text = groupInfo!!.groupName
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            updateData()
        }
    }

    private suspend fun updateData() {
        withContext(Dispatchers.Main) {
            // 자동 로그인 response 데이터 값 받아오기
            clientList = UserData.clientListResponse
            groupInfo = UserData.groupinfo
            clientList?.let { ListRv(it) }

            if (groupInfo != null) {
                binding.spinnerSearch.text = groupInfo!!.groupName
            }
            Log.d("deleteupdate", clientList.toString())
        }
    }

    fun ListRv(it : GetAllClientResponse){
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
                val groupId = it.clients[position].groupInfo.groupId
                val groupName = it.clients[position].groupInfo.groupName
                it.clients[position].image.filePath?.let { it1 ->
                    GajaMapApplication.prefs.setString("image",
                        it1
                    )
                }
                GajaMapApplication.prefs.setString("groupName", groupName.toString())
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
                val intent = Intent(activity, CustomerInfoActivity::class.java)
                startActivity(intent)
            }
        })

        //내비게이션
        customerListAdapter.setItemClickListener(object :
            CustomerListAdapter.ItemClickListener{
            override fun onClick(v: View, position: Int) {
                val latitude = it.clients[position].location.latitude
                val longitude = it.clients[position].location.longitude
                val name = it.clients[position].clientName
                Log.d("navi", latitude.toString())
                Log.d("navi", longitude.toString())
                //카카오내비
                // 카카오내비 앱으로 길 안내
                if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
                    // 카카오내비 앱으로 길 안내 - WGS84
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            //위도 경도를 장소이름으로 바꿔주기
                            Location(name, longitude.toString(), latitude.toString()),
                            NaviOption(coordType = CoordType.WGS84)
                        )
                    )
                } else {
                    // 카카오내비 설치 페이지로 이동
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.WEB_NAVI_INSTALL)
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }

            }

        })

    }


    /*fun GroupClientSearchRV(it : GetGroupAllClientResponse){
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
                val groupName = it.clients[position].groupInfo.groupName
                GajaMapApplication.prefs.setString("groupName", groupName.toString())
                GajaMapApplication.prefs.setString("clientId", clientId.toString())
                GajaMapApplication.prefs.setString("groupId", groupId.toString())
                it.clients[position].image.filePath?.let { it1 ->
                    GajaMapApplication.prefs.setString("image",
                        it1
                    )
                }
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
                val intent = Intent(activity, CustomerInfoActivity::class.java)
                startActivity(intent)
            }
        })

        //내비게이션
        customerListAdapter.setItemClickListener(object :
            CustomerListAdapter.ItemClickListener{
            override fun onClick(v: View, position: Int) {
                val latitude = it.clients[position].location.latitude
                val longitude = it.clients[position].location.longitude
                val name = it.clients[position].clientName
                //카카오내비
                // 카카오내비 앱으로 길 안내
                if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
                    // 카카오내비 앱으로 길 안내 - WGS84
                    startActivity(
                        NaviClient.instance.navigateIntent(
                            //위도 경도를 장소이름으로 바꿔주기
                            Location(name, longitude.toString(), latitude.toString()),
                            NaviOption(coordType = CoordType.WGS84)
                        )
                    )
                } else {
                    // 카카오내비 설치 페이지로 이동
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.WEB_NAVI_INSTALL)
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }

            }

        })
    }*/

    fun filterClientList(searchText: String) {

        val filteredList = clientList?.clients?.filter { client ->
            // 여기에서 clientName을 검색합니다. 대소문자를 무시하려면 equals를 equalsIgnoreCase로 바꿀 수 있습니다.
            client.clientName.contains(searchText, ignoreCase = true)
        }
        val customerListAdapter = clientList?.let { CustomerListAdapter(it.clients) }
        binding.listRv.apply {
            adapter = customerListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        // 필터링된 결과를 리사이클러뷰 어댑터에 설정합니다.
        if (filteredList != null) {
            customerListAdapter?.updateData(filteredList)
        }
    }
}