package com.example.gajamap.ui.fragment.customerList


import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentListBinding
import com.example.gajamap.viewmodel.ListViewModel

class ListFragment : BaseFragment<FragmentListBinding> (R.layout.fragment_list) {
    // 검색창 dropdown list
    var searchList : Array<String> = emptyArray()

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
        // todo: 나중에 서버 연동 후 값 받아와서 넣어주는 것으로 수정 예정
        searchList = searchList.plus("전체")
        searchList = searchList.plus("서울특별시 고객들")
        val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, searchList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSearch.adapter = adapter
        binding.spinnerSearch.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                Toast.makeText(requireContext(), "클릭클릭클릭", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
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