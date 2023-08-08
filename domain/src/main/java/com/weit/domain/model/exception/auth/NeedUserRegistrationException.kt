package com.weit.domain.model.exception.auth

import java.lang.Exception

// 유효한 토큰이지만 서비스에 가입이 되지 않았다면 해당 에러를 반환합니다.
class NeedUserRegistrationException(
    val username: String,
    ) : Exception()
