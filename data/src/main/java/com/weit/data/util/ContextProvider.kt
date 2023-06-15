package com.weit.data.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.util.NoSuchElementException

/**
 * 카카오 로그인 처럼 현재 Activity의 context가 절실하게 필요한 경우에 사용한다
 * 현재 올라와 있는 Activity의 Context를 제공한다.
 */
class ContextProvider : Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    fun provide(): Context {
        return currentActivity ?: throw NoSuchElementException()
    }
}
