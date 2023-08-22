package com.example.gajamap.ui.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.databinding.ActivityCustomerInfoBinding
import com.example.gajamap.viewmodel.GetClientViewModel

class CustomerInfoActivity : BaseActivity<ActivityCustomerInfoBinding>(R.layout.activity_customer_info) {

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }
    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@CustomerInfoActivity
        binding.viewModel = this.viewModel
    }

    // todo: 추후에 수정 예정 -> 서버 연동 코드 작성 예정
    val clientId = GajaMapApplication.prefs.getString("clientId", "")
    val groupId = GajaMapApplication.prefs.getString("groupId", "")
    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
        viewModel.deleteClient(groupId.toInt(), clientId.toInt())
        viewModel.deleteClient.observe(this, Observer {
            Log.d("delete", it.toString())
        })
        finish()
        Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateAction() {
        binding.topBackBtn.setOnClickListener {
            finish()
        }

        //고객 삭제 dialog
        binding.topDeleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("해당 고객을 삭제하시겠습니까?")
                .setMessage("고객을 삭제하시면 영구 삭제되어 복구할 수 없습니다.")
                .setPositiveButton("확인", positiveButtonClick)
                .setNegativeButton("취소", negativeButtonClick)
            val alertDialog = builder.create()
            alertDialog.show()
        }

        val name = GajaMapApplication.prefs.getString("name", "")
        val address1 = GajaMapApplication.prefs.getString("address1", "")
        val address2 = GajaMapApplication.prefs.getString("address2", "")
        val phone = GajaMapApplication.prefs.getString("phone", "")
        val latitude = GajaMapApplication.prefs.getString("latitude1", "")
        val longitude = GajaMapApplication.prefs.getString("longitude1", "")

        //마커 찍는데 사용하기
        /*val latitude = GajaMapApplication.prefs.getString("latitude1", "")
        val longitude = GajaMapApplication.prefs.getString("longitude1", "")
        val mapView = binding.mapView
        val point = MapPOIItem()
        point.apply {
            mapPoint =
                MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble())
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
            mapView.setMapCenterPoint(mapPoint, true)
        }*/


        binding.infoProfileNameTv.text = name
        binding.infoProfileAddressTv1.text = address1
        binding.infoProfileAddressTv2.text = address2
        binding.infoProfilePhoneTv.text = phone


        binding.topModifyBtn.setOnClickListener {
            // todo: EditProfileFragment를 activity로 만들고 다시 수정하기
            // parentFragmentManager.beginTransaction().replace(R.id.nav_fl, EditProfileFragment()).commit()
        }
    }
}