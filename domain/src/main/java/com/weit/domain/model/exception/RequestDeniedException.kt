package com.weit.domain.model.exception

class RequestDeniedException(val permission: String) : Exception()
