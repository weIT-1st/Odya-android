package com.weit.domain.model.exception.auth

import java.lang.Exception

// 카카오 로그인이 되지 않은 상태면 해당 에러를 반환합니다.
class UserNotFoundException : Exception()
