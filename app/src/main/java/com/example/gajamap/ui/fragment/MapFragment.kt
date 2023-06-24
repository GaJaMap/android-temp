package com.example.gajamap.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import com.example.gajamap.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: FragmentMapBinding? = null
    // 매번 null 체크를 할 필요없이 편의성을 위해 바인딩 변수 재선언
    private val binding get() = mBinding!!
    private lateinit var mapView : MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바인딩
        mBinding = FragmentMapBinding.inflate(inflater, container, false)
        context ?: return binding.root
        mapView = MapView(context)
        binding.mapView.addView(mapView)

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