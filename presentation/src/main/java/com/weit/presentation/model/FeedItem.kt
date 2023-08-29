package com.weit.presentation.model

import com.weit.domain.model.user.UserProfile

sealed class Feed {
    data class FeedItem(
        val feedId: Long,
        val userId: Long,
        val userProfile: UserProfile? = null,
        val userNickname: String,
        val followState: Boolean,
        val feedImage: String?,
        val travelLogId: Long?,
        val travelLogTitle: String?,
        val date: String?,
        val content: String?,
        val likeNum: Int?,
        val commentNum: Int?,
        val place: String?,
    ) : Feed()
    data class PopularTravelLogItem(val popularTravelLogList: List<PopularTravelLog>) : Feed()
    data class MayknowFriendItem(val mayKnowFriendList: List<MayKnowFriend>) : Feed()
}

data class MayKnowFriend(
    val userId: Long,
    val userProfile: UserProfile? = null,
    val userNickname: String,
    val userFeature: String,
    val followState: Boolean,
)
data class PopularTravelLog(
    val travelLogId: Long,
    val userId: Long,
    val userProfile: UserProfile? = null,
    val userNickname: String,
    val travelLogTitle: String,
    val travelLogImage: String,
)

data class FeedDTO(
    val feedId: Long,
    val userId: Long,
    val userProfile: UserProfile? = null,
    val userNickname: String,
    val followState: Boolean,
    val feedImage: String?,
    val travelLogId: Long?,
    val travelLogTitle: String?,
    val date: String?,
    val content: String?,
    val likeNum: Int?,
    val commentNum: Int?,
    val place: String?,
)
