package com.weit.presentation.model

enum class Visibility(val position: Int) {
    PUBLIC(0),
    FRIEND_ONLY(1),
    PRIVATE(2);

    companion object {
        fun fromPosition(position: Int): Visibility {
            return values().first { it.position == position }
        }
        fun getPosition(visibility: Visibility): Int {
            return visibility.position
        }
    }
}