package com.example.gajamap.ui.fragment.customerAdd

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentAddBinding
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.ui.fragment.customerList.CustomerListAdapter
import com.example.gajamap.ui.fragment.customerList.CustomerListVerticalItemDecoration
import com.example.gajamap.viewmodel.AddViewModel
import com.example.gajamap.viewmodel.ListViewModel

class AddFragment: BaseFragment<FragmentAddBinding>(R.layout.fragment_add) {

    override val viewModel by viewModels<AddViewModel> {
        AddViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@AddFragment
        binding.fragment = this@AddFragment
    }

    override fun onCreateAction() {

    }
}