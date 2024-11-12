package com.apicta.myoscopealert.ai

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.utils.logLargeArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeartbeatViewModel(application: Application) : AndroidViewModel(application) {
    private val predictor = HeartbeatPredictor(application)
    private val _prediction = MutableStateFlow<String?>(null)
    val prediction: StateFlow<String?> = _prediction.asStateFlow()
    private val featureExtractor = AudioFeatureExtractor()

    fun predictHeartbeatFromPath(wavFilePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val features = featureExtractor.extractFeatures(getApplication(), wavFilePath)
                Log.d("HeartbeatViewModel", "features size: ${features.size}")
//                Log.d("HeartbeatViewModel", "Extracted features: ${features.contentToString()}")
//                println("HeartbeatViewModel Extracted features: ${features.contentToString()}")
                logLargeArray("HeartbeatViewModel", features)


                var result = predictor.predict(features)
                Log.d("HeartbeatViewModel", "Prediction result1: $result")
                if (result in 0f..0.1f) result *= 10
                Log.d("HeartbeatViewModel", "Prediction result2: $result")
                _prediction.value = if (result in 0.6f..1f) "MI Detected" else "Normal"
                Log.d("HeartbeatViewModel", "Final prediction: ${_prediction.value}")
            } catch (e: Exception) {
                Log.e("HeartbeatViewModel", "Failed to predict heartbeat from WAV path", e)
                _prediction.value = "Error"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        predictor.close()
    }
}
