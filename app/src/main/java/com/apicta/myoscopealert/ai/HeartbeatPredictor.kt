package com.apicta.myoscopealert.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

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