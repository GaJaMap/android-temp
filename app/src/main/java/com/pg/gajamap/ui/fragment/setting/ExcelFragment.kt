package com.pg.gajamap.ui.fragment.setting

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.pg.gajamap.BR
import com.pg.gajamap.R
import com.pg.gajamap.base.BaseFragment
import com.pg.gajamap.databinding.FragmentExcelBinding
import com.pg.gajamap.viewmodel.ClientViewModel

class ExcelFragment : BaseFragment<FragmentExcelBinding>(R.layout.fragment_excel) {

    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@ExcelFragment
        binding.fragment = this@ExcelFragment
    }

    override fun onCreateAction() {
        binding.topBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, SettingFragment()).addToBackStack(null).commit()
        }
    }

}