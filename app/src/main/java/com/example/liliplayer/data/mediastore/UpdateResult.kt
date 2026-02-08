package com.example.liliplayer.data.mediastore

import android.content.IntentSender

sealed class UpdateResult {
    data object Success : UpdateResult()
    data class RequiresPermission(val intentSender: IntentSender) : UpdateResult()
    data class Error(val message: String) : UpdateResult()
}
