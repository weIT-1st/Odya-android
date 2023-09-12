package com.weit.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.FeedDTO
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Integer.min
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private val allFeed = ArrayList<Feed>()
    private val feedItems = ArrayList<Feed.FeedItem>()
    private val popularLogs = ArrayList<PopularTravelLog>()
    private val friends = ArrayList<MayKnowFriend>()

    private val _changeFeedEvent = MutableStateFlow<List<Feed>>(emptyList())
    val changeFeedEvent : StateFlow<List<Feed>> get() =  _changeFeedEvent

    init {
        getFavoriteTopicList()
        getFeeds()
        getPopularTravelLogs()
        getMayknowFriends()
        makeFeedItems()
    }

    private fun getFavoriteTopicList() {
        viewModelScope.launch {
            val result = getFavoriteTopicListUseCase()
            if (result.isSuccess) {
                val topics = result.getOrThrow()
                _event.emit(Event.OnChangeFavoriteTopics(topics))
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun makeFeedItems() {
        viewModelScope.launch {
            allFeed.clear()
            val feedList = feedItems
            val popularTravelLogList = Feed.PopularTravelLogItem(popularLogs)
            val mayKnowFriendList = Feed.MayknowFriendItem(friends)

            val feedSizeBeforeLog = min(feedList.size, MINIMUM_FEED_SIZE)

            allFeed.addAll(feedList.subList(0,feedSizeBeforeLog))

            if (popularTravelLogList.popularTravelLogList.isNotEmpty()) {
                allFeed.add(popularTravelLogList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE) {
                val feedSizeAfterLog = min(feedList.size - MINIMUM_FEED_SIZE, MINIMUM_FEED_SIZE)
                allFeed.addAll(feedList.subList(MINIMUM_FEED_SIZE,MINIMUM_FEED_SIZE+feedSizeAfterLog))
            }

            if (mayKnowFriendList.mayKnowFriendList.isNotEmpty()) {
                allFeed.add(mayKnowFriendList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE_DOUBLE) {
                allFeed.addAll(feedList.subList(MINIMUM_FEED_SIZE_DOUBLE,feedList.size))
            }

            _changeFeedEvent.emit(allFeed.toList())
        }
    }

    private fun getFeeds() {
        val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
        val travelLog = TravelLogInFeed(1, "ddd", "")
        val feedList = listOf<FeedDTO>(
            FeedDTO(1, 4, profile, "dd", true, "dd", null, "Dd", "dd", 100, 100, "dd"),
            FeedDTO(2, 5, profile, "dd", false, "dd", travelLog, "Dd", "dd", 10, 9, "dd"),
            FeedDTO(2, 6, profile, "dd", true, "dd", travelLog, "Dd", "dd", 10, 9, "dd"),
            FeedDTO(2, 2, profile, "dd", true, "dd", travelLog, "Dd", "dd", 10, 9, "dd"),
            FeedDTO(2, 2, profile, "dd", true, "dd", travelLog, "Dd", "dd", 10, 9, "dd"),
        )
        val feeds = feedList.map {
            Feed.FeedItem(
                it.feedId,
                it.userId,
                it.userProfile,
                it.userNickname,
                it.followState,
                it.feedImage,
                it.travelLog,
                it.date,
                it.content,
                it.likeNum,
                it.commentNum,
                it.place,
            )
        }
        feedItems.clear()
        feedItems.addAll(ArrayList(feeds))
    }

    private fun getPopularTravelLogs() {
        val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
        val popularSpotList = arrayListOf<PopularTravelLog>(
            PopularTravelLog(12, 1, profile, "dd", "dd", "dd"),
            PopularTravelLog(13, 2, profile, "dd", "dd", "dd"),
        )

        popularLogs.clear()
        popularLogs.addAll(popularSpotList)
    }

    private fun getMayknowFriends() {
        val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
        val mayKnowFriendList = arrayListOf<MayKnowFriend>(
            MayKnowFriend(4, profile, "ari", "함께 아는 친구 2명", true),
            MayKnowFriend(5, profile, "ari", "함께 아는 친구 2명", false),
            MayKnowFriend(6, profile, "ari", "함께 아는 친구 2명", true),
        )
        friends.clear()
        friends.addAll(mayKnowFriendList)
    }

    private fun onChangeFeedAndFriendItems(userId: Long, followState: Boolean) {
        val newFriends = friends.map { friends ->
            if (friends.userId == userId) {
                friends.copy(followState = !followState)
            } else {
                friends
            }
        }
        friends.clear()
        friends.addAll(newFriends)
        val newFeedItems = feedItems.map { feed ->
            if (feed.userId == userId) {
                feed.copy(followState = !followState)
            } else {
                feed
            }
        }
        feedItems.clear()
        feedItems.addAll(newFeedItems)
        makeFeedItems()
    }
    fun onFollowStateChange(userId: Long, followState: Boolean) {
        viewModelScope.launch {
            onChangeFeedAndFriendItems(userId, followState)
            val result = changeFollowStateUseCase(userId, !followState)
            if (result.isFailure) {
                onChangeFeedAndFriendItems(userId, !followState)
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is ExistedFollowingIdException -> _event.emit(Event.ExistedFollowingIdException)
            is NotExistTopicIdException -> _event.emit(Event.NotExistTopicIdException)
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is InvalidPermissionException -> _event.emit(Event.NotHavePermissionException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {
        data class OnChangeFavoriteTopics(
            val topics: List<TopicDetail>,
        ) : Event()
        object CreateAndDeleteFollowSuccess : Event()
        object NotExistTopicIdException : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object ExistedFollowingIdException : Event()
        object UnknownException : Event()
    }

    companion object{
       private const val MINIMUM_FEED_SIZE = 2
        private const val MINIMUM_FEED_SIZE_DOUBLE = 4
    }
}

// feedFragment에서는 필요없지만 다른 곳에서 쓰일테니 여기둠
// private fun getTopicList() {
//    viewModelScope.launch {
//        val result = getTopicListUseCase()
//        if (result.isSuccess) {
//            val topic = result.getOrThrow()
//            Logger.t("MainTest").i("$topic")
//        } else {
//            Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
//        }
//    }
// }
// private fun addFavoriteTopic() {
//    viewModelScope.launch {
//        val result = registerFavoriteTopicUseCase(
//            listOf(1, 2),
//        )
//        if (result.isSuccess) {
//            Logger.t("MainTest").i("성공!")
//        } else {
//            handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
//            Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
//        }
//    }
// }
