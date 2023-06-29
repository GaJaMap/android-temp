package com.example.gajamap.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.contains
import com.example.gajamap.R
import com.example.gajamap.api.data.remote.GroupListData
import com.example.gajamap.databinding.DialogAddGroupBottomSheetBinding
import com.example.gajamap.databinding.FragmentMapBinding
import com.example.gajamap.ui.adapter.GroupListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.daum.mf.map.api.MapView
import kotlin.random.Random

class MapFragment : Fragment() {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: FragmentMapBinding? = null
    // 매번 null 체크를 할 필요없이 편의성을 위해 바인딩 변수 재선언
    private val binding get() = mBinding!!
    private lateinit var mapView : MapView

    // 그룹 리스트 recyclerview
    lateinit var groupListAdapter: GroupListAdapter
    val dataList = mutableListOf<GroupListData>()

    // todo: 추후에 수정 예정
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
        context ?: return binding.root
        mapView = MapView(context)
        binding.mapView.addView(mapView)

        // 그룹 더보기 바텀 다이얼로그 띄우기
        // todo: 상단 검색창 만들면 왼쪽 dropdown 누르면 띄우기! 일단 plus 버튼으로 해둠
        binding.ibPlus.setOnClickListener{
            dataList.clear()
            groupListAdapter = GroupListAdapter()
            val groupDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
            val sheetView = DialogAddGroupBottomSheetBinding.inflate(layoutInflater)

            dataList.apply {
                add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), groupnumber = 1, groupperson = 3))
                add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), groupnumber = 2, groupperson = 9))
                add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), groupnumber = 3, groupperson = 6))
            }
            groupListAdapter.datalist = dataList
            groupListAdapter.notifyDataSetChanged()

            sheetView.rvAddgroup.adapter = groupListAdapter

            groupDialog.setContentView(sheetView.root)
            groupDialog.show()
        }

        // 알림창
        /*
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("정말 삭제하시겠습니까?")
            .setMessage("해당 스케줄을 삭제합니다.")
            .setPositiveButton("확인",positiveButtonClick)
            .setNegativeButton("취소", negativeButtonClick)
        val alertDialog = builder.create()
        alertDialog.show() */
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        if(binding.mapView.contains(mapView)){
            try{
                mapView = MapView(context)
                binding.mapView.addView(mapView)
            }catch (re: RuntimeException){
                Log.e("MapFragment", "onResume: " + re)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.removeView(mapView)
    }
}