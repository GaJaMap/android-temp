package com.pg.gajamap.ui.fragment.setting

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactsData(
    val contactsId: Int,
    val name: String,
    val number: String
): Parcelable
