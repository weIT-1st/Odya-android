package com.weit.domain.model.auth

interface AgreedTermInfo {
    val agreedTermId: Long
    val userId: Long
    val termsId: Long
    val required: Boolean
}
