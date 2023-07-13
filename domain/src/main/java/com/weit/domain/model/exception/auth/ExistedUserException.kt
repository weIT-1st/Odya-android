package com.weit.domain.model.exception.auth

// 회원가입 시 유효한 정보를 넣었지만 이미 존재하는 유저일 때 해당 에러를 반환합니다.
class ExistedUserException : Exception()
