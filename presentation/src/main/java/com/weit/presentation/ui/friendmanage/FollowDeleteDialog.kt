package com.weit.presentation.ui.friendmanage

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weit.presentation.R

fun showDeleteFollowingDialog(
    context : Context,
    deleteAction: (Unit) -> Unit
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.friend_manage_following_delete_title))
        .setMessage(context.getString(R.string.friend_manage_following_delete_content))
        .setPositiveButton(context.getString(R.string.friend_manage_delete)) { dialog, which ->
            deleteAction(Unit)
        }
        .setNegativeButton(context.getString(R.string.friend_manage_cancel)) { dialog, which -> }
        .show()
}

fun showDeleteFollowerDialog(
    context : Context,
    deleteAction: (Unit) -> Unit
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.friend_manage_follower_delete_title))
        .setMessage(context.getString(R.string.friend_manage_follower_delete_content))
        .setPositiveButton(context.getString(R.string.friend_manage_delete)) { dialog, which ->
            deleteAction(Unit)
        }
        .setNegativeButton(context.getString(R.string.friend_manage_cancel)) { dialog, which -> }
        .show()
}