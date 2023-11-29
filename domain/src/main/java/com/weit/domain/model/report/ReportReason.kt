package com.weit.domain.model.report

enum class ReportReason {
    SPAM, // 스팸 및 홍보글
    PORNOGRAPHY, // 음란성이 포함된 글
    SWEAR_WORD, // 욕설 / 생명 경시 / 혐오 / 차별적인 글
    OVER_POST, // 개시글 도배
    COPYRIGHT_VIOLATION, // 개인정보 노출 및 불법 정보
    INFO_LEAK, // 개인정보 노출 및 불법 정보
    OTHER, // 기타
}
