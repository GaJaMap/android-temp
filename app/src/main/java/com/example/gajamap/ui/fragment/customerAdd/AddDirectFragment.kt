package com.example.gajamap.ui.fragment.customerAdd

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentAddDirectBinding
import com.example.gajamap.viewmodel.AddViewModel

class AddDirectFragment: BaseFragment<FragmentAddDirectBinding>(R.layout.fragment_add_direct) {

    override val viewModel by viewModels<AddViewModel> {
        AddViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@AddDirectFragment
        binding.fragment = this@AddDirectFragment
    }

    override fun onCreateAction() {

    }
}