package com.example.gajamap.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.BuildConfig
import com.example.gajamap.BuildConfig.KAKAO_API_KEY
import com.example.gajamap.R
import com.example.gajamap.api.retrofit.KakaoSearchClient
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.data.model.GroupListData
import com.example.gajamap.data.model.LoginRequest
import com.example.gajamap.data.model.RadiusRequest
import com.example.gajamap.data.repository.GroupRepository
import com.example.gajamap.data.response.CheckGroupResponse
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.LocationSearchData
import com.example.gajamap.data.response.ResultSearchKeywordData
import com.example.gajamap.databinding.DialogAddGroupBottomSheetBinding
import com.example.gajamap.databinding.DialogGroupBinding
import com.example.gajamap.databinding.FragmentMapBinding
import com.example.gajamap.ui.adapter.GroupListAdapter
import com.example.gajamap.ui.adapter.LocationSearchAdapter
import com.example.gajamap.ui.fragment.customerAdd.AddDirectFragment
import com.example.gajamap.ui.view.MainActivity
import com.example.gajamap.viewmodel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapReverseGeoCoder.ReverseGeoCodingResultListener
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), MapView.POIItemEventListener, MapView.MapViewEventListener {
    // 그룹 리스트 recyclerview
    lateinit var groupListAdapter: GroupListAdapter
    val dataList = mutableListOf<GroupListData>()
    private val ACCESS_FINE_LOCATION = 1000   // Request Code
    var groupName: String = ""
    var pos: Int = 0
    // 검색창 dropdown list
    var searchList : Array<String> = emptyArray()
    var check = false
    var markerCheck = false
    // 지도에서 직접 추가하기를 위한 중심 위치 point
    private lateinit var marker: MapPOIItem
    private lateinit var reverseGeoCodingResultListener : ReverseGeoCodingResultListener
    // LocationSearch recyclerview
    private val locationSearchList = arrayListOf<LocationSearchData>()
    private val locationSearchAdapter = LocationSearchAdapter(locationSearchList)
    private var keyword = "" // 검색 키워드
    var countkm = 0
    var gid = 0

    override val viewModel by viewModels<MapViewModel> {
        MapViewModel.MapViewModelFactory()
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@MapFragment
        binding.fragment = this@MapFragment
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateAction() {
        binding.mapView.setMapViewEventListener(this)
        // GPS 권한 설정
        binding.ibGps.setOnClickListener {
            // gps 버튼 클릭 상태로 변경
            // 원을 유지한 상태로 drawable 색상만 변경할 때 사용
            val bgShape = binding.ibGps.background as GradientDrawable
            bgShape.setColor(resources.getColor(R.color.main))
            binding.ibGps.setImageResource(R.drawable.ic_white_gps)

            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                permissionCheck()
            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 그룹 더보기 바텀 다이얼로그 띄우기
        // todo: 나중에 서버 연동 후 값 받아와서 넣어주는 것으로 수정 예정
        searchList = searchList.plus("전체")
        searchList = searchList.plus("서울특별시 고객들")
        val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, searchList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSearch.adapter = adapter
        binding.spinnerSearch.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                // 그룹 조회 api 연동
                if (check == true && position == 0) {
                    val groupDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
                    val sheetView = DialogAddGroupBottomSheetBinding.inflate(layoutInflater)
                    // 그룹 조회 서버 연동 함수 호출
                    checkGroup()
                    groupListAdapter = GroupListAdapter(object : GroupListAdapter.GroupDeleteListener{
                        override fun click(id: Int, name: String, position: Int) {
                            // 그룹 삭제 dialog
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("해당 그룹을 삭제하시겠습니까?")
                                .setMessage("그룹을 삭제하시면 영구 삭제되어 복구할 수 없습니다.")
                                .setPositiveButton("확인",positiveButtonClick)
                                .setNegativeButton("취소", negativeButtonClick)
                            val alertDialog = builder.create()
                            alertDialog.show()
                            groupName = name
                            pos = position
                            gid = id
                            Log.d("deleteGId1", gid.toString())
                        }
                    }, object : GroupListAdapter.GroupEditListener{
                        override fun click2(id: Int, name: String, position: Int) {
                            // 그룹 수정 dialog
                            val mDialogView = DialogGroupBinding.inflate(layoutInflater)
                            val mBuilder = AlertDialog.Builder(requireContext())
                            val addDialog = mBuilder.create()
                            addDialog.setView(mDialogView.root)
                            addDialog.show()
                            gid = id
                            mDialogView.ivClose.setOnClickListener {
                                addDialog.dismiss()
                            }
                            mDialogView.btnDialogSubmit.setOnClickListener {
                                // todo: 확인 필요!, 그룹 수정 api 연동
                                modifyGroup(gid, mDialogView.etName.text.toString())
                                // todo : 확인 필요
                                checkGroup()
                                addDialog.dismiss()
                            }
                        }
                    })
                    sheetView.rvAddgroup.adapter = groupListAdapter

                    groupDialog.setContentView(sheetView.root)
                    groupDialog.show()

                    sheetView.btnAddgroup.setOnClickListener {
                        // 그룹 추가 dialog
                        val mDialogView = DialogGroupBinding.inflate(layoutInflater)
                        mDialogView.tvTitle.text = "그룹 추가하기"
                        val mBuilder = AlertDialog.Builder(requireContext())
                        val addDialog = mBuilder.create()
                        addDialog.setView(mDialogView.root)
                        addDialog.show()
                        mDialogView.ivClose.setOnClickListener {
                            addDialog.dismiss()
                        }
                        mDialogView.btnDialogSubmit.setOnClickListener {
                            // 그룹 생성 api 연동
                            createGroup(mDialogView.etName.text.toString())
                            // todo : 확인 필요
                            checkGroup()
                            addDialog.dismiss()
                        }
                    }
                }
                check = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        // 위도, 경도 값으로 주소 받기
        reverseGeoCodingResultListener = object : ReverseGeoCodingResultListener {
            override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder, addressString: String) {
                // 주소를 찾은 경우
                Log.d("ReverseGeocoding", "도로명 주소: $addressString")
                binding.tvLocationAddress.text = addressString

            }

            override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                // 호출에 실패한 경우
                Log.e("ReverseGeocoding", "주소를 찾을 수 없습니다.")
            }
        }

        binding.ibPlus.setOnClickListener{
            // plus 버튼 클릭 상태로 변경
            val bgShape = binding.ibPlus.background as GradientDrawable
            bgShape.setColor(resources.getColor(R.color.main))
            binding.ibPlus.setImageResource(R.drawable.ic_white_plus)

            // 화면 변경
            binding.clSearchWhole.visibility = View.GONE
            binding.clSearchLocation.visibility = View.VISIBLE
            binding.clLocation.visibility = View.VISIBLE
            binding.ibPlus.visibility = View.GONE
            binding.ibGps.visibility = View.GONE
            binding.ibKm.visibility = View.GONE

            // 지도에서 직접 추가하기 마커 위치
            val centerPoint = binding.mapView.mapCenterPoint
            marker = MapPOIItem()
            binding.mapView.setMapCenterPoint(centerPoint, true)
            marker.itemName = "Marker"
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.5665, 126.9780)
            marker.markerType = MapPOIItem.MarkerType.RedPin
            binding.mapView.addPOIItem(marker)
            val mapGeoCoder = MapReverseGeoCoder(KAKAO_API_KEY, marker.mapPoint, reverseGeoCodingResultListener, requireActivity())
            mapGeoCoder.startFindingAddress()
            markerCheck = true
        }

        binding.ibKm.setOnClickListener {
            val bgShape = binding.ibKm.background as GradientDrawable
            if (countkm % 2 == 0){
                // km 버튼 클릭 상태로 변경
                bgShape.setColor(resources.getColor(R.color.main))
                binding.ibKm.setImageResource(R.drawable.ic_white_km)
                binding.clKm.visibility = View.VISIBLE
                // todo : 전체 고객 대상 반경 검색 api => radius 값 변경 필요
                wholeRadius(3000.0, 33.12345, 127.7777)

            }
            else{ // 두 번 클릭 시 원상태로 돌아오게 하기
                bgShape.setColor(resources.getColor(R.color.white))
                binding.ibKm.setImageResource(R.drawable.ic_km)
                binding.clKm.visibility = View.INVISIBLE
            }
            countkm += 1
        }

        // LocationSearch recyclerview
        binding.rvLocation.adapter = locationSearchAdapter
        // recyclerview 아이템 클릭 시 해당 위치로 이동
        locationSearchAdapter.setItemClickListener(object : LocationSearchAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(locationSearchList[position].y, locationSearchList[position].x)
                binding.mapView.setMapCenterPoint(mapPoint, true)
                v.setBackgroundColor(context!!.getResources().getColor(R.color.inform))
                val btn: Button = v.findViewById(R.id.btn_plus)
                btn.visibility = View.VISIBLE
                btn.setOnClickListener {
                    // 고객 추가하기 fragment로 이동
                    val addDirectFragment = AddDirectFragment()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_fl, addDirectFragment)
                        .commitNow()
                }
            }
        })
        // 오른쪽 화살표를 누르면 화면 전환되는 것으로 구현
        // edittext 완료 클릭 시 변경하는 방법도 있긴 한데 내 키보드에서는 완료 버튼이 없음....
        binding.tvLocationSearchGo.setOnClickListener {
            binding.clLocationSearch.visibility = View.VISIBLE
            binding.clLocation.visibility = View.GONE
            // 검색 키워드 받기
            keyword = binding.etLocationSearch.text.toString()
            searchKeyword(keyword)
        }

        binding.tvLocationBtn.setOnClickListener {
            // 고객 추가하기 fragment로 이동
            val addDirectFragment = AddDirectFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_fl, addDirectFragment)
                .commitNow()
        }
    }

    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
        // 그룹 삭제 서버 연동 함수 호출
        // todo: 제대로 되는지 확인 필요
        wholeDelete()
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(requireContext(), "취소", Toast.LENGTH_SHORT).show()
    }

    private fun wholeDelete() {
        deleteGroup(gid)
        dataList.removeAt(pos)
        groupListAdapter.datalist = dataList
        groupListAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        // 다른 화면에 갔다가 다시 돌아왔을 때 버튼 색상 원래대로 되돌리기
        val bgShapekm = binding.ibKm.background as GradientDrawable
        bgShapekm.setColor(resources.getColor(R.color.white))
        val bgShapeplus = binding.ibPlus.background as GradientDrawable
        bgShapeplus.setColor(resources.getColor(R.color.white))
        val bgShapegps = binding.ibGps.background as GradientDrawable
        bgShapegps.setColor(resources.getColor(R.color.white))
    }

    // 그룹 생성 api
    private fun createGroup(name: String){
        viewModel.createGroup(CreateGroupRequest(name))

        viewModel.createGroup.observe(this, Observer {
            Log.d("createGroupObserver", name)
            checkGroup()
        })
    }

    // 그룹 조회 api
    private fun checkGroup(){
        viewModel.checkGroup()
        viewModel.checkGroup.observe(this@MapFragment, Observer {
            groupListAdapter.setData(it)
        })
    }

    // 그룹 삭제 api
    private fun deleteGroup(groupId: Int){
        viewModel.deleteGroup(groupId)
        viewModel.deleteGroup.observe(this, Observer {
            Log.d("deleteGroupObserver", groupId.toString())
        })
    }

    // 그룹 수정 api
    private fun modifyGroup(groupId: Int, name: String){
        viewModel.modifyGroup(groupId, CreateGroupRequest(name))

        viewModel.modifyGroup.observe(this, Observer {
            Log.d("modifyGroupObserver", groupId.toString() + name)
        })
    }

    // 전체 고객 대상 반경 검색 api
    private fun wholeRadius(radius: Double, latitude: Double, longitude: Double){
        viewModel.wholeRadius(RadiusRequest(radius, latitude, longitude))

        viewModel.wholeRadius.observe(this, Observer {
            Log.d("wholeRadiusObserver", "작동?")
        })
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        // API 서버에 요청
        KakaoSearchClient.kakaoSearchService?.getSearchKeyword(BuildConfig.KAKAO_REST_API_KEY, keyword, 1)?.enqueue(object: Callback<ResultSearchKeywordData> {
            override fun onResponse(call: Call<ResultSearchKeywordData>, response: Response<ResultSearchKeywordData>) {
                if (response.isSuccessful){
                    // 직접 지도에 추가하기 위해 기존에 존재한 마커는 없애주기
                    binding.mapView.removePOIItem(marker)
                    markerCheck = false
                    addItemsAndMarkers(response.body())
                    Log.d("LocationSearch", "success")

                }else{  /// 이곳은 에러 발생할 경우 실행됨
                    Log.d("LocationSearch", "fail : ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ResultSearchKeywordData>, t: Throwable) {
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeywordData?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있을 경우
            locationSearchList.clear()           // 리사이클러뷰 초기화
            binding.mapView.removeAllPOIItems()  // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러뷰에 추가
                val item = LocationSearchData(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                locationSearchList.add(item)
                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint =
                        MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            locationSearchAdapter.notifyDataSetChanged()
        } else {
            // 검색 결과 없음
            Toast.makeText(requireContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = requireActivity().getPreferences(MODE_PRIVATE)
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
            startTracking()
        }
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(requireContext(), "위치 권한 승인", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(requireContext(), "위치 권한 거절", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    // 지도에 직접 추가하기 부분 기능들 구현
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        if (markerCheck){
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(p0!!.mapCenterPoint.mapPointGeoCoord.latitude, p0!!.mapCenterPoint.mapPointGeoCoord.longitude)
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        if (markerCheck){
            val mapGeoCoder = MapReverseGeoCoder(KAKAO_API_KEY, marker.mapPoint, reverseGeoCodingResultListener, requireActivity())
            mapGeoCoder.startFindingAddress()
        }
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
}