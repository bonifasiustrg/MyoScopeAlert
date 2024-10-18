package com.apicta.myoscopealert.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

//class HeartbeatPredictor(private val context: Context) {
//    private lateinit var interpreter: Interpreter
//    private val scalerMean = floatArrayOf(/* Isi dengan mean dari server */)
//    private val scalerStd = floatArrayOf(/* Isi dengan std dari server */)
//
//    init {
//        val model = loadModelFile()
//        interpreter = Interpreter(model)
//    }
//
//    private fun loadModelFile(): MappedByteBuffer {
//        val fileDescriptor = context.assets.openFd("model.tflite")
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    fun predict(features: FloatArray): Float {
//        // Skalasi fitur menggunakan mean dan std yang disimpan
//        val inputBuffer = FloatArray(features.size)
//        for (i in features.indices) {
//            inputBuffer[i] = if (scalerStd[i] != 0f) (features[i] - scalerMean[i]) / scalerStd[i] else 0f
//        }
//
//        val inputShape = interpreter.getInputTensor(0).shape()
//        val outputShape = interpreter.getOutputTensor(0).shape()
//
//        // Pastikan ukuran inputBuffer sesuai dengan inputShape
//        val paddedInput = FloatArray(inputShape[1])
//        System.arraycopy(inputBuffer, 0, paddedInput, 0, minOf(inputBuffer.size, paddedInput.size))
//
//        val outputBuffer = Array(1) { FloatArray(outputShape[1]) }
//
//        interpreter.run(arrayOf(paddedInput), outputBuffer)
//
//        return outputBuffer[0][0]
//    }
//
//    fun close() {
//        interpreter.close()
//    }
//}

class HeartbeatPredictor(private val context: Context) {
    private lateinit var interpreter: Interpreter

    init {
        val model = loadModelFile()
        interpreter = Interpreter(model)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("dnn_model_fold_2.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(features: FloatArray): Float {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()

        val inputBuffer = FloatArray(inputShape[1])
        System.arraycopy(features, 0, inputBuffer, 0, features.size)

        val outputBuffer = Array(1) { FloatArray(outputShape[1]) }

        interpreter.run(arrayOf(inputBuffer), outputBuffer)

        return outputBuffer[0][0]
    }

    fun close() {
        interpreter.close()
    }
}