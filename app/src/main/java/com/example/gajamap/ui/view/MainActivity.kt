package com.example.gajamap.ui.view

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.databinding.ActivityMainBinding
import com.example.gajamap.ui.fragment.customerList.ListFragment
import com.example.gajamap.ui.fragment.MapFragment
import com.example.gajamap.ui.fragment.SettingFragment
import com.example.gajamap.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
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

        // bottomnavigation
        bnMain = binding.navBn
        // 맨 처음 화면을 켰을 때 map 탭이 보여지도록
        bnMain.selectedItemId = R.id.menu_map
        supportFragmentManager.beginTransaction().add(R.id.nav_fl, MapFragment()).commit()

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
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.nav_fl, fragment).commit()
    }

}