package com.example.gajamap.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gajamap.R
import com.example.gajamap.data.model.GroupListData
import com.example.gajamap.databinding.DialogAddGroupBottomSheetBinding
import com.example.gajamap.databinding.DialogGroupBinding
import com.example.gajamap.databinding.FragmentMapBinding
import com.example.gajamap.ui.adapter.GroupListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.random.Random

class MapFragment : Fragment() {
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
        // todo: 상단 검색창 만들면 왼쪽 dropdown 누르면 띄우기! 일단 plus 버튼으로 해둠
        binding.ibPlus.setOnClickListener{
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
                    val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_group, null)
                    val mBuilder = AlertDialog.Builder(requireContext())
                        .setView(mDialogView)
                    mBuilder.show()
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
                    .setView(mDialogView.root)
                mBuilder.show()
            }
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
       // binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }
}