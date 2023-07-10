package com.example.gajamap.ui.fragment.setting

import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentPhoneBinding
import com.example.gajamap.viewmodel.SettingViewModel
import android.provider.Settings
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gajamap.ui.adapter.PhoneListAdapter

class PhoneFragment: BaseFragment<FragmentPhoneBinding>(R.layout.fragment_phone) {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }
    private var contactsList = ArrayList<ContactsData>()
    private var phoneListAdapter : PhoneListAdapter? = null
    private val ACCESS_FINE_LOCATION = 1000

    override val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@PhoneFragment
        binding.fragment = this@PhoneFragment
    }

    override fun onCreateAction() {

        //스피너
        val itemList = listOf("그룹선택", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.settingPhoneSpinner.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        //연락처 권한
        onCheckContactsPermission()
        requestPermission()

        //리사이클러뷰

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
    }

    // 연락처 리사이클러뷰
    private fun setContacts() {
        phoneListAdapter = PhoneListAdapter(contactsList)
        binding.phoneListRv.apply {
            adapter = phoneListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(PhoneListVerticalItemDecoration())
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

}