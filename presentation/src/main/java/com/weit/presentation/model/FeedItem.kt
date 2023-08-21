package com.weit.presentation.model

sealed class FeedItem {
    data class CommunityItem(var title: String, var nickname: String) : FeedItem()
    data class PopularSpotItem(var popularSpotList: List<PopularSpot>) : FeedItem()
    data class MayknowFriendItem(var mayKnowFriendList: List<MayKnowFriend>) : FeedItem()
}

data class MayKnowFriend(var nickname: String,var feature: String)
data class PopularSpot(var title: String, var nickname: String)
data class Community(var title: String, var nickname: String)
