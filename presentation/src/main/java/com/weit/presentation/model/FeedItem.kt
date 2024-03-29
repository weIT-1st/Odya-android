package com.weit.presentation.model

import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.community.CommunityTravelJournal
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.journal.TravelJournalListInfo
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
    data class PopularTravelLogItem(val popularTravelLogList: List<TravelJournalListInfo>) : Feed()
    data class MayknowFriendItem(val mayKnowFriendList: List<FollowUserContent>) : Feed()
}

data class PopularTravelLog(
    val travelLogId: Long,
    val userId: Long,
    val userProfile: UserProfile,
    val userNickname: String,
    val travelLogTitle: String,
    val travelLogImage: String,
)


