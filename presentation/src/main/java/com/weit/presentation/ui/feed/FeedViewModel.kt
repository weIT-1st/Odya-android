package com.weit.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.community.GetCommunitiesByTopicUseCase
import com.weit.domain.usecase.community.GetCommunitiesUseCase
import com.weit.domain.usecase.community.GetFriendCommunitiesUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.GetMayknowUsersUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.ui.util.Constants.feedAll
import com.weit.presentation.ui.util.Constants.feedFriend
import com.weit.presentation.ui.util.Constants.feedTopic
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
    private val getTopicListUseCase: GetTopicListUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val getMayknowUsersUseCase: GetMayknowUsersUseCase,
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCommunitiesByTopicUseCase: GetCommunitiesByTopicUseCase,
    private val getFriendCommunitiesUseCase: GetFriendCommunitiesUseCase,
) : ViewModel() {

    val user = MutableStateFlow<User?>(null)
    private var feedState = feedAll

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private val totalFeed = CopyOnWriteArrayList<Feed>()
    private val feedItems = CopyOnWriteArrayList<Feed.FeedItem>()
    private val popularLogs = CopyOnWriteArrayList<PopularTravelLog>()
    private val friends = CopyOnWriteArrayList<FollowUserContent>()

    private val _feed = MutableStateFlow<List<Feed>>(emptyList())
    val feed: StateFlow<List<Feed>> get() = _feed

    private var friendLastId: Long? = null
    private var pageJob: Job = Job().apply {
        complete()
    }

    private var feedLastId: Long? = null
    private var getFeedJob: Job = Job().apply {
        complete()
    }
    private var selectedTopicId: Long = 0
    private var topicFeedLastId: Long? = null
    private var friendFeedLastId: Long? = null

    private var topicList = CopyOnWriteArrayList<FeedTopic>()
    init {

        viewModelScope.launch {
            getUserUseCase().onSuccess {
                user.value = it
            }
        }

        getTopicList()
        onNextFeeds(null)
        getPopularTravelLogs()
        onNextFriends()
        makeFeedItems()
    }

    fun updateTopicUI(position: Int) {
        viewModelScope.launch {
            val newTopics = topicList.mapIndexed { index, feedTopic ->
                if (index == position) {
                    feedTopic.copy(isChecked = true)
                } else {
                    feedTopic.copy(isChecked = false)
                }
            }
            topicList.clear()
            topicList.addAll(newTopics)
            _event.emit(Event.OnChangeFavoriteTopics(newTopics))
        }
    }


    private fun getTopicList() {
        viewModelScope.launch {
            val result = getTopicListUseCase()
            if (result.isSuccess) {
                val topics = result.getOrThrow().map{
                    FeedTopic(
                        it.topicId,it.topicWord,false
                    )
                }
                topicList.addAll(topics)
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

            totalFeed.addAll(feedList.subList(0, feedSizeBeforeLog))

            if (popularTravelLogList.popularTravelLogList.isNotEmpty()) {
                totalFeed.add(popularTravelLogList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE) {
                val feedSizeAfterLog = min(feedList.size - MINIMUM_FEED_SIZE, MINIMUM_FEED_SIZE)
                totalFeed.addAll(
                    feedList.subList(
                        MINIMUM_FEED_SIZE,
                        MINIMUM_FEED_SIZE + feedSizeAfterLog
                    )
                )
            }

            if (mayKnowFriendList.mayKnowFriendList.isNotEmpty()) {
                totalFeed.add(mayKnowFriendList)
            }

            if (feedList.size > MINIMUM_FEED_SIZE_DOUBLE) {
                totalFeed.addAll(feedList.subList(MINIMUM_FEED_SIZE_DOUBLE, feedList.size))
            }

            _feed.emit(totalFeed.toList())
        }
    }

    fun selectFeedFriend(){
        feedState = feedFriend
        friendFeedLastId = null
        onNextFeeds()
    }

    fun selectFeedAll(){
        feedState = feedAll
        feedLastId = null
        onNextFeeds()
    }

    fun onNextFeeds(topicId: Long?= null) {
        if(topicFeedLastId == null || feedLastId==null || friendFeedLastId == null){
            feedItems.clear()
        }
        if (getFeedJob.isCompleted.not()) {
            return
        }

        //토픽 눌렀을 때
        if(topicId != null){
            feedState = feedTopic
            selectedTopicId = topicId
        }
        loadNextFeeds()
    }

    private fun changeFeedItems(newContents: List<CommunityMainContent>){
        val feeds = newContents.map {
            Feed.FeedItem(
                it.communityId,
                it.communityContent,
                it.placeId,
                it.communityMainImageUrl,
                it.writer,
                it.travelJournalSimpleResponse,
                it.communityCommentCount,
                it.communityLikeCount
            )
        }
        val original = feedItems
        feedItems.clear()
        feedItems.addAll(original + CopyOnWriteArrayList(feeds))
        makeFeedItems()
    }

    private fun loadNextFeeds() {
        getFeedJob = viewModelScope.launch {
            when(feedState) {
                feedAll -> {
                    val result = getCommunitiesUseCase(
                        CommunityRequestInfo(DEFAULT_PAGE_SIZE, feedLastId)
                    )
                    if (result.isSuccess) {
                        val newContents = result.getOrThrow()
                        feedLastId = newContents[newContents.lastIndex].communityId
                        if (newContents.isEmpty()) {
                            loadNextFeeds()
                        }
                        changeFeedItems(newContents)
                    } else {
                        Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
                    }
                }

                feedFriend -> {
                    val result = getFriendCommunitiesUseCase(
                        CommunityRequestInfo(DEFAULT_PAGE_SIZE, friendFeedLastId)
                    )
                    if (result.isSuccess) {
                        val newContents = result.getOrThrow()
                        if (!newContents.isEmpty()) {
                            friendFeedLastId = newContents[newContents.lastIndex].communityId
                        }
                        if (newContents.isEmpty()) {
                            onNextFeeds()
                        }
                        changeFeedItems(newContents)
                    } else {
                        handleError(result.exceptionOrNull() ?: UnKnownException())
                        Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
                    }
                }

                feedTopic -> {
                    val result = getCommunitiesByTopicUseCase(
                        selectedTopicId,CommunityRequestInfo(DEFAULT_PAGE_SIZE, topicFeedLastId)
                    )
                    if (result.isSuccess) {
                        val newContents = result.getOrThrow()
                        if(!newContents.isEmpty()){
                            topicFeedLastId = newContents[newContents.lastIndex].communityId
                        }
                        if (newContents.isEmpty()) {
                            onNextFeeds(selectedTopicId)
                        }
                        changeFeedItems(newContents)
                    } else {
                        handleError(result.exceptionOrNull() ?: UnKnownException())
                    }
                }
            }
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

    fun onNextFriends() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextFriends()
    }

    private fun loadNextFriends() {
        pageJob = viewModelScope.launch {
            val result = getMayknowUsersUseCase(
                MayknowUserSearchInfo(
                    DEFAULT_PAGE_SIZE, friendLastId
                )
            )
            if (result.isSuccess) {
                val newFriends = result.getOrThrow()
//                Logger.t("MainTest").i("${newFriends}")

                if (newFriends.size > 0) {
                    friendLastId = newFriends[newFriends.lastIndex].userId
                }
                if (newFriends.isEmpty()) {
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
            if (feed.writer.userId == userId) {
                val newWriter = object : CommunityUser {
                    override val userId: Long = feed.writer.userId
                    override val nickname: String = feed.writer.nickname
                    override val profile: UserProfile = feed.writer.profile
                    override val isFollowing: Boolean = !followState
                }
                feed.copy(writer = newWriter)
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
        } else {
            //알 수도 있는 친구를 다시 부른다
            friendLastId = null
            loadNextFriends()
            onChangeFeedAndFriendItems(userId, followState)
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
        is NoMoreItemException -> {
            topicFeedLastId = null
            friendFeedLastId = null
            feedLastId = null
        }
        else -> _event.emit(Event.UnknownException)
    }
}

sealed class Event {
    data class OnChangeFavoriteTopics(
        val topics: List<FeedTopic>,
    ) : Event()

    object CreateAndDeleteFollowSuccess : Event()
    object NotExistTopicIdException : Event()
    object InvalidRequestException : Event()
    object InvalidTokenException : Event()
    object NotHavePermissionException : Event()
    object ExistedFollowingIdException : Event()
    object UnknownException : Event()
}

companion object {
    private const val MINIMUM_FEED_SIZE = 2
    private const val MINIMUM_FEED_SIZE_DOUBLE = 4
    private const val DEFAULT_PAGE_SIZE = 20

}
}


