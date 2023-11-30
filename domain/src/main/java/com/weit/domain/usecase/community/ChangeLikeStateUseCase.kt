package com.weit.domain.usecase.community

import com.weit.domain.model.follow.FollowFollowingIdInfo
import javax.inject.Inject

//class ChangeLikeStateUseCase @Inject constructor(
//    private val registerCommunityLikeUseCase: RegisterCommunityLikeUseCase,
//    private val deleteCommunityLikeUseCase: DeleteCommunityLikeUseCase,
//) {
//    suspend operator fun invoke(communityId: Long, changeLikeState: Boolean): Result<Unit> {
//        return if (changeLikeState) {
//            registerCommunityLikeUseCase(communityId)
//        } else {
//            deleteCommunityLikeUseCase(communityId)
//        }
//    }
//}
