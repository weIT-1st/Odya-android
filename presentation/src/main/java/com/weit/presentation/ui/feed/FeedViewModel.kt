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
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.CreateFollowCreateUseCase
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.topic.RegisterFavoriteTopicUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.FeedDTO
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.model.post.travellog.DailyTravelLog
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getTopicListUseCase: GetTopicListUseCase,
    private val registerFavoriteTopicUseCase: RegisterFavoriteTopicUseCase,
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
    private val createFollowCreateUseCase: CreateFollowCreateUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    ) : ViewModel() {

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private var allFeed = ArrayList<Feed>()
    private var feedItems = ArrayList<Feed.FeedItem>()
    private var popularLogs = ArrayList<PopularTravelLog>()
    private var frineds = ArrayList<MayKnowFriend>()

    private val _changeFeedEvent = MutableEventFlow<List<Feed>>()
    val changeFeedEvent = _changeFeedEvent.asEventFlow()

    init {
        getFavoriteTopicList()
        getFeeds()
        getPopularTravelLogs()
        getMayknowFriends()
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

            var feedList = feedItems
            var popularTravelLogList = Feed.PopularTravelLogItem(popularLogs)
            val mayKnowFriendList = Feed.MayknowFriendItem(frineds)

            val feedIdxBefore = min(feedList.size, 2)
            var feedIdxAfter = min(feedList.size - 2, 2)

            for (i in 0 until feedIdxBefore) {
                allFeed.add(feedList[i])
            }

            if (popularTravelLogList.popularTravelLogList.size > 0) {
                allFeed.add(popularTravelLogList)
            }

            if (feedList.size > 2) {
                for (i in 2 until 2 + feedIdxAfter) {
                    allFeed.add(feedList[i])
                }
            }

            if (mayKnowFriendList.mayKnowFriendList.size > 0) {
                allFeed.add(mayKnowFriendList)
            }

            if (feedList.size > 4) {
                for (i in 4 until feedList.size) {
                    allFeed.add(feedList[i])
                }
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
        feedItems = ArrayList(feeds)
    }

    private fun getPopularTravelLogs() {
        val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
        val popularSpotList = arrayListOf<PopularTravelLog>(
            PopularTravelLog(12, 1, profile, "dd", "dd", "dd"),
            PopularTravelLog(13, 2, profile, "dd", "dd", "dd"),
        )

        popularLogs = popularSpotList
    }

    private fun getMayknowFriends() {
        val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
        val mayKnowFriendList = arrayListOf<MayKnowFriend>(
            MayKnowFriend(4, profile, "ari", "함께 아는 친구 2명", true),
            MayKnowFriend(5, profile, "ari", "함께 아는 친구 2명", false),
            MayKnowFriend(6, profile, "ari", "함께 아는 친구 2명", true),
        )
        frineds = mayKnowFriendList
    }

    fun onFollowStateChange(type:String, position:Int, userId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            Logger.t("position").i(isChecked.toString())
            val result = changeFollowStateUseCase(userId, !isChecked)
            if (result.isSuccess) {
                when(type){
                    "feed" -> {
                        feedItems[position] = feedItems[position].copy(followState = !isChecked)
                        val newFriends = frineds.map { friend ->
                            if (friend.userId == userId)
                                friend.copy(followState = !isChecked)
                            else
                                friend
                        }
                        frineds.clear()
                        frineds.addAll(newFriends)
                    }
                    else -> {
                        frineds[position] = frineds[position].copy(followState = !isChecked)
                        val newFeedItems = feedItems.map { feed ->
                            if(feed.userId == userId)
                                feed.copy(followState = !isChecked)
                            else
                                feed
                        }
                        feedItems.clear()
                        feedItems.addAll(newFeedItems)
                    }
                }
                Logger.t("MainTest").i("성공임돠")
                Logger.t("position").i(feedItems[0].followState.toString())


                makeFeedItems()
            } else {
                when(type){
                    "feed" -> feedItems[position] = feedItems[position].copy(followState = isChecked)
                    else -> frineds[position] = frineds[position].copy(followState = isChecked)
                }
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")

                makeFeedItems()
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
        data class OnChangeFeeds(
            val feeds: List<Feed>,
        ) : Event()
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
