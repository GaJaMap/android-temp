package com.example.gajamap.ui.fragment.customerAdd

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentAddBinding
import com.example.gajamap.databinding.FragmentCustomerInfoBinding
import com.example.gajamap.viewmodel.AddViewModel

class CustomerInfoFragment: BaseFragment<FragmentCustomerInfoBinding>(R.layout.fragment_customer_info) {
    override val viewModel by viewModels<AddViewModel> {
        AddViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@CustomerInfoFragment
        binding.fragment = this@CustomerInfoFragment
    }

    override fun onCreateAction() {


        binding.topModifyBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, AddDirectFragment()).addToBackStack(null).commit()
        }
    }
}