package com.weit.presentation.ui.feed

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.user.User
import com.weit.domain.usecase.community.ChangeLikeStateUseCase
import com.weit.domain.usecase.community.GetCommunitiesByTopicUseCase
import com.weit.domain.usecase.community.GetCommunitiesUseCase
import com.weit.domain.usecase.community.GetFriendCommunitiesUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.GetMayknowUsersUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.journal.GetRecommendTravelJournalListUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.PopularTravelLog
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.user.CommunityUserImpl
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
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val getMayknowUsersUseCase: GetMayknowUsersUseCase,
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCommunitiesByTopicUseCase: GetCommunitiesByTopicUseCase,
    private val getFriendCommunitiesUseCase: GetFriendCommunitiesUseCase,
    private val changeLikeStateUseCase: ChangeLikeStateUseCase,
    private val getRecommendTravelJournalListUseCase: GetRecommendTravelJournalListUseCase,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> get() = _user

    private var feedState = feedAll


    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private val totalFeed = CopyOnWriteArrayList<Feed>()
    private val feedItems = CopyOnWriteArrayList<Feed.FeedItem>()
    private val popularLogs = CopyOnWriteArrayList<TravelJournalListInfo>()
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
    private var travelJournalJob: Job = Job().apply {
        complete()
    }
    private var selectedTopicId: Long = 0
    private var topicFeedLastId: Long? = null
    private var friendFeedLastId: Long? = null
    private var travelJournalLastId: Long? = null

    private var topicList = CopyOnWriteArrayList<FeedTopic>()

    init {

        viewModelScope.launch {
            getUserUseCase().onSuccess {
                _user.value = it
            }
        }

        getTopicList()
        onNextFeeds()
        onNextJournals()
        onNextFriends()
        makeFeedItems()
    }

    fun updateTopicUI(position: Int?) {
        viewModelScope.launch {
            val newTopics = if (position == null) {
                topicList.map {
                    it.copy(isChecked = false)
                }
            } else {
                topicList.mapIndexed { index, feedTopic ->
                    if (index == position) {
                        feedTopic.copy(isChecked = true)
                    } else {
                        feedTopic.copy(isChecked = false)
                    }
                }
            }
            topicList.clear()
            topicList.addAll(newTopics)
            _event.emit(Event.OnChangeFavoriteTopics(newTopics))
        }
    }


    private fun getTopicList() {
        viewModelScope.launch {
            val result = getFavoriteTopicListUseCase()
            if (result.isSuccess) {
                val topics = result.getOrThrow().map {
                    FeedTopic(
                        it.topicId, it.topicWord, false
                    )
                }
                topicList.addAll(topics)
                _event.emit(Event.OnChangeFavoriteTopics(topics))
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
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


    fun selectFeedFriend() {
        friendFeedLastId = null
        updateTopicUI(null)
        onNextFeeds(null, feedFriend)
    }

    fun selectFeedAll() {
        feedLastId = null
        updateTopicUI(null)
        onNextFeeds(null, feedAll)
    }

    fun selectFeedTopic(topicId: Long, position: Int) {
        topicFeedLastId = null
        updateTopicUI(position)
        onNextFeeds(topicId, feedTopic)
    }

    fun onNextFeeds(topicId: Long? = null, feedState: String = feedAll) {
        if (topicFeedLastId == null || feedLastId == null || friendFeedLastId == null) {
            feedItems.clear()
        }
        if (getFeedJob.isCompleted.not()) {
            return
        }

        if (topicId != null) {
            selectedTopicId = topicId
        }
        loadNextFeeds(feedState)
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun getPlaceName(placeId: String?): String{
        var name = ""
            if(placeId.isNullOrEmpty().not()){
                val placeInfo = getPlaceDetailUseCase(placeId.toString())
                if (placeInfo.name.isNullOrBlank().not()) {
                    name = placeInfo.name.toString()
                }
            }
        return name
    }

    private suspend fun changeFeedItems(newContents: List<CommunityMainContent>) {
            val feeds = newContents.map {
                Feed.FeedItem(
                    it.communityId,
                    it.communityContent,
                    getPlaceName(it.placeId),
                    it.communityMainImageUrl,
                    it.writer,
                    it.travelJournalSimpleResponse,
                    it.communityCommentCount,
                    it.communityLikeCount,
                    it.isUserLiked,
                    it.createdDate,
                )
            }
            Logger.t("MainTest").i("${feeds}")

            val original = feedItems
            feedItems.clear()
            feedItems.addAll(original + CopyOnWriteArrayList(feeds))
            makeFeedItems()
    }

    private fun loadNextFeeds(feedState: String = feedAll) {
        getFeedJob = viewModelScope.launch {
            when (feedState) {
                feedAll -> {
                    val result = getCommunitiesUseCase(
                        CommunityRequestInfo(DEFAULT_PAGE_SIZE, feedLastId)
                    )
                    if (result.isSuccess) {
                        val newContents = result.getOrThrow()
                        Logger.t("MainTest").i("$newContents")

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
                    }
                }

                feedTopic -> {
                    val result = getCommunitiesByTopicUseCase(
                        selectedTopicId, CommunityRequestInfo(DEFAULT_PAGE_SIZE, topicFeedLastId)
                    )
                    if (result.isSuccess) {
                        val newContents = result.getOrThrow()

                        if (!newContents.isEmpty()) {
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

    fun onNextJournals() {
        if (travelJournalJob.isCompleted.not()) {
            return
        }
        loadNextJournals()
    }

    private fun loadNextJournals() {
        travelJournalJob = viewModelScope.launch {
            val result = getRecommendTravelJournalListUseCase(
                DEFAULT_PAGE_SIZE,travelJournalLastId, null
            )
            if (result.isSuccess) {
                val newJournals = result.getOrThrow()
                if (newJournals.isNotEmpty()) {
                    travelJournalLastId = newJournals.last().travelJournalId
                }
                val originalJournals = popularLogs
                popularLogs.clear()
                popularLogs.addAll(originalJournals + newJournals)
            }
        }
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

                if (newFriends.isNotEmpty()) {
                    friendLastId = newFriends.last().userId
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

    private fun replaceFeedItems(newFeedItems: List<Feed.FeedItem>) {
        viewModelScope.launch {
            val newTotalFeed = CopyOnWriteArrayList<Feed>(
                totalFeed.map { existingFeed ->
                    (existingFeed as? Feed.FeedItem)?.let {
                        newFeedItems.find { it.communityId == existingFeed.communityId } ?: it
                    } ?: existingFeed
                }
            )
            totalFeed.clear()
            totalFeed.addAll(newTotalFeed)
            _feed.emit(newTotalFeed.toList())

        }
    }

    private fun onChangeFeedAndFriendItems(userId: Long, followState: Boolean) {
        viewModelScope.launch {
            val newFeedItems = feedItems.map { feed ->
                if (feed.writer.userId == userId) {
                    val newWriter = CommunityUserImpl(
                        feed.writer.userId,
                        feed.writer.nickname,
                        feed.writer.profile,
                        followState
                    )
                    feed.copy(writer = newWriter)
                } else {
                    feed
                }
            }
            feedItems.clear()
            feedItems.addAll(newFeedItems)
            replaceFeedItems(newFeedItems)
        }
    }

    fun onFollowStateChange(communityId: Long) {
        viewModelScope.launch {
            val userId = feedItems.find { it.communityId == communityId }?.writer?.userId ?: -1
            val currentFollowState =
                feedItems.find { it.communityId == communityId }?.writer?.isFollowing ?: false
            val result = changeFollowStateUseCase(userId, !currentFollowState)
            if (result.isSuccess) {
                onNextFriends()
                onChangeFeedAndFriendItems(userId, !currentFollowState)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    private fun onChangeFeedsByLikeState(communityId: Long, changeLikeState: Boolean) {
        viewModelScope.launch {
            val newFeedItems = feedItems.map { feed ->
                if (communityId == feed.communityId) {
                    feed.copy(
                        isUserLiked = changeLikeState,
                        communityLikeCount = if (changeLikeState) feed.communityLikeCount + 1 else feed.communityLikeCount - 1
                    )
                } else {
                    feed
                }
            }
            feedItems.clear()
            feedItems.addAll(newFeedItems)
            replaceFeedItems(newFeedItems)
        }
    }

    fun onLikeStateChange(communityId: Long) {
        viewModelScope.launch {
            val currentLikeState =
                feedItems.find { it.communityId == communityId }?.isUserLiked ?: false
            val result = changeLikeStateUseCase(communityId, !currentLikeState)
            if (result.isSuccess) {
                onChangeFeedsByLikeState(communityId, !currentLikeState)
            } else {
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
            is NoMoreItemException -> {
                topicFeedLastId = null
                friendFeedLastId = null
                feedLastId = null
            }

            else -> _event.emit(Event.UnknownException)
        }
    }

    fun onSelectPictures(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val images = pickImageUseCase()
            _event.emit(Event.OnSelectPictures(images))
        }
    }


    sealed class Event {
        data class OnChangeFavoriteTopics(
            val topics: List<FeedTopic>,
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

companion object {
    private const val MINIMUM_FEED_SIZE = 2
    private const val MINIMUM_FEED_SIZE_DOUBLE = 4
    private const val DEFAULT_PAGE_SIZE = 20

}
}


