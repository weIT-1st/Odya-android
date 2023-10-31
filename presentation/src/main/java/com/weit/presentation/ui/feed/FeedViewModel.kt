package com.weit.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.model.user.User
import com.weit.domain.usecase.community.GetCommunitiesUseCase
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.GetMayknowUsersUseCase
import com.weit.domain.usecase.image.GetImagesUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.FeedDTO
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.login.nickname.LoginNicknameViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val getMayknowUsersUseCase: GetMayknowUsersUseCase,
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    val user = MutableStateFlow<User?>(null)

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private val totalFeed = CopyOnWriteArrayList<Feed>()
    private val feedItems = CopyOnWriteArrayList<Feed.FeedItem>()
    private val popularLogs = CopyOnWriteArrayList<PopularTravelLog>()
    private val friends = CopyOnWriteArrayList<FollowUserContent>()

    private val _feed = MutableStateFlow<List<Feed>>(emptyList())
    val feed : StateFlow<List<Feed>> get() =  _feed

    private var pageJob: Job = Job().apply {
        complete()
    }

    private var lastId :Long? = null
    //    private val _friends = MutableStateFlow<List<MayKnowFriend>>(emptyList())
//    val friends : StateFlow<List<MayKnowFriend>> get() = _friends
    init {

        viewModelScope.launch {
            getUserUseCase().onSuccess {
                user.value = it
            }
        }

        getFavoriteTopicList()
        getFeeds()
        getPopularTravelLogs()
        loadNextFriends()
//        getMayknowFriends()
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
            totalFeed.clear()
            val feedList = feedItems
            val popularTravelLogList = Feed.PopularTravelLogItem(popularLogs)
            val mayKnowFriendList = Feed.MayknowFriendItem(friends)

            val feedSizeBeforeLog = min(feedList.size, MINIMUM_FEED_SIZE)

            totalFeed.addAll(feedList.subList(0,feedSizeBeforeLog))

            if (popularTravelLogList.popularTravelLogList.isNotEmpty()) {
                totalFeed.add(popularTravelLogList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE) {
                val feedSizeAfterLog = min(feedList.size - MINIMUM_FEED_SIZE, MINIMUM_FEED_SIZE)
                totalFeed.addAll(feedList.subList(MINIMUM_FEED_SIZE,MINIMUM_FEED_SIZE+feedSizeAfterLog))
            }

            if (mayKnowFriendList.mayKnowFriendList.isNotEmpty()) {
                totalFeed.add(mayKnowFriendList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE_DOUBLE) {
                totalFeed.addAll(feedList.subList(MINIMUM_FEED_SIZE_DOUBLE,feedList.size))
            }

            _feed.emit(totalFeed.toList())
        }
    }

    private fun getFeeds() {
        viewModelScope.launch {

//            val result = getCommunitiesUseCase(
//                CommunityRequestInfo()
//            )
//            if (result.isSuccess) {
//                Logger.t("MainTest").i("${result.getOrThrow()}")
//
//            } else {
//                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
//            }

            val profile =
                UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
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
            feedItems.addAll(CopyOnWriteArrayList(feeds))
        }
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

        fun onNextFriends(){
        if(pageJob.isCompleted.not()){
            return
        }
        loadNextFriends()
    }

    private fun loadNextFriends(){
        pageJob = viewModelScope.launch {
            val result = getMayknowUsersUseCase(
                MayknowUserSearchInfo(
                    DEFAULT_PAGE_SIZE,lastId
                ))
            if(result.isSuccess){
                val newFriends = result.getOrThrow()
                Logger.t("MainTest").i("${newFriends}")

                if(newFriends.size>0){
                    lastId = newFriends[newFriends.lastIndex].userId
                }
                if (newFriends.isEmpty()){
                    loadNextFriends()
                }
                val originalFriends = friends
                friends.clear()
                friends.addAll(originalFriends + newFriends)

            }

        }
    }

    private fun onChangeFeedAndFriendItems(userId: Long, followState: Boolean) {

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
            val result = changeFollowStateUseCase(userId, !followState)
            if (result.isFailure) {
                onChangeFeedAndFriendItems(userId, !followState)
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }else{
                //알 수도 있는 친구를 다시 부른다
                lastId = null
                loadNextFriends()
                onChangeFeedAndFriendItems(userId, followState)
            }
        }
    }

    fun onSelectPictures(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
             _event.emit(Event.OnSelectPictures(images))
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
        data class OnSelectPictures(
            val uris: List<String>,
        ) : Event()
        object NotSelectedFeedImages : Event()
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
        private const val DEFAULT_PAGE_SIZE = 20
    }
}

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
