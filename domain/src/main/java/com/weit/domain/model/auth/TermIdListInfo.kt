package com.weit.domain.model.auth

data class TermIdListInfo(
    val agreedTermsIdList: List<Long>,
    val disagreeTermsIdList: List<Long>,
)
