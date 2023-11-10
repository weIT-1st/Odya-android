package com.weit.presentation.model

import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.community.CommunityTravelJournal
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.model.user.UserProfile
import java.time.LocalDateTime

sealed class Feed {
    data class FeedItem(
        val communityId: Long,
        val communityContent: String,
        val placeId: String?,
        val communityMainImageUrl: String,
        val writer: CommunityUser,
        val travelJournalSimpleResponse: CommunityTravelJournal?,
        val communityCommentCount: Int,
        val communityLikeCount: Int,
        val isUserLiked: Boolean,
        val createdDate: LocalDateTime,
    ) : Feed()
    data class PopularTravelLogItem(val popularTravelLogList: List<PopularTravelLog>) : Feed()
    data class MayknowFriendItem(val mayKnowFriendList: List<FollowUserContent>) : Feed()
}
data class TopicDTO(
    override val topicId: Long,
    override val topicWord: String,
) : TopicDetail

data class MayKnowFriend(
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val userFeature: String,
    val followState: Boolean,
)
data class PopularTravelLog(
    val travelLogId: Long,
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val travelLogTitle: String,
    val travelLogImage: String,
)

data class FeedDTO(
    val feedId: Long,
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val followState: Boolean,
    val feedImage: String,
    val travelLog: TravelLogInFeed?,
    val date: String,
    val content: String,
    val likeNum: Int,
    val commentNum: Int,
    val place: String?,
)

data class FeedDetail(
    val feedId: Long,
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val followState: Boolean,
    val feedImage: String,
    val travelLog: TravelLogInFeed?,
    val date: String,
    val content: String,
    val likeNum: Int,
    val commentNum: Int,
    val place: String?,
    val topics: List<TopicDTO>,
)

data class FeedComment(
    val commentId: Long,
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val content: String,
)

data class TravelLogInFeed(
    val travelLogId: Long,
    val travelLogTitle: String,
    val travelLogImage: String,
)
