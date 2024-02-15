package com.weit.presentation.ui.profile.preferences.update

import com.google.firebase.auth.ktx.actionCodeSettings

object FirebaseCommunication {

    val actionCodeSettings = actionCodeSettings {
        url = URL
        handleCodeInApp = true
        setAndroidPackageName(
            PACk_NAME,
            true,
            "12"
        )
    }

    private const val URL = "https://odya.page.link/Go1D"
    private const val PACk_NAME = "com.weit.odya"
}
