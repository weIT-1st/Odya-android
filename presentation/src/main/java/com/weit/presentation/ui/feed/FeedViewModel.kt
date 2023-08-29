package com.weit.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.exception.topic.NotHavePermissionException
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.follow.CreateFollowCreateUseCase
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.topic.RegisterFavoriteTopicUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.FeedDTO
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Integer.min
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerFavoriteTopicUseCase: RegisterFavoriteTopicUseCase,
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
    private val createFollowCreateUseCase: CreateFollowCreateUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    init {
        getFavoriteTopicList()
        makeFeedItems()
    }

    fun getFavoriteTopicList() {
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

    fun makeFeedItems() {
        viewModelScope.launch {
            val items = mutableListOf<Feed>()

            var feedList = getFeeds()
            var popularTravelLogList = getPopularTravelLogs()
            val mayKnowFriendList = getMayknowFriends()

            val feedIdxBefore = min(feedList.size, 2)
            var feedIdxAfter = min(feedList.size - 2, 2)

            for (i in 0 until feedIdxBefore) {
                items.add(feedList[i])
            }

            if (popularTravelLogList.popularTravelLogList.size > 0) {
                items.add(popularTravelLogList)
            }

            if (feedList.size > 2) {
                for (i in 2 until 2 + feedIdxAfter) {
                    items.add(feedList[i])
                }
            }

            if (mayKnowFriendList.mayKnowFriendList.size > 0) {
                items.add(mayKnowFriendList)
            }

            if (feedList.size > 4) {
                for (i in 4 until feedList.size) {
                    items.add(feedList[i])
                }
            }

            _event.emit(Event.OnChangeFeeds(items))
        }
    }

    private fun getFeeds(): List<Feed.FeedItem> {
        val feedList = listOf(
            FeedDTO(1, 1, null, "dd", true, "dd", null, "Dd", "dd", "ddddddddd", 100, 100, "dd"),
            FeedDTO(2, 2, null, "dd", false, "dd", 1253, "Dd", "dd", "dddddddddd", 10, 9, "dd"),
            FeedDTO(2, 2, null, "dd", true, "dd", 1253, "Dd", "dd", "dsfdfsdfd", 10, 9, "dd"),
            FeedDTO(2, 2, null, "dd", true, "dd", 1253, "Dd", "dd", "dsfdfsdfd", 10, 9, "dd"),
            FeedDTO(2, 2, null, "dd", true, "dd", 1253, "Dd", "dd", "dsfdfsdfd", 10, 9, "dd"),
        )
        val feeds = feedList.map {
            Feed.FeedItem(
                it.feedId,
                it.userId,
                null,
                it.userNickname,
                it.followState,
                it.feedImage,
                it.travelLogId,
                it.travelLogTitle,
                it.date,
                it.content,
                it.likeNum,
                it.commentNum,
                it.place,
            )
        }

        return feeds
    }

    private fun getPopularTravelLogs(): Feed.PopularTravelLogItem {
        val popularSpotList = listOf(
            PopularTravelLog(12, 1, null, "dd", "dd", "dd"),
            PopularTravelLog(13, 2, null, "dd", "dd", "dd"),
        )

        return Feed.PopularTravelLogItem(popularSpotList)
    }

    private fun getMayknowFriends(): Feed.MayknowFriendItem {
        val mayKnowFriendList = listOf(
            MayKnowFriend(1, null, "ari", "함께 아는 친구 2명", true),
            MayKnowFriend(2, null, "ari", "함께 아는 친구 2명", true),
            MayKnowFriend(3, null, "ari", "함께 아는 친구 2명", true),
        )
        return Feed.MayknowFriendItem(mayKnowFriendList)
    }

    fun onFollowStateChange(userId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            Logger.t("MainTest").i("feed like $userId")
            if (isChecked) {
                val result = createFollowCreateUseCase(FollowFollowingIdInfo(userId))
                if (result.isSuccess) {
                    _event.emit(Event.CreateFollowSuccess)
                } else {
                    handleError(result.exceptionOrNull() ?: UnKnownException())
                    Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                }
            } else {
                val result = deleteFollowUseCase(FollowFollowingIdInfo(userId))
                if (result.isSuccess) {
                    _event.emit(Event.DeleteFollowSuccess)
                } else {
                    handleError(result.exceptionOrNull() ?: UnKnownException())
                    Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                }
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is ExistedFollowingIdException -> _event.emit(Event.ExistedFollowingIdException)
            is NotExistTopicIdException -> _event.emit(Event.NotExistTopicIdException)
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is NotHavePermissionException -> _event.emit(Event.NotHavePermissionException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {
        data class OnChangeFeeds(
            val feeds: List<Feed>,
        ) : Event()
        data class OnChangeFavoriteTopics(
            val topics: List<TopicDetail>,
        ) : Event()
        object CreateFollowSuccess : Event()
        object DeleteFollowSuccess : Event()
        object NotExistTopicIdException : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object ExistedFollowingIdException : Event()
        object UnknownException : Event()
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
