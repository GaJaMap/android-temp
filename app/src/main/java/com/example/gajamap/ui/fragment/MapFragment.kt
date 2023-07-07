package com.example.gajamap.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.core.view.isVisible
import com.example.gajamap.R
import com.example.gajamap.api.data.remote.GroupListData
import com.example.gajamap.databinding.DialogAddGroupBottomSheetBinding
import com.example.gajamap.databinding.DialogGroupBinding
import com.example.gajamap.databinding.FragmentAddBinding
import com.example.gajamap.databinding.FragmentMapBinding
import com.example.gajamap.ui.adapter.GroupListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.security.KeyStore.TrustedCertificateEntry
import kotlin.random.Random

class MapFragment : Fragment(), MapView.POIItemEventListener, MapView.MapViewEventListener {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: FragmentMapBinding? = null
    // 매번 null 체크를 할 필요없이 편의성을 위해 바인딩 변수 재선언
    private val binding get() = mBinding!!

    // 그룹 리스트 recyclerview
    lateinit var groupListAdapter: GroupListAdapter
    val dataList = mutableListOf<GroupListData>()
    private val ACCESS_FINE_LOCATION = 1000   // Request Code
    var groupName: String = ""
    var pos: Int = 0
    // 검색창 dropdown list
    var searchList : Array<String> = emptyArray()
    var check = false
    // 지도에서 직접 추가하기를 위한 중심 위치 point
    var centerPoint: MapPoint? = null

    // todo: 추후에 수정 예정 -> 서버 연동 코드 작성 예정
    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(requireContext(), "취소", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바인딩
        mBinding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // GPS 권한 설정
        binding.ibGps.setOnClickListener {
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
                if (check == true){
                    dataList.clear()
                    val groupDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
                    val sheetView = DialogAddGroupBottomSheetBinding.inflate(layoutInflater)

                    groupListAdapter = GroupListAdapter(object : GroupListAdapter.GroupDeleteListener{
                        override fun click(name: String, position: Int) {
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
                        }
                    }, object : GroupListAdapter.GroupEditListener{
                        override fun click2(name: String, position: Int) {
                            // 그룹 수정 dialog
                            val mDialogView = DialogGroupBinding.inflate(layoutInflater)
                            val mBuilder = AlertDialog.Builder(requireContext())
                            val addDialog = mBuilder.create()
                            addDialog.setView(mDialogView.root)
                            addDialog.show()
                            mDialogView.ivClose.setOnClickListener {
                                addDialog.dismiss()
                            }
                        }
                    })
                    dataList.apply {
                        add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), name = "그룹 1", person = 3))
                        add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), name = "그룹 2", person = 9))
                        add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), name = "그룹 3", person = 6))
                    }
                    groupListAdapter.datalist = dataList
                    groupListAdapter.notifyDataSetChanged()

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
                    }
                }
                check = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.ibPlus.setOnClickListener{
            binding.clSearchWhole.visibility = View.INVISIBLE
            // todo: 상단바가 안 보임 추후 수정 예정
            //binding.clSearchLocation.visibility = View.VISIBLE
            binding.clLocation.visibility = View.VISIBLE
            binding.ibPlus.isVisible = false
            binding.ibGps.isVisible = false
            binding.ibKm.isVisible = false
            // 지도에 마커 추가
            val point = MapPOIItem()
            point.apply{
                mapPoint = MapPoint.mapPointWithGeoCoord(37.5562,126.9724)
                markerType = MapPOIItem.MarkerType.RedPin
                isDraggable = true
            }
            binding.mapView.addPOIItem(point)
            onMapViewCenterPointMoved(binding.mapView, centerPoint)
        }
        binding.addBottomBtn.setOnClickListener {
            // todo: AddDirectFragment로 이동
        }

        return root
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
        TODO("Not yet implemented")
    }

    // 지도에 직접 추가하기 부분 기능들 구현
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        Log.d("중심 위치?", p1.toString())
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        TODO("Not yet implemented")
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        TODO("Not yet implemented")
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        TODO("Not yet implemented")
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        TODO("Not yet implemented")
    }


}