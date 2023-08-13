package com.example.gajamap.ui.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.databinding.ActivityMainBinding
import com.example.gajamap.ui.fragment.customerList.ListFragment
import com.example.gajamap.ui.fragment.map.MapFragment
import com.example.gajamap.ui.fragment.setting.SettingFragment
import com.example.gajamap.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val ACCESS_FINE_LOCATION = 1000   // Request Code
    private var mapFragment: MapFragment? = null
    override val viewModel by viewModels<MainViewModel> {
        MainViewModel.MainViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@MainActivity
        binding.viewModel = this.viewModel
    }
    lateinit var bnMain : BottomNavigationView

    override fun onCreateAction() {
        // 이곳에 화면 기능 구현
        //카카오 해시키
        // Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")

        // bottom navigation
        bnMain = binding.navBn
        // 맨 처음 화면을 켰을 때 map 탭이 보여지도록
        bnMain.selectedItemId = R.id.menu_map
        // 프래그먼트 초기화 및 추가
        mapFragment = MapFragment()
        supportFragmentManager.beginTransaction().add(R.id.nav_fl, mapFragment!!).commit()

        bnMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_map -> {
                    loadFragment(MapFragment())
                    true
                }
                R.id.menu_list -> {
                    loadFragment(ListFragment())
                    true
                }
                R.id.menu_setting -> {
                    loadFragment(SettingFragment())
                    true
                }
                else -> false
            }
        }
        // 탭 버튼 재 호출시 이벤트 없이 처리
        bnMain.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.menu_map -> {}
                R.id.menu_list -> {}
                R.id.menu_setting -> {}
            }
        }
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.nav_fl, fragment).commit()
    }

    // MapFragment에서 위치 권한 요청 후 행동
    @SuppressLint("ResourceAsColor")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한 승인", Toast.LENGTH_SHORT).show()
                mapFragment?.startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한 거절", Toast.LENGTH_SHORT).show()
                mapFragment?.permissionCheck()
                findViewById<ImageButton>(R.id.ib_gps).setImageResource(R.drawable.ic_gray_gps)
            }
        }
    }
}