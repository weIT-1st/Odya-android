package com.weit.data.repository.example

import android.Manifest
import android.os.Build
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import com.weit.data.R
import com.weit.data.source.ImageDataSource
import com.weit.domain.model.exception.RequestDeniedException
import com.weit.domain.repository.example.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val dataSource: ImageDataSource,
) : ImageRepository {
    override suspend fun getImages(path: String?): Result<List<String>> {
        val result = getReadPermissionResult()
        return if (result.isGranted) {
            Result.success(dataSource.getImages(path))
        } else {
            Result.failure(RequestDeniedException(result.deniedPermissions.first()))
        }
    }

    private suspend fun getReadPermissionResult(): TedPermissionResult {
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
