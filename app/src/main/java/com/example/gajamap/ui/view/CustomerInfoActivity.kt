package com.example.gajamap.ui.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.databinding.ActivityCustomerInfoBinding
import com.example.gajamap.ui.fragment.customerAdd.CustomerInfoFragment
import com.example.gajamap.ui.fragment.customerAdd.EditProfileFragment
import com.example.gajamap.viewmodel.GetClientViewModel

class CustomerInfoActivity : BaseActivity<ActivityCustomerInfoBinding>(R.layout.activity_customer_info) {

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }
    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@CustomerInfoActivity
        binding.viewModel = this.viewModel
    }

    override fun onCreateAction() {
        supportFragmentManager.beginTransaction().replace(R.id.frame_fragment, CustomerInfoFragment()).commit()
    }

}