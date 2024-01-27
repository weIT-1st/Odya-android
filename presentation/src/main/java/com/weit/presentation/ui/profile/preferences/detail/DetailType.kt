package com.weit.presentation.ui.profile.preferences.detail

enum class DetailType(val type: Int) {
    PRIVACY_POLICY(0),
    TERMS_OF_USE(1),
    OPEN_SOURCE(2),
    ;

    companion object {
        fun fromPosition(position: Int): String {
            return values().first { it.type == position }.toString()
        }
    }
}
