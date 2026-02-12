package com.example.liliplayer.ui.screens.downloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.BeelodyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeelodyDownloaderViewModel @Inject constructor(
    private val repository: BeelodyRepository
) : ViewModel() {

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    private val _downloadState = MutableStateFlow<BeelodyRepository.DownloadState>(
        BeelodyRepository.DownloadState.Idle
    )
    val downloadState: StateFlow<BeelodyRepository.DownloadState> = _downloadState.asStateFlow()

    val isDownloading: Boolean
        get() {
            val state = _downloadState.value
            return state !is BeelodyRepository.DownloadState.Idle &&
                    state !is BeelodyRepository.DownloadState.Success &&
                    state !is BeelodyRepository.DownloadState.Error
        }

    fun setUrl(url: String) {
        _url.value = url
    }

    fun download() {
        if (_url.value.isBlank()) return
        viewModelScope.launch {
            repository.download(_url.value) { state ->
                _downloadState.value = state
            }
        }
    }

    fun reset() {
        _downloadState.value = BeelodyRepository.DownloadState.Idle
        _url.value = ""
    }
}
