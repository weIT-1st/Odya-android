package com.weit.domain.model.exception.auth

import java.lang.Exception

// 서버에서 리턴 토큰 생성에 실패하거나 회원 정보 요청 통신에 문제가 생기면 해당 에러를 반환합니다.
class ServerLoginFailedException : Exception()
