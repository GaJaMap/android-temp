package com.pg.gajamap.ui.fragment.setting

import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.pg.gajamap.BR
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pg.gajamap.R
import com.pg.gajamap.base.BaseFragment
import com.pg.gajamap.databinding.FragmentPhoneBinding
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.base.UserData
import com.pg.gajamap.data.model.Clients
import com.pg.gajamap.data.model.GroupInfoResponse
import com.pg.gajamap.data.model.PostKakaoPhoneRequest
import com.pg.gajamap.ui.adapter.PhoneListAdapter
import com.pg.gajamap.viewmodel.ClientViewModel

class PhoneFragment: BaseFragment<FragmentPhoneBinding>(R.layout.fragment_phone) {

    // 선택된 클라이언트들을 저장하기 위한 리스트
    private var selectedClients: MutableList<Clients?> = mutableListOf()
    private var groupId : Long = -1
    var client = UserData.clientListResponse
    var clientList = UserData.clientListResponse?.clients
    var groupInfo = UserData.groupinfo

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }
    private var contactsList = ArrayList<ContactsData>()
    private var phoneListAdapter : PhoneListAdapter? = null
    private val ACCESS_FINE_LOCATION = 1000


    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@PhoneFragment
        binding.fragment = this@PhoneFragment
    }

    override fun onCreateAction() {

        //var groupInfo = UserData.groupinfo

        //스피너
        viewModel.checkGroup()
        viewModel.checkGroup.observe(this, Observer {
            // GroupResponse에서 GroupInfoResponse의 groupName 속성을 추출하여 리스트로 변환합니다.
            val groupNames = mutableListOf<String>()
            // groupResponse의 groupInfos에서 각 GroupInfoResponse의 groupName을 추출하여 리스트에 추가합니다.
            it.groupInfos.forEach { groupInfo ->
                groupNames.add(groupInfo.groupName)
            }
            //groupNames.add(groupNames.size, "그룹 선택")
            groupNames.add(0,"그룹 선택")
            //그룹 스피너
            /*val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_list, groupNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.infoProfileGroup.adapter = adapter*/
            val adapter = object : ArrayAdapter<String>(requireActivity(), R.layout.spinner_list, groupNames) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val textView = super.getView(position, convertView, parent) as TextView
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black)) // 검정색으로 변경
                    return textView
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val textView = super.getDropDownView(position, convertView, parent) as TextView
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black)) // 검정색으로 변경
                    return textView
                }

            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.settingPhoneSpinner.adapter = adapter

        })

        /*val itemList = listOf("그룹선택", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.infoProfileGroup.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/

        binding.settingPhoneSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                //binding.result.text = data[pos] //배열이라서 []로 된다.
                //textView를 위에서 선언한 리스트(data)와 연결. [pos]는 리스트에서 선택된 항목의 위치값.
                // 스피너에서 선택한 아이템의 그룹 아이디를 가져옵니다.
                //if (pos == 0) return

                if(pos != 0){
                    val selectedGroupInfoResponse: GroupInfoResponse = viewModel.checkGroup.value?.groupInfos?.get(pos - 1) ?: return
                    groupId = selectedGroupInfoResponse.groupId
                    Log.d("groupId", groupId.toString())
                    GajaMapApplication.prefs.setString("groupIdSpinner", groupId.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.topBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, SettingFragment()).addToBackStack(null).commit()
        }


        //연락처 권한
        onCheckContactsPermission()
        requestPermission()

    }

    override fun onResume() {
        super.onResume()
        onCheckContactsPermission()
        //requestPermission()
    }

    //연락처 가져오기
    private fun getContactsList() {

        val contacts = context?.contentResolver
            ?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        val list = ArrayList<ContactsData>()
        contacts?.let {
            while (it.moveToNext()) {
                val contactsId = contacts.getInt(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                list.add(ContactsData(contactsId, name, number))
            }
        }
        list.sortBy { it.name }
        contacts?.close()
        if (contactsList != list) {
            contactsList = list
            setContacts()
        }
        Log.d("phonekakao", contactsList.toString())
    }

    // 연락처 리사이클러뷰
    private fun setContacts() {
        binding.topTvNumber2.text = contactsList.size.toString()

        phoneListAdapter = PhoneListAdapter(contactsList)
        binding.phoneListRv.apply {
            adapter = phoneListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(PhoneListVerticalItemDecoration())
        }

        //전체선택
        binding.settingPhoneCheckEvery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedClients.addAll(contactsList.map {
                    Clients(it.name, it.number)
                } ?: emptyList())
            }
            else {
                selectedClients.clear()
            }
            phoneListAdapter?.setAllItemsChecked(isChecked)
            updateSelectedClientsCount()
        }

        phoneListAdapter?.setOnItemClickListener(object :
            PhoneListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 아이템 클릭시 해당 아이템의 선택 여부를 토글하고 선택된 클라이언트 리스트 업데이트
                val item = contactsList[position]
                Log.d("selectItem", item.toString())
                item.let {
                    if (phoneListAdapter!!.isChecked(position)) {
                        it.name.let { nickname ->
                            selectedClients.add(Clients(nickname, it.number))
                        }
                    } else {
                        it.name.let { nickname ->
                            selectedClients.remove(Clients(nickname, it.number))
                        }
                    }
                    updateSelectedClientsCount()
                }
                /*val item = friends.elements?.get(position)
                item?.let {
                    selectedClients = kakaoFriendAdapter.getSelectedClients().toMutableList()
                }*/
            }
        })
        val groupId1 = GajaMapApplication.prefs.getString("groupIdSpinner", "")
        binding.btnSubmit.setOnClickListener {
            viewModel.postKakaoPhoneClient(PostKakaoPhoneRequest(selectedClients, groupId1.toInt()))
            Log.d("select", selectedClients.toString())
            viewModel.postKakaoPhoneClient.observe(this, Observer {
                if(groupId1.toString() == groupInfo?.groupId.toString()){

                }

            })
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, SettingFragment()).addToBackStack(null).commit()
        }
    }

    private fun onCheckContactsPermission() {
        val permissionDenied = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
        if (permissionDenied) {
            Toast.makeText(requireContext(), "권한이 거절되었습니다.", Toast.LENGTH_SHORT).show()
        }
         else {
            getContactsList()
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (checkSelfPermission(
                    requireContext(),
                    permissions[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onCheckContactsPermission()
            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(requireContext(), "권한이 거절되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("권한")
                        setMessage("권한을 허용하기 위해서 설정으로 이동합니다.")
                        setPositiveButton("확인") { _, _ ->
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    //data = Uri.fromParts("package", "com.example.gajamap", null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            startActivity(intent)
                        }
                        setNegativeButton("거절") { dialog, _ ->
                            dialog.dismiss()
                        }
                        show()
                    }
                }
                }
            }
        }

    private fun updateSelectedClientsCount() {
        binding.topTvNumber1.text = selectedClients.size.toString()
    }

}