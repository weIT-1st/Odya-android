package com.weit.domain.model.exception.auth

import java.lang.Exception

// 회원가입 정보(닉네임, 전화번호, 이메일)가 중복될 때 해당 에러를 반환합니다.
// 현재 중복된게 뭔지 구분할 수 있는 error 코드가 없어서 이 에러로 퉁칩니다. (그래서 이름도 애매한 Something)
class DuplicatedSomethingException : Exception()
