package com.weit.domain.model.community

data class CommunityUpdateInfo(
    val content: String,
    val visibility: String,
    val placeId: String,
    val travelJournalId: Long,
    val topicId: Long,
    val deleteCommunityContentImageIds: List<Long>,
 ) {
}