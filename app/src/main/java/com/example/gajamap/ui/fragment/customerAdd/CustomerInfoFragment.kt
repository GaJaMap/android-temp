package com.example.gajamap.ui.fragment.customerAdd

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.databinding.FragmentCustomerInfoBinding
import com.example.gajamap.ui.view.CustomerInfoActivity
import com.example.gajamap.viewmodel.GetClientViewModel

class CustomerInfoFragment: BaseFragment<FragmentCustomerInfoBinding>(R.layout.fragment_customer_info) {

    var customerInfoActivity: CustomerInfoActivity?=null

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }
    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@CustomerInfoFragment
        binding.viewModel = this.viewModel
    }

    val clientId = GajaMapApplication.prefs.getString("clientId", "")
    val groupId = GajaMapApplication.prefs.getString("groupId", "")
    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
        viewModel.deleteClient(groupId.toInt(), clientId.toInt())
        viewModel.deleteClient.observe(this, Observer {
            Log.d("delete", it.toString())
            customerInfoActivity!!.finish()
        })
        // 액티비티 꺼지게 하는 코드 추가
        Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(requireContext(), "취소", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateAction(){
        binding.topBackBtn.setOnClickListener {
            // 액티비티 꺼지게 하는 코드 추가
            customerInfoActivity!!.finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                customerInfoActivity!!.finish()
            }
        })

        //고객 삭제 dialog
        binding.topDeleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
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


        binding.infoProfileNameTv.text = name
        binding.infoProfileAddressTv1.text = address1
        binding.infoProfileAddressTv2.text = address2
        binding.infoProfilePhoneTv.text = phone


        binding.topModifyBtn.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_fragment, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        customerInfoActivity = context as CustomerInfoActivity
    }

}