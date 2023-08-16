package com.weit.domain.model.exception

import java.lang.Exception

// 무한 스크롤로 아이템을 가져올 때 이전 api의 hasNext가 False임에도 요청을 시도할 때 해당 에러를 반환합니다.
class NoMoreItemException : Exception()
