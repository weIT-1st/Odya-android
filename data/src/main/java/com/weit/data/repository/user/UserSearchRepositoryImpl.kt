package com.weit.data.repository.user

import com.weit.data.model.coordinate.Coordinate
import com.weit.data.model.user.search.UserSearch
import com.weit.data.model.user.search.UserSearchProfile
import com.weit.data.model.user.search.UserSearchProfileColor
import com.weit.data.source.CoordinateDataSource
import com.weit.data.source.UserSearchDataSource
import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.model.user.search.UserProfileColorInfo
import com.weit.domain.model.user.search.UserProfileInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import com.weit.domain.repository.user.UserSearchRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSearchRepositoryImpl @Inject constructor(
    private val dataSource: UserSearchDataSource,
) : UserSearchRepository {

    private fun UserSearchInfo.toUserSearch(): UserSearch =
        UserSearch(
            userId = userId,
            nickname = nickname,
            profile = UserSearchProfile(
                profileUrl = profile.url,
                profileColor = UserSearchProfileColor(
                    colorHex = profile.color.colorHex,
                    red = profile.color.red,
                    blue = profile.color.blue,
                    green = profile.color.green
                )
            )
        )

    override suspend fun insertUser(user: UserSearchInfo) {
        dataSource.insertUser(user.toUserSearch())
    }

    override suspend fun deleteUser(userSearchId: Long) {
        dataSource.deleteUser(userSearchId)
    }

    override suspend fun getUser(): List<UserSearchInfo> {
        val result = dataSource.getUsers()
        return result.map {
            UserSearchInfo(it.userId,it.nickname,
                UserProfileInfo(it.profile.profileUrl,
                    UserProfileColorInfo(it.profile.profileColor.colorHex,
                        it.profile.profileColor.red,
                        it.profile.profileColor.blue,
                        it.profile.profileColor.green)
                )
            )
        }
    }
}
