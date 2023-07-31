package com.example.gajamap.ui.fragment.customerList

import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Delete
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.DeleteRequest
import com.example.gajamap.data.model.GetAllClientResponse
import com.example.gajamap.databinding.FragmentEditListBinding
import com.example.gajamap.ui.adapter.CustomerAnyListAdapter
import com.example.gajamap.ui.adapter.CustomerListAdapter
import com.example.gajamap.ui.fragment.customerAdd.CustomerInfoFragment
import com.example.gajamap.ui.fragment.setting.SettingFragment
import com.example.gajamap.viewmodel.ClientViewModel
import com.example.gajamap.viewmodel.GetClientViewModel

class EditListFragment: BaseFragment<FragmentEditListBinding>(R.layout.fragment_edit_list) {

    private var isPurpleBackground: Boolean = false

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@EditListFragment
        binding.fragment = this@EditListFragment
    }

    override fun onCreateAction() {

        //리사이클러뷰
        binding.listRv.addItemDecoration(CustomerListVerticalItemDecoration())
        viewModel.getAllClient()
        viewModel.getAllClient.observe(this, Observer {
            ListRv(it)
        })
    }

    fun ListRv(it : GetAllClientResponse){
        // 리스트를 저장할 변수
        val selectedClientIds = mutableListOf<Int>()

        //고객 리스트
        val customerAnyListAdapter = CustomerAnyListAdapter(it.clients)
        binding.listRv.apply {
            adapter = customerAnyListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.topBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, ListFragment()).commit()
        }

        //전체 선택
        binding.checkEvery.setOnCheckedChangeListener { _, isChecked ->
            // 선택한 모든 clientId들을 selectedClientIds 리스트에 추가 또는 삭제
            selectedClientIds.clear() // 기존 선택한 아이템들 초기화

            if(isChecked){
                selectedClientIds.addAll(it.clients.map { client -> client.clientId })
                // 배경색 변경
                val backgroundDrawable: Drawable? =
                    context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.fragment_list_tool_purple) }
                customerAnyListAdapter.updateItemBackground(backgroundDrawable)
            }
            else{
                // 배경색 변경
                selectedClientIds.removeAll { true }
                val backgroundDrawable: Drawable? =
                    context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.fragment_list_tool) }
                customerAnyListAdapter.updateItemBackground(backgroundDrawable)
            }

        }

        binding.topDeleteBtn.setOnClickListener {
            val deleteRequest = DeleteRequest(selectedClientIds)
            viewModel.deleteAnyClient(10, deleteRequest)
            viewModel.deleteAnyClient.observe(this, Observer {

            })
        }


        //리사이클러뷰 클릭
        customerAnyListAdapter.setOnItemClickListener(object :
            CustomerAnyListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val selectedClientId = it.clients[position].clientId
                // 리스트에 클릭한 아이템의 clientId 추가 또는 삭제
                if (selectedClientIds.contains(selectedClientId)) {
                    selectedClientIds.remove(selectedClientId)
                } else {
                    selectedClientIds.add(selectedClientId)
                }

            }
        })
    }
}