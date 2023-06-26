package com.example.gajamap.ui.fragment.customerList


import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.viewmodel.ListViewModel

class ListFragment : BaseFragment<FragmentListBinding> (R.layout.fragment_list) {

    //더미데이터
    private var customerList: ArrayList<Customer> = arrayListOf(
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km"),
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km"),
        Customer(R.drawable.item_list_img, "한고객", "서울특별시 강남구", "010-2166-1769","5.0km")
    )

    override val viewModel by viewModels<ListViewModel> {
        ListViewModel.ListViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@ListFragment
        binding.fragment = this@ListFragment
    }

    override fun onCreateAction() {

        //리사이클러뷰
        val customerListAdapter = CustomerListAdapter(customerList)
        binding.listRv.apply {
            adapter = customerListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CustomerListVerticalItemDecoration())
        }

        //다이얼로그
        /*binding.fragmentListCategory3.setOnClickListener {
            val dialog = CustomerListDialog()
            // 알림창이 띄워져있는 동안 배경 클릭 막기
            dialog.isCancelable = false
            dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")
        }*/

    }

}