package com.weit.data.source

import android.Manifest
import android.os.Build
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.R

class PermissionDataSource {
    suspend fun getReadPermissionResult(): TedPermissionResult {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return TedPermission.create()
            .setDeniedMessage(R.string.read_permission_denied)
            .setPermissions(permission)
            .check()
    }
}
