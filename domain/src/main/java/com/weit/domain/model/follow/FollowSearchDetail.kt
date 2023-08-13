package com.weit.domain.model.follow

interface FollowSearchDetail {
    val hasNext: Boolean
    val content: List<FollowUserContent>
}
