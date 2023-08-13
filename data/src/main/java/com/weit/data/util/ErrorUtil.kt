package com.weit.data.util

import org.json.JSONObject
import retrofit2.Response

fun Response<*>.getErrorMessage(): String {
    val message = errorBody()?.string().toString()
    return JSONObject(message)["code"].toString() + " " + JSONObject(message)["errorMessage"].toString()
}
