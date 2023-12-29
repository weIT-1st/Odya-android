package com.weit.domain.model.user

data class SearchUserRequestInfo(
    val size: Int? = null,
    val lastId:Long? = null,
    val nickname:String,
)