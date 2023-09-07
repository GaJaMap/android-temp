package com.pg.gajamap.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.location.LocationManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.pg.gajamap.BR
import com.pg.gajamap.BuildConfig
import com.pg.gajamap.BuildConfig.KAKAO_API_KEY
import com.pg.gajamap.R
import com.pg.gajamap.api.retrofit.KakaoSearchClient
import com.pg.gajamap.base.BaseFragment
import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.base.UserData
import com.pg.gajamap.data.model.GetAllClientResponse
import com.pg.gajamap.data.model.ViewPagerData
import com.pg.gajamap.data.response.*
import com.pg.gajamap.databinding.DialogAddGroupBottomSheetBinding
import com.pg.gajamap.databinding.DialogGroupBinding
import com.pg.gajamap.databinding.FragmentMapBinding
import com.pg.gajamap.ui.adapter.GroupListAdapter
import com.pg.gajamap.ui.adapter.LocationSearchAdapter
import com.pg.gajamap.ui.adapter.SearchResultAdapter
import com.pg.gajamap.ui.adapter.ViewPagerAdapter
import com.pg.gajamap.ui.view.AddDirectActivity
import com.pg.gajamap.viewmodel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapReverseGeoCoder.ReverseGeoCodingResultListener
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Integer.min

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), MapView.POIItemEventListener, MapView.MapViewEventListener {
    // 그룹 리스트 recyclerview
    lateinit var groupListAdapter: GroupListAdapter
    private val ACCESS_FINE_LOCATION = 1000   // Request Code
    var gName: String = ""
    var pos: Int = 0
    var posDelete: Int = 0
    var markerCheck = false
    // 지도에서 직접 추가하기를 위한 중심 위치 point
    private lateinit var marker: MapPOIItem
    private lateinit var reverseGeoCodingResultListener : ReverseGeoCodingResultListener
    // LocationSearch recyclerview
    private val locationSearchList = arrayListOf<LocationSearchData>()
    private val locationSearchAdapter = LocationSearchAdapter(locationSearchList)
    // SearchResult recyclerview
    private val searchResultList = arrayListOf<SearchResultData>()
    val searchResultAdapter = SearchResultAdapter(searchResultList)
    // viewpager 설정
    private val viewpagerList = arrayListOf<ViewPagerData>()
    val viewpagerAdapter = ViewPagerAdapter(viewpagerList)
    var sheetView : DialogAddGroupBottomSheetBinding? = null
    private var keyword = "" // 검색 키워드
    var gid: Long = 0
    var itemId: Long = 0
    var groupNum = 0
    // 반경 3km, 5km 버튼 클릭되었는지 check
    var threeCheck = false
    var fiveCheck = false
    var plusBtn = false
    var bottomGPSBtn = false
    var kmBtn = false
    var GPSBtn = false
    var latitude = 0.0
    var longitude = 0.0

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
        val groupDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        sheetView = DialogAddGroupBottomSheetBinding.inflate(layoutInflater)

        // 검색결과 recyclerview 크기 아이템 개수에 따라 조절
        val maxRecyclerViewHeight = resources.getDimensionPixelSize(R.dimen.max_recycler_view_height)
        val itemHeight = resources.getDimensionPixelSize(R.dimen.item_height)

        // 자동 로그인 response 데이터 값 받아오기
        val clientList = UserData.clientListResponse
        val groupInfo = UserData.groupinfo

        clientMarker()

        // 그룹 더보기 및 검색창 그룹 이름, 현재 선택된 이름으로 변경
        if (groupInfo != null) {
            binding.tvSearch.text = groupInfo.groupName
            sheetView!!.tvAddgroupMain.text = groupInfo.groupName
        }

        binding.mapView.setMapViewEventListener(this)
        binding.mapView.setPOIItemEventListener(this)

        binding.vpClient.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로

        // 추가한 그룹이 존재하는지 확인한 뒤에 그룹을 추가하라는 다이얼로그를 띄울지 말지 결정해야 하기에 일단 여기에서 호출
        checkGroup()
        // GPS 권한 설정
        binding.ibGps.setOnClickListener {
            if(!GPSBtn){
                if (checkLocationService()) {
                    GPSBtn = true

                    // GPS가 켜져있을 경우
                    permissionCheck()
                    // gps 버튼 클릭 상태로 변경
                    // 원을 유지한 상태로 drawable 색상만 변경할 때 사용
                    val bgShape = binding.ibGps.background as GradientDrawable
                    bgShape.setColor(resources.getColor(R.color.main))
                    binding.ibGps.setImageResource(R.drawable.ic_white_gps)
                } else {
                    // GPS가 꺼져있을 경우 클릭한 상태가 아님
                    Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                GPSBtn = false
                val bgShape = binding.ibGps.background as GradientDrawable
                bgShape.setColor(resources.getColor(R.color.white))
                binding.ibGps.setImageResource(R.drawable.ic_gray_gps)
                stopTracking()
            }
        }

        // 지도에 직접 위치 추가하기 클릭시 보이는 GPS 버튼에 대한 위치 권한 설정
        binding.ibBottomGps.setOnClickListener {
            // gps 버튼 클릭 상태로 변경
            // 원을 유지한 상태로 drawable 색상만 변경할 때 사용
            if(!bottomGPSBtn){
                if (checkLocationService()) {
                    bottomGPSBtn = true
                    binding.tvLocationAddress.text = "내 위치 검색중..."
                    // GPS가 켜져있을 경우
                    val a = permissionCheck()

                    val bgShape = binding.ibBottomGps.background as GradientDrawable
                    bgShape.setColor(resources.getColor(R.color.main))
                    binding.ibBottomGps.setImageResource(R.drawable.ic_white_gps)

                    binding.mapView.removePOIItem(marker)
                    // 지도에서 직접 추가하기 마커 위치
                    marker = MapPOIItem()
                    marker.itemName = "Marker"
                    marker.mapPoint = MapPoint.mapPointWithGeoCoord(a.first, a.second)
                    marker.markerType = MapPOIItem.MarkerType.RedPin
                    binding.mapView.addPOIItem(marker)
                    val mapGeoCoder = MapReverseGeoCoder(KAKAO_API_KEY, marker.mapPoint, reverseGeoCodingResultListener, requireActivity())
                    mapGeoCoder.startFindingAddress()
                    markerCheck = true
                } else {
                    // GPS가 꺼져있을 경우
                    Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
            // 두 번 클릭 시 원상태로 돌아오게 하기 및 위치 추적 중지
            else {
                bottomGPSBtn = false
                val bgShape = binding.ibBottomGps.background as GradientDrawable
                bgShape.setColor(resources.getColor(R.color.white))
                binding.ibBottomGps.setImageResource(R.drawable.ic_gray_gps)
                stopTracking()
            }
        }

        // groupListAdapter를 우선적으로 초기화해줘야 함
        groupListAdapter = GroupListAdapter(object : GroupListAdapter.GroupDeleteListener{
            override fun click(id: Long, name: String, position: Int) {
                // 그룹 삭제 dialog
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("해당 그룹을 삭제하시겠습니까?")
                    .setMessage("그룹을 삭제하시면 영구 삭제되어 복구할 수 없습니다.")
                    .setPositiveButton("확인", { dialogInterface: DialogInterface, i: Int ->
                        // 그룹 삭제 서버 연동 함수 호출
                        deleteGroup(gid, position)
                    })
                    .setNegativeButton("취소", { dialogInterface: DialogInterface, i: Int ->
                        Toast.makeText(requireContext(), "취소", Toast.LENGTH_SHORT).show()
                    })
                val alertDialog = builder.create()
                alertDialog.show()
                gName = name
                posDelete = position
                gid = id
            }
        }, object : GroupListAdapter.GroupEditListener{
            override fun click2(id: Long, name: String, position: Int) {
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
                    // 그룹 수정 api 연동
                    modifyGroup(gid, mDialogView.etName.text.toString(), position)
                    addDialog.dismiss()
                }
            }
        })

        // 그룹 recyclerview 아이템 클릭 시 값 변경 및 배경색 바꾸기
        groupListAdapter.setItemClickListener(object : GroupListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, gid: Long, gname: String) {
                itemId = gid
                binding.tvSearch.text = gname
                sheetView!!.tvAddgroupMain.text = gname
                pos = position

                if (position == 0){
                    getAllClient()
                }else{
                    getGroupClient(gid)
                }
            }
        })

        // search bar 클릭 시 바텀 다이얼로그 띄우기
        binding.clSearch.setOnClickListener {
            // 그룹 더보기 바텀 다이얼로그 띄우기
            checkGroup()
            sheetView!!.rvAddgroup.adapter = groupListAdapter

            groupDialog.setContentView(sheetView!!.root)
            groupDialog.show()

            sheetView!!.btnAddgroup.setOnClickListener {
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
                    addDialog.dismiss()
                }
            }
        }

        // 위도, 경도 값으로 주소 받기
        reverseGeoCodingResultListener = object : ReverseGeoCodingResultListener {
            override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder, addressString: String) {
                // 주소를 찾은 경우
                GajaMapApplication.prefs.setString("address", addressString)
                binding.tvLocationAddress.text = addressString
            }

            override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                // 호출에 실패한 경우
                Log.e("ReverseGeocoding", "주소를 찾을 수 없습니다.")
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                searchResultList.clear()
                val size = UserData.clientListResponse?.clients!!.size
                for (i in 0..size-1){
                    val name = UserData.clientListResponse?.clients!!.get(i).clientName
                    if(name.contains(binding.etSearch.text.toString())){
                        searchResultList.add(SearchResultData(name, i))
                    }
                }

                binding.rvSearch.adapter = searchResultAdapter
                val itemCount = searchResultList.size
                // 최대 크기와 비교하여 결정
                val calculatedRecyclerViewHeight = min(itemHeight * itemCount, maxRecyclerViewHeight)
                // RecyclerView의 높이를 동적으로 설정
                binding.rvSearch.layoutParams.height = calculatedRecyclerViewHeight
                searchResultAdapter.notifyDataSetChanged()

                binding.clSearchResult.visibility = View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        // 검색 결과 recyclerview 아이템 클릭 시 해당 고객에 대한 마커 위치로 이동
        searchResultAdapter.setItemClickListener(object : SearchResultAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, index: Int) {
                val itemData = UserData.clientListResponse?.clients?.get(index)
                val mapPoint = MapPoint.mapPointWithGeoCoord(itemData!!.location.latitude, itemData.location.longitude)
                binding.mapView.setMapCenterPoint(mapPoint, true)
            }
        })

        // plus버튼, 지도에 직접 추가하기 dialog 보여짐
        binding.ibPlus.setOnClickListener{
            if (groupNum == 1){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("현재 생성된 그룹이 없습니다.")
                    .setMessage("그룹을 등록해주시기 바랍니다.")
                    .setPositiveButton("확인"){ dialog, which ->
                    }
                val alertDialog = builder.create()
                alertDialog.show()
            }
            else{
                if (!plusBtn){
                    // plus 버튼 클릭 상태로 변경
                    plusBtn = true
                    val bgShape = binding.ibPlus.background as GradientDrawable
                    bgShape.setColor(resources.getColor(R.color.main))
                    binding.ibPlus.setImageResource(R.drawable.ic_white_plus)

                    binding.mapView.removeAllPOIItems()
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
                    marker.mapPoint = MapPoint.mapPointWithGeoCoord(binding.mapView.mapCenterPoint.mapPointGeoCoord.latitude, binding.mapView.mapCenterPoint.mapPointGeoCoord.longitude)
                    latitude = binding.mapView.mapCenterPoint.mapPointGeoCoord.latitude
                    longitude = binding.mapView.mapCenterPoint.mapPointGeoCoord.longitude
                    marker.markerType = MapPOIItem.MarkerType.RedPin
                    binding.mapView.addPOIItem(marker)
                    val mapGeoCoder = MapReverseGeoCoder(KAKAO_API_KEY, marker.mapPoint, reverseGeoCodingResultListener, requireActivity())
                    mapGeoCoder.startFindingAddress()
                    markerCheck = true
                }
                else{
                    // plus 버튼 클릭하지 않은 상태로 변경
                    plusBtn = false
                    val bgShape = binding.ibPlus.background as GradientDrawable
                    bgShape.setColor(resources.getColor(R.color.white))
                    binding.ibPlus.setImageResource(R.drawable.ic_plus)
                }
            }
        }

        // km 메인 버튼 클릭 이벤트, 3km와 5km 버튼 띄우기
        binding.ibKm.setOnClickListener {
            if(!kmBtn){
                // km 버튼 클릭 상태로 변경
                // GPS가 켜져있을 경우
                if (checkLocationService()) {
                    val a = permissionCheck()
                    kmBtn = true
                    val bgShape = binding.ibKm.background as GradientDrawable
                    bgShape.setColor(resources.getColor(R.color.main))
                    binding.ibKm.setImageResource(R.drawable.ic_white_km)
                    binding.clKm.visibility = View.VISIBLE

                    // 자신의 현재 위치를 기준으로 반경 3km, 5km에 위치한 전체 고객 정보 가져오기
                    binding.btn3km.setOnClickListener {
                        if(!threeCheck){
                            if(fiveCheck){
                                fiveCheck = false
                                binding.btn5km.setBackgroundResource(R.drawable.bg_km_notclick)
                                binding.btn5km.setTextColor(resources.getColor(R.color.main))
                            }
                            threeCheck = true
                            binding.btn3km.setBackgroundResource(R.drawable.bg_km_click)
                            binding.btn3km.setTextColor(resources.getColor(R.color.white))

                            if (a.first != 0.0 && a.second != 0.0){
                                if (binding.tvSearch.text == "전체"){
                                    wholeRadius(3000, a.first, a.second)
                                }
                                else{
                                    specificRadius(3000, a.first, a.second, itemId)
                                }
                            }
                        }
                        // 3km 버튼이 이미 눌려있을 경우
                        else{
                            threeCheck = false
                            binding.mapView.removeAllPOIItems()  // 지도의 마커 모두 제거
                            binding.btn3km.setBackgroundResource(R.drawable.bg_km_notclick)
                            binding.btn3km.setTextColor(resources.getColor(R.color.main))
                        }
                    }

                    binding.btn5km.setOnClickListener {
                        if(!fiveCheck) {
                            if (threeCheck) {
                                threeCheck = false
                                binding.btn3km.setBackgroundResource(R.drawable.bg_km_notclick)
                                binding.btn3km.setTextColor(resources.getColor(R.color.main))
                            }
                            fiveCheck = true
                            binding.btn5km.setBackgroundResource(R.drawable.bg_km_click)
                            binding.btn5km.setTextColor(resources.getColor(R.color.white))

                            if (a.first != 0.0 && a.second != 0.0) {
                                if (binding.tvSearch.text == "전체") {
                                    wholeRadius(5000, a.first, a.second)
                                } else {
                                    specificRadius(5000, a.first, a.second, itemId)
                                }
                            }
                        }
                        // 5km 버튼이 이미 눌려있을 경우
                        else{
                            fiveCheck = false
                            binding.mapView.removeAllPOIItems()  // 지도의 마커 모두 제거
                            binding.btn5km.setBackgroundResource(R.drawable.bg_km_notclick)
                            binding.btn5km.setTextColor(resources.getColor(R.color.main))
                        }
                    }
                }else {
                    // GPS가 꺼져있을 경우
                    Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
            else{ // 두 번 클릭 시 원상태로 돌아오게 하기
                kmBtn = false
                val bgShape = binding.ibKm.background as GradientDrawable
                bgShape.setColor(resources.getColor(R.color.white))
                binding.ibKm.setImageResource(R.drawable.ic_km)
                binding.clKm.visibility = View.GONE
            }
        }

        // LocationSearch recyclerview
        binding.rvLocation.adapter = locationSearchAdapter
        // recyclerview 아이템 클릭 시 해당 위치로 이동
        locationSearchAdapter.setItemClickListener(object : LocationSearchAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(locationSearchList[position].y, locationSearchList[position].x)
                latitude = locationSearchList[position].y
                longitude = locationSearchList[position].x

                binding.mapView.setMapCenterPoint(mapPoint, true)
                val btn: Button = v.findViewById(R.id.btn_plus)
                btn.setOnClickListener {
                    // 고객 추가하기 activity로 이동
                    val intent = Intent(activity, AddDirectActivity::class.java)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivity(intent)
                }
            }
        })

        // 오른쪽 화살표를 누르면 화면 전환되는 것으로 구현
        binding.tvLocationSearchGo.setOnClickListener {
            binding.clLocationSearch.visibility = View.VISIBLE
            binding.clLocation.visibility = View.GONE
            // 검색 키워드 받기
            keyword = binding.etLocationSearch.text.toString()
            searchKeyword(keyword)
        }

        // edittext 완료 클릭 시 화면 전환되는 것으로 추가 구현
        binding.etLocationSearch.setOnKeyListener { view, i, keyEvent ->
            // Enter Key Action
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                // 키패드 내리기
                val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etLocationSearch.windowToken, 0)
                binding.clLocationSearch.visibility = View.VISIBLE
                binding.clLocation.visibility = View.GONE
                // 검색 키워드 받기
                keyword = binding.etLocationSearch.text.toString()
                searchKeyword(keyword)
                true
            }
            false
        }

        binding.tvLocationBtn.setOnClickListener {
            // 고객 추가하기 activity로 이동
            val intent = Intent(getActivity(), AddDirectActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
        }
    }

    // 그룹 생성 api
    private fun createGroup(name: String){
        viewModel.createGroup(CreateGroupRequest(name))
        viewModel.checkGroup.observe(this, Observer {
            groupListAdapter.setData(it)
        })
    }

    // 그룹 조회 api
    private fun checkGroup(){
        viewModel.checkGroup()
        viewModel.checkGroup.observe(this@MapFragment, Observer {
            groupListAdapter.setData(it)
            groupNum = viewModel.checkGroup.value!!.size
        })
    }

    // 그룹 삭제 api
    private fun deleteGroup(groupId: Long, position: Int){
        viewModel.deleteGroup(groupId, position)
        viewModel.checkGroup.observe(this, Observer {
            groupListAdapter.setData(it)
            // 현재 선택한 리사이클러뷰 아이템의 그룹을 삭제했을 경우
            // 전체 고객을 조회하는 api 호출 후 전체 고객 마커 찍고 UserData 값 변경
            if(posDelete == position){
                getAllClient()
                binding.tvSearch.text = "전체"
                sheetView!!.tvAddgroupMain.text = "전체"
            }
        })
    }

    // 그룹 수정 api
    private fun modifyGroup(groupId: Long, name: String, position: Int){
        viewModel.modifyGroup(groupId, CreateGroupRequest(name), position)
        viewModel.checkGroup.observe(this, Observer {
            groupListAdapter.setData(it)

            // 변경한 그룹 이름 저장 데이터에도 갱신
            UserData.groupinfo!!.groupName = name

            // 현재 선택한 리사이클러뷰 아이템의 그룹 이름을 변경했을 경우
            if(pos == position){
                binding.tvSearch.text = name
                sheetView!!.tvAddgroupMain.text = name
            }
       })
    }

    // 전체 고객 대상 반경 검색 api
    private fun wholeRadius(radius: Int, latitude: Double, longitude: Double){
        viewModel.wholeRadius(radius, latitude, longitude)

        viewModel.wholeRadius.observe(this, Observer {
            if (viewModel.wholeRadius.value == null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("현재 생성된 그룹이 없거나 등록된 고객이 없습니다.\n그룹 및 고객을 등록해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                builder.show()
            }
            else{
                val data = viewModel.wholeRadius.value!!.clients
                val num = data.count()
                binding.mapView.removeAllPOIItems()
                for (i in 0..num-1){
                    val itemdata = data.get(i)
                    // 지도에 마커 추가
                    val point = MapPOIItem()
                    point.apply {
                        itemName = itemdata.clientName
                        tag = itemdata.clientId.toInt()
                        mapPoint =
                            MapPoint.mapPointWithGeoCoord(itemdata.location!!.latitude, itemdata.location.longitude)
                        markerType = MapPOIItem.MarkerType.BluePin
                        selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }
                    binding.mapView.addPOIItem(point)
                }
            }
        })
    }

    // 특정 그룹 내에 고객 대상 반경 검색 api
    private fun specificRadius(radius: Int, latitude: Double, longitude: Double, groupId: Long){
        viewModel.specificRadius(radius, latitude, longitude, groupId)

        viewModel.wholeRadius.observe(this, Observer {
            if (viewModel.wholeRadius.value == null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("현재 생성된 그룹이 없거나 등록된 고객이 없습니다.\n그룹 및 고객을 등록해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                builder.show()
            }
            else{
                val data = viewModel.wholeRadius.value!!.clients
                val num = data.count()
                binding.mapView.removeAllPOIItems()
                for (i in 0..num-1){
                    val itemdata = data.get(i)
                    // 지도에 마커 추가
                    val point = MapPOIItem()
                    point.apply {
                        itemName = itemdata.clientName
                        tag = itemdata.clientId.toInt()
                        mapPoint =
                            MapPoint.mapPointWithGeoCoord(itemdata.location!!.latitude, itemdata.location!!.longitude)
                        markerType = MapPOIItem.MarkerType.BluePin
                        selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }
                    binding.mapView.addPOIItem(point)
                }
            }
        })
    }

    // 특정 그룹 내에 고객 전부 조회 api
    private fun getGroupClient(groupId: Long){
        viewModel.getGroupAllClient(groupId)
        viewModel.groupClients.observe(this, Observer {
            val data = viewModel.groupClients.value!!.clients
            val num = data.count()

            // UserData 값 갱신
            UserData.clientListResponse = viewModel.groupClients.value
            binding.mapView.removeAllPOIItems()
            for (i in 0..num-1) {
                val itemdata = data.get(i)
                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = itemdata.clientName
                    tag = itemdata.clientId.toInt()
                    mapPoint = MapPoint.mapPointWithGeoCoord(itemdata.location.latitude, itemdata.location.longitude)
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
        })
    }

    // 전체 고객 전부 조회 api
    private fun getAllClient(){
        viewModel.getAllClient()
        viewModel.allClients.observe(this, Observer {

            val data = viewModel.allClients.value!!.clients
            val num = data.count()

            // UserData 값 갱신
            UserData.clientListResponse = viewModel.allClients.value
            binding.mapView.removeAllPOIItems()
            for (i in 0..num-1) {
                val itemdata = data.get(i)
                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = itemdata.clientName
                    tag = itemdata.clientId.toInt()
                    mapPoint = MapPoint.mapPointWithGeoCoord(itemdata.location.latitude, itemdata.location.longitude)
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
        })
    }

    // 전체 고객 검색 -> 조회할 고객 이름 검색 api
    private fun getAllClientName(name : String){
        viewModel.getAllClientName(name)
        viewModel.allClientsName.observe(this, Observer {
            getClientList(viewModel.allClientsName.value!!)
        })
    }

    // 특정 그룹 내 고객 검색 -> 조회할 고객 이름 검색 api
    private fun getGroupAllClientName(name : String, groupId: Long){
        viewModel.getGroupAllClientName(name, groupId)
        viewModel.groupClientsName.observe(this, Observer {
            getClientList(viewModel.groupClientsName.value!!)
        })
    }

    override fun onResume() {
        super.onResume()
        if(plusBtn){
            plusBtnInactivation()
            clientMarker()
        }
    }

    // ViewPager에 들어갈 아이템
    private fun getClientList(data : GetAllClientResponse) {
        viewpagerList.clear()
        val size = data.clients.size

        for (i in 0..size-1){
            val itemdata = data.clients.get(i)
            if(itemdata.image.filePath != null){
                if(itemdata.distance == null){
                    viewpagerList.add(ViewPagerData(UserData.imageUrlPrefix + itemdata.image.filePath, itemdata.clientName, itemdata.address.mainAddress, itemdata.phoneNumber, null))

                }else{
                    viewpagerList.add(ViewPagerData(UserData.imageUrlPrefix + itemdata.image.filePath, itemdata.clientName, itemdata.address.mainAddress, itemdata.phoneNumber, itemdata.distance))
                }
            }
            else{
                if(itemdata.distance == null){
                    viewpagerList.add(ViewPagerData("null", itemdata.clientName, itemdata.address.mainAddress, itemdata.phoneNumber, null))

                }else{
                    viewpagerList.add(ViewPagerData("null", itemdata.clientName, itemdata.address.mainAddress, itemdata.phoneNumber, itemdata.distance))
                }
            }
        }

        binding.vpClient.adapter = viewpagerAdapter
        searchResultAdapter.notifyDataSetChanged()
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
            Log.d("locations", searchResult!!.documents.size.toString())
            for (document in searchResult.documents) {
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
    fun permissionCheck(): Pair<Double, Double> {
        val preference = requireActivity().getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        var userLatitude = 0.0
        var userLongitude = 0.0
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
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
            // 사용자 위치에 대한 위도, 경도 값 저장
            val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            //위도 , 경도
            userLatitude = userNowLocation!!.latitude
            userLongitude = userNowLocation.longitude
            GajaMapApplication.prefs.setString("UserLatitude", userLatitude.toString())
            GajaMapApplication.prefs.setString("UserLongitude", userLongitude.toString())
        }
        return Pair(userLatitude, userLongitude)
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun clientMarker(){
        // MapFragment 띄우자마자 현재 선택된 고객들의 위치 마커 찍기
        val clientNum = UserData.clientListResponse!!.clients.size
        for (i in 0..clientNum-1){
            val itemdata = UserData.clientListResponse!!.clients.get(i)
            // 지도에 마커 추가
            val point = MapPOIItem()
            point.apply {
                itemName = itemdata.clientName
                tag = itemdata.clientId.toInt()
                mapPoint =
                    MapPoint.mapPointWithGeoCoord(itemdata.location.latitude, itemdata.location.longitude)
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            binding.mapView.addPOIItem(point)
        }
    }

    private fun plusBtnInactivation(){
        plusBtn = false
        val bgShape = binding.ibPlus.background as GradientDrawable
        bgShape.setColor(resources.getColor(R.color.white))
        binding.ibPlus.setImageResource(R.drawable.ic_plus)
        binding.clSearchWhole.visibility = View.VISIBLE
        binding.clSearchLocation.visibility = View.GONE
        binding.clLocation.visibility = View.GONE
        binding.ibPlus.visibility = View.VISIBLE
        binding.ibGps.visibility = View.VISIBLE
        binding.ibKm.visibility = View.VISIBLE
        markerCheck = false
        binding.mapView.removePOIItem(marker)
    }

    // 위치추적 시작
    fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    // 지도에 직접 추가하기 부분 기능들 구현
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        if (markerCheck){
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(p0!!.mapCenterPoint.mapPointGeoCoord.latitude, p0.mapCenterPoint.mapPointGeoCoord.longitude)
            latitude = p0.mapCenterPoint.mapPointGeoCoord.latitude
            longitude = p0.mapCenterPoint.mapPointGeoCoord.longitude
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    // MapView를 클릭하면 호출되는 콜백 메서드
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        binding.clCardview.visibility = View.GONE
        binding.ibPlus.visibility = View.VISIBLE
        binding.ibGps.visibility = View.VISIBLE
        binding.ibKm.visibility = View.VISIBLE
        // 검색창 없애기
        binding.clSearchResult.visibility = View.GONE

        if(plusBtn){
            plusBtnInactivation()
        }
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
    // 마커 클릭 시 호출되는 콜백 메서드
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        binding.clCardview.visibility = View.VISIBLE
        binding.ibPlus.visibility = View.GONE
        binding.ibGps.visibility = View.GONE
        binding.ibKm.visibility = View.GONE
        binding.clKm.visibility = View.GONE
        if(pos == 0){
            getAllClientName(p1!!.itemName)
        }
        else{
            getGroupAllClientName(p1!!.itemName, itemId)
        }
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