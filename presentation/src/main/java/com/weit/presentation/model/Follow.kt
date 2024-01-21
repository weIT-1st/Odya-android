package com.weit.presentation.model

enum class Follow(val position: Int) {
    FOLLOWER(0),
    FOLLOWING(1);
    companion object {
        fun fromPosition(position: Int): Follow {
            return values().first { it.position == position }
        }
        fun getPosition(follow: Follow): Int {
            return follow.position
        }
    }
}