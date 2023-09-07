package com.pg.gajamap.base

import com.pg.gajamap.data.model.AutoLoginGroupInfo
import com.pg.gajamap.data.model.GetAllClientResponse

object UserData {
    var clientListResponse: GetAllClientResponse? = null
    var imageUrlPrefix : String?=null
    var groupinfo: AutoLoginGroupInfo? = null
}