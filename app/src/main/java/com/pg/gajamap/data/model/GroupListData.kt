package com.pg.gajamap.data.model

data class GroupListData(
    val img: Int,
    val id: Long,
    var name: String,
    var person: String,
    var isSelected: Boolean,
    var whole: Boolean
)
