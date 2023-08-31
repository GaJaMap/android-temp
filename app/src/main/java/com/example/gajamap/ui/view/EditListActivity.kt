package com.example.gajamap.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.base.UserData
import com.example.gajamap.data.model.DeleteRequest
import com.example.gajamap.data.model.GetAllClientResponse
import com.example.gajamap.databinding.ActivityEditListBinding
import com.example.gajamap.ui.adapter.CustomerAnyListAdapter
import com.example.gajamap.ui.fragment.customerList.CustomerListVerticalItemDecoration
import com.example.gajamap.viewmodel.GetClientViewModel

class EditListActivity : BaseActivity<ActivityEditListBinding>(R.layout.activity_edit_list) {
    var selectedClientIds = mutableListOf<Int>()
    var groupId = 0
    var client = UserData.clientListResponse
    var clientList = UserData.clientListResponse?.clients
    var groupInfo = UserData.groupinfo

    override val viewModel by viewModels<GetClientViewModel> {
        GetClientViewModel.AddViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@EditListActivity
        binding.viewModel = this.viewModel
    }
    override fun onCreateAction() {
        // 자동 로그인 response 데이터 값 받아오기
        //val clientList = UserData.clientListResponse
        //val groupInfo = UserData.groupinfo

        if (groupInfo != null) {
            groupId = groupInfo!!.groupId
        }

        //리사이클러뷰
        binding.listRv.addItemDecoration(CustomerListVerticalItemDecoration())
        //viewModel.getAllClient()
        /*viewModel.getAllClient.observe(this, Observer {
            ListRv(it)
        })*/
        client?.let { ListRv(it) }

        binding.topBackBtn.setOnClickListener {
            // 리스트 fragment로 이동
            finish()
        }

        binding.topDeleteBtn.setOnClickListener {
            val deleteRequest = DeleteRequest(selectedClientIds)
            viewModel.deleteAnyClient(groupId, deleteRequest)
            viewModel.deleteAnyClient.observe(this, Observer {

                // 선택된 클라이언트들 삭제 후, 클라이언트 목록 업데이트
                val newClientList = clientList?.filter { client ->
                    client.clientId !in selectedClientIds
                }
                Log.d("newdelete", clientList.toString())
               // Log.d("new", newClientList.toString())

                if (newClientList != null) {
                    val newResponse = client?.let { it1 -> GetAllClientResponse(newClientList, it1.imageUrlPrefix) }
                    if (newResponse != null) {
                        ListRv(newResponse)
                    }
                }

                //clientList = newClientList
                //Log.d("newdelete", clientList.toString())

                // 선택된 클라이언트 아이디 목록 초기화
                selectedClientIds.clear()

                binding.topTvNumber1.text = selectedClientIds.size.toString()

                finish()
            })
        }
    }

    private fun ListRv(it : GetAllClientResponse){
        // 리스트를 저장할 변수
        GajaMapApplication.prefs.setString("imageUrlPrefix", it.imageUrlPrefix.toString())

        //고객 리스트
        binding.topTvNumber2.text = it.clients.size.toString()
        val customerAnyListAdapter = CustomerAnyListAdapter(it.clients)
        binding.listRv.apply {
            adapter = customerAnyListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        //전체 선택
        binding.checkEvery.setOnCheckedChangeListener { _, isChecked ->
            // 선택한 모든 clientId들을 selectedClientIds 리스트에 추가 또는 삭제
            selectedClientIds.clear() // 기존 선택한 아이템들 초기화

            if(isChecked){
                selectedClientIds.addAll(it.clients.map { client -> client.clientId })
                // 배경색 변경
                val backgroundDrawable: Drawable? by lazy {ContextCompat.getDrawable(this, R.drawable.fragment_list_tool_purple) }
                customerAnyListAdapter.updateItemBackground(backgroundDrawable)
            }
            else{
                // 배경색 변경
                selectedClientIds.removeAll { true }
                val backgroundDrawable: Drawable? by lazy { ContextCompat.getDrawable(this, R.drawable.fragment_list_tool) }
                customerAnyListAdapter.updateItemBackground(backgroundDrawable)
            }
        }

        //리사이클러뷰 클릭
        customerAnyListAdapter.setOnItemClickListener(object :
            CustomerAnyListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val selectedClientId = it.clients[position].clientId
                binding.topTvNumber1.text = selectedClientIds.size.toString()
                // 리스트에 클릭한 아이템의 clientId 추가 또는 삭제
                if (selectedClientIds.contains(selectedClientId)) {
                    //selectedClientIds.remove(selectedClientId)
                } else {
                    selectedClientIds.add(selectedClientId)
                }
            }
        })
    }
}