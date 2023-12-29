package com.weit.domain.model.community

data class CommunityRegistrationInfo(
    val content: String,
    val visibility: String,
    val placeId: String? = null,
    val travelJournalId: Long? = null,
    val topicId: Long? = null,
 ) {
}