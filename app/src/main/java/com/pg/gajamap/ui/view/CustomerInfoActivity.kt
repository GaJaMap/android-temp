package com.pg.gajamap.ui.view

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.pg.gajamap.R
import com.pg.gajamap.base.BaseActivity
import com.pg.gajamap.databinding.ActivityCustomerInfoBinding
import com.pg.gajamap.ui.fragment.customerAdd.CustomerInfoFragment
import com.pg.gajamap.viewmodel.GetClientViewModel

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