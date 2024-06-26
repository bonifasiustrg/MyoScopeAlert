package com.apicta.myoscopealert.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.apicta.myoscopealert.data.repository.AudioRepository
import com.apicta.myoscopealert.models.Audio
import com.apicta.myoscopealert.service.JetAudioServiceHandler
import com.apicta.myoscopealert.service.JetAudioState
import com.apicta.myoscopealert.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val audioDummy = Audio(
    "".toUri(), "", 0L, "", "", 0, "",1679395200000L,
)

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: JetAudioServiceHandler,
    private val repository: AudioRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf(audioDummy) }
//    var audioList by savedStateHandle.saveable { mutableStateOf(listOf<Audio>()) }

    private val _audioListState = MutableStateFlow<List<Audio>>(emptyList())
    val audioList: StateFlow<List<Audio>> get() = _audioListState
    val audioListSearch: StateFlow<List<Audio>> get() = _audioListState

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        loadAudioData()
    }

    init {
        viewModelScope.launch {
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    JetAudioState.Initial -> _uiState.value = UIState.Initial
                    is JetAudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is JetAudioState.Playing -> isPlaying = mediaState.isPlaying
                    is JetAudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is JetAudioState.CurrentPlaying -> {
                        currentSelectedAudio = audioList.value[mediaState.mediaItemIndex]
                    }

                    is JetAudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }

                    else -> {}
                }
            }


        }
    }

    fun loadAudioData() {
        viewModelScope.launch {
            val audio = repository.getAudioData()
//            audioList = audio
            _audioListState.value = audio
            Log.e("AudioViewModel list vm", audio.toString())
            Log.d("AudioViewModel", "Number of items in audio list: ${audioList.value.size}")
            setMediaItems()
        }
    }

    private fun setMediaItems() {
        audioList.value.map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also {
            audioServiceHandler.setMediaItemList(it)
        }
    }

    // Fungsi untuk melakukan filtering berdasarkan query
    fun filterAudioList(query: String):List<Audio> {
        val filteredList = _audioListState.value.filter { audio ->
            audio.displayName!!.contains(query, ignoreCase = true)
            // Sesuaikan dengan kondisi atau properti audio yang ingin Anda gunakan untuk filtering
        }
        return filteredList
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0f
        progressString = formatDuration(currentProgress)
    }

    fun onUiEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            is UIEvents.PlayPause -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.PlayPause
                )
            }

            is UIEvents.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((duration * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.SelectedAudioChange -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SelectedAudioChange,
                    selectedAudioIndex = uiEvents.index
                )
            }

            is UIEvents.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                progress = uiEvents.newProgress
            }
        }
    }


    fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }


}


sealed class UIEvents {
    object PlayPause : UIEvents()
    data class SelectedAudioChange(val index: Int) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    object SeekToNext : UIEvents()
    object Backward : UIEvents()
    object Forward : UIEvents()
    data class UpdateProgress(val newProgress: Float) : UIEvents()
}

sealed class UIState {
    object Initial : UIState()
    object Ready : UIState()
}







