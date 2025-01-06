package com.apicta.myoscopealert.ui.screen.common

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.databinding.SignalChartBinding
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.terniary
import com.apicta.myoscopealert.utils.ThreadConnected.Companion.SAMPLE_RATE
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.IOException

@Composable
fun ProcessWavFileData(wavFilePath: String, ctx: Context, isZooming: Boolean = false, modifier: Modifier = Modifier) {
    val SHRT_MAX = 32767 // Untuk 16-bit PCM audio sample
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        AndroidViewBinding(
            factory = SignalChartBinding::inflate,
            modifier = modifier.background(color = terniary, shape = RoundedCornerShape(32.dp))
        ) {
            signalView.description?.isEnabled = false
            signalView.setTouchEnabled(true)
            signalView.setPinchZoom(true)
            signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
            signalView.setDrawGridBackground(false)

            // Customize X-axis and Y-axis properties
            val xAxis = signalView.xAxis
            xAxis?.setDrawGridLines(false)
            val yAxis = signalView.axisLeft
            yAxis?.setDrawGridLines(false)
            yAxis?.setAxisMaximum(0.1f)
            yAxis?.setAxisMinimum(-0.1f)
            val rightYAxis = signalView.axisRight
            rightYAxis?.setDrawLabels(false)
            rightYAxis?.setDrawGridLines(false)

            // Buffer size for WAV data reading
            val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT)
            val wavData = ByteArray(bufferSize)
            val dataPoints = ArrayList<Float>()

            // Read the WAV file in chunks
            val inputStream = FileInputStream(wavFilePath)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val dataInputStream = DataInputStream(bufferedInputStream)

            try {
                var bytesRead = dataInputStream.read(wavData, 0, bufferSize)
                while (bytesRead != -1) {
                    // Convert PCM data to amplitude values and normalize
                    for (i in 0 until bytesRead / 2) {
                        val sample = wavData[i * 2].toInt() and 0xFF or (wavData[i * 2 + 1].toInt() shl 8)
                        val amplitude = sample.toFloat() / SHRT_MAX.toFloat()
                        dataPoints.add(amplitude)
                    }
                    bytesRead = dataInputStream.read(wavData, 0, bufferSize)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                dataInputStream.close()
            }

            // Close the WAV file after reading
            inputStream.close()

            // Convert data points to chart-friendly format (Entry objects)
            val audioData = ArrayList<Entry>()
            for (i in dataPoints.indices) {
                val entry = Entry(i.toFloat(), dataPoints[i])
                audioData.add(entry)
            }

            // Set up the LineDataSet for the chart
            val dataSet = LineDataSet(audioData, "Heart Beat Wave (zoom for detail)")
            dataSet.color = R.color.green
            dataSet.setDrawCircles(false)
            val lineData = LineData(dataSet)
            signalView.data = lineData

            // Set the visible range of the X-axis (zooming behavior)
            val initialVisibleRange = 20000f
            signalView.setVisibleXRangeMaximum(initialVisibleRange)
            signalView.setVisibleXRangeMinimum(10f)
            signalView.moveViewToX(0f)

            // Refresh the chart
            signalView.invalidate()
        }
    }
}

//@Composable
//fun ProcessWavFileData(wavFilePath: String, ctx: Context, isZooming: Boolean = false, modifier: Modifier = Modifier) {
////    val SAMPLE_RATE = 8000
////    val SAMPLE_RATE = 24000 /*The speed and weight of audio*/
////    val SAMPLE_RATE = SAMPLE_RATE /*The speed and weight of audio*/
//    val SHRT_MAX = 24000 /*The range of values for a 16-bit PCM audio sample is from -32768 to 32767.*/
////    val SHRT_MAX = 255
//    Column(
//        modifier
//            .fillMaxWidth()
//            .padding(horizontal = 4.dp)
//    ) {
//
//        AndroidViewBinding(
//            factory = SignalChartBinding::inflate,
//            modifier = modifier.background(color = terniary, shape = RoundedCornerShape(32.dp))
//        ) {
//            signalView.description?.isEnabled = false
//            signalView.setTouchEnabled(true)
//            signalView.setPinchZoom(true)
//            signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
//            signalView.setDrawGridBackground(false)
//
//            // Customize X-axis properties if needed
//            val xAxis = signalView.xAxis
//            xAxis?.setDrawGridLines(false)
//
//            // Customize Y-axis properties if needed
//            val yAxis = signalView.axisLeft
//            yAxis?.setDrawGridLines(false)
//
//
////            yAxis?.setAxisMaximum(0.05f)
////            yAxis?.setAxisMinimum(-0.03f)
//
//            yAxis?.setAxisMaximum(0.030f)
//            yAxis?.setAxisMinimum(-0.020f)
////            if (isZooming) {
////                yAxis?.setAxisMaximum(0.008f)
////                yAxis?.setAxisMinimum(-0.005f)
////                Log.e("zoom aktif yaksis min max", "-0.03f, 0.05f --> -0.005f, 0.008f")
////
////            }
//            // Customize right Y-axis properties to hide labels
//            val rightYAxis = signalView.axisRight
//            rightYAxis?.setDrawLabels(false)
//            rightYAxis?.setDrawGridLines(false)
//            // **Memory Efficiency Improvement:** Read the WAV file in chunks instead of loading the entire file into memory at once.
//            val bufferSize = AudioRecord.getMinBufferSize(
//                SAMPLE_RATE,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_FLOAT
//            )
//            val wavData = ByteArray(bufferSize)
//            val dataPoints = ArrayList<Float>()
//
//            val inputStream = FileInputStream(wavFilePath)
//            val bufferedInputStream = BufferedInputStream(inputStream)
//            val dataInputStream = DataInputStream(bufferedInputStream)
//
//
//            try {
//                var bytesRead = dataInputStream.read(wavData, 0, bufferSize)
//
//                while (bytesRead != -1) {
//                    // Process the WAV data and convert it to data points suitable for the chart
//                    for (i in 0 until bytesRead / 2) { // Assuming 16-bit PCM
//                        val sample =
//                            wavData[i * 2].toInt() and 0xFF or (wavData[i * 2 + 1].toInt() shl 8)
//                        val amplitude = sample.toFloat() / SHRT_MAX.toFloat() // Normalize amplitude
//                        dataPoints.add(amplitude)
//                    }
//
//                    bytesRead = dataInputStream.read(wavData, 0, bufferSize)
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } finally {
//                dataInputStream.close()
//            }
//
//            // **Memory Leak Prevention:** Close the WAV file after processing it.
//            inputStream.close()
//
////
////            val minValue = intArray.minOrNull()?.toFloat() ?: 0f
////            val maxValue = intArray.maxOrNull()?.toFloat() ?: 1f
////
////            val normalizedDataPoints = ArrayList<Float>()
////            for (value in intArray) {
////                val normalizedValue = (value - minValue) / (maxValue - minValue)
////                normalizedDataPoints.add(normalizedValue)
////            }
//
//// Convert data points to Entry objects for the chart
//            val audioData = ArrayList<Entry>()
//            // Convert data points to Entry objects for the chart
//            for (i in dataPoints.indices) {
//                val entry = Entry(i.toFloat(), dataPoints[i])
//                audioData.add(entry)
//            }
//
//            // Create a LineDataSet with the audio data
//            val dataSet = LineDataSet(audioData, "Heart Beat Wave (zoom for detail)")
//            dataSet.color = R.color.green
//            dataSet.setDrawCircles(false)
//
//            // Create a LineData object and set the LineDataSet
//            val lineData = LineData(dataSet)
//
//            // Set the LineData object to the chart
//            signalView.data = lineData
//
//            // moveViewToX(...) also calls invalidate()
//
////            if (isZooming) {
////                // now modify viewport
////                signalView.setVisibleXRangeMaximum(50F) // allow 20 values to be displayed at once on the x-axis, not more
////                signalView.moveViewToX(100F) // set the left edge of the chart to x-index 10
////                Log.e("zoom aktif", "range data 800")
////            }
//
////            signalView.setVisibleXRangeMaximum(10000F) // allow 20 values to be displayed at once on the x-axis, not more
////            signalView.moveViewToX(100F) // set the left edge of the chart to x-index 10
////            val initialVisibleRange = dataPoints.size / 4f // Misalnya setengah data
//            val initialVisibleRange = 20000f // Ganti 100 dengan jumlah data yang diinginkan
//
//            signalView.setVisibleXRangeMaximum(initialVisibleRange) // Sesuaikan X-axis agar seluruh data terlihat
//            signalView.setVisibleXRangeMinimum(10f)
//            signalView.moveViewToX(0f)
////            yAxis.setAxisMinimum(dataPoints.minOrNull() ?: -0.010f)
////            yAxis.setAxisMaximum(dataPoints.maxOrNull() ?: 0.050f)
//
//            Log.e("processwav", "Refresh signalview")
//            // Refresh the chart
//            signalView.invalidate()
//
//        }
//
//
//    }
//}


@Composable
fun SetUpChart(ctx: Context) {
    AndroidViewBinding(
        SignalChartBinding::inflate,
        modifier = Modifier.border(
            width = 2.dp,
            color = cardsecondary,
            shape = RoundedCornerShape(16.dp)
        )


    ) {
        // Configure chart properties
        signalView.description?.isEnabled = false
        signalView.setTouchEnabled(true)
        signalView.setPinchZoom(true)
//        signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.transparent))
        signalView.setDrawGridBackground(false)

        // Customize X-axis properties if needed
        val xAxis = signalView.xAxis
        xAxis?.setDrawGridLines(false)

        // Customize Y-axis properties if needed
        val yAxis = signalView.axisLeft
        yAxis?.setDrawGridLines(false)
    }
}

@Composable
fun CardContent(isVerified: Boolean, note: String?,  modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(12.dp)
        ) {
//            Text(text = "Hello, ")
            Row {

                Text(
                    text = "Doctor's Verification",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins

                    )   
                )
                Spacer(modifier = modifier.width(4.dp))
                Box(
                    modifier = modifier
                        .background(
                            color = if (isVerified) Color.Green else Color.Red,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    if (isVerified) {
                        Text(
                            text = "Verified",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            color = Color.Black,
                            fontFamily = poppins
                        )
                    } else {
                        Text(
                            text = "Not verified",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            color = Color.White,
                            fontFamily = poppins

                        )
                    }
                }
            }
            Spacer(modifier = modifier.height(8.dp))
            if (expanded) {
                Text(
//                    text = "After examining the patient's heartbeat sound wave graph, I confirmed that there was no indication of Myocardial infarction. The patient's heart condition looks healthy and stable based on the graphic analysis that we have verified.",
                    text = if (note.isNullOrEmpty()) "Tidak ada catatan dari dokter" else note,
                    fontFamily = poppins,
                    fontSize = 13.sp
                )
            }
        }

        if (isVerified) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}
@Composable
fun ProcessWavFileData2(wavFilePath: String, ctx: Context, isZooming: Boolean = false) {
//    val SAMPLE_RATE = 8000
    val SAMPLE_RATE = 24000 /*The speed and weight of audio*/
    val SHRT_MAX = 32767 /*The range of values for a 16-bit PCM audio sample is from -32768 to 32767.*/
//    val SHRT_MAX = 255
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {

        AndroidViewBinding(
            factory = SignalChartBinding::inflate,
            modifier = Modifier.background(color = terniary, shape = RoundedCornerShape(32.dp))
        ) {
            signalView.description?.isEnabled = false
            signalView.setTouchEnabled(true)
            signalView.setPinchZoom(true)
            signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
            signalView.setDrawGridBackground(false)

            // Customize X-axis properties if needed
            val xAxis = signalView.xAxis
            xAxis?.setDrawLabels(false)
            xAxis?.setDrawGridLines(false)

            // Customize Y-axis properties if needed
            val yAxis = signalView.axisLeft
            yAxis?.setDrawLabels(false)
            yAxis?.setDrawGridLines(false)

            // Customize right Y-axis properties to hide labels
            val rightYAxis = signalView.axisRight
            rightYAxis?.setDrawLabels(false)
            rightYAxis?.setDrawGridLines(false)

            yAxis?.setAxisMaximum(0.006f)
            yAxis?.setAxisMinimum(-0.002f)
//            if (isZooming) {
//            }
            signalView.setVisibleXRangeMaximum(50F) // allow 20 values to be displayed at once on the x-axis, not more
            signalView.moveViewToX(25F) // set the left edge of the chart to x-index 10

            // **Memory Efficiency Improvement:** Read the WAV file in chunks instead of loading the entire file into memory at once.
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_FLOAT
            )
            val wavData = ByteArray(bufferSize)
            val dataPoints = ArrayList<Float>()

            val inputStream = FileInputStream(wavFilePath)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val dataInputStream = DataInputStream(bufferedInputStream)


            try {
                var bytesRead = dataInputStream.read(wavData, 0, bufferSize)

                while (bytesRead != -1) {
                    // Process the WAV data and convert it to data points suitable for the chart
                    for (i in 0 until bytesRead / 2) { // Assuming 16-bit PCM
                        val sample =
                            wavData[i * 2].toInt() and 0xFF or (wavData[i * 2 + 1].toInt() shl 8)
                        val amplitude = sample.toFloat() / SHRT_MAX.toFloat() // Normalize amplitude
                        dataPoints.add(amplitude)
                    }

                    bytesRead = dataInputStream.read(wavData, 0, bufferSize)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                dataInputStream.close()
            }

            // **Memory Leak Prevention:** Close the WAV file after processing it.
            inputStream.close()

//
//            val minValue = intArray.minOrNull()?.toFloat() ?: 0f
//            val maxValue = intArray.maxOrNull()?.toFloat() ?: 1f
//
//            val normalizedDataPoints = ArrayList<Float>()
//            for (value in intArray) {
//                val normalizedValue = (value - minValue) / (maxValue - minValue)
//                normalizedDataPoints.add(normalizedValue)
//            }

// Convert data points to Entry objects for the chart
            val audioData = ArrayList<Entry>()
            // Convert data points to Entry objects for the chart
            for (i in dataPoints.indices) {
                val entry = Entry(i.toFloat(), dataPoints[i])
                audioData.add(entry)
            }

            // Create a LineDataSet with the audio data
            val dataSet = LineDataSet(audioData, "Heart Beat Wave (zoom for detail)")
            dataSet.color = R.color.green
            dataSet.setDrawCircles(false)

            // Create a LineData object and set the LineDataSet
            val lineData = LineData(dataSet)

            // Set the LineData object to the chart
            signalView.data = lineData

            // moveViewToX(...) also calls invalidate()


            Log.e("processwav", "Refresh signalview")
            // Refresh the chart
            signalView.invalidate()

        }


    }
}

@Composable
fun ProcessWavFileData3(wavFilePath: String, ctx: Context, isZooming: Boolean = false) {
//    val SAMPLE_RATE = 8000
    val SAMPLE_RATE = 24000 /*The speed and weight of audio*/
    val SHRT_MAX = 32767 /*The range of values for a 16-bit PCM audio sample is from -32768 to 32767.*/
//    val SHRT_MAX = 255
    Column(
        Modifier
            .fillMaxWidth()
    ) {

        AndroidViewBinding(
            factory = SignalChartBinding::inflate,
            modifier = Modifier.background(color = terniary, shape = RoundedCornerShape(32.dp))
        ) {
            signalView.description?.isEnabled = false
            signalView.setTouchEnabled(true)
            signalView.setPinchZoom(true)
            signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
            signalView.setDrawGridBackground(false)

            // Customize X-axis properties if needed
            val xAxis = signalView.xAxis
            xAxis?.setDrawLabels(false)
            xAxis?.setDrawGridLines(false)

            // Customize Y-axis properties if needed
            val yAxis = signalView.axisLeft
            yAxis?.setDrawLabels(false)
            yAxis?.setDrawGridLines(false)

            // Customize right Y-axis properties to hide labels
            val rightYAxis = signalView.axisRight
            rightYAxis?.setDrawLabels(false)
            rightYAxis?.setDrawGridLines(false)

            yAxis?.setAxisMaximum(0.009f)
            yAxis?.setAxisMinimum(-0.006f)
//            if (isZooming) {
//            }
            signalView.setVisibleXRangeMaximum(50F) // allow 20 values to be displayed at once on the x-axis, not more
            signalView.moveViewToX(25F) // set the left edge of the chart to x-index 10

            // **Memory Efficiency Improvement:** Read the WAV file in chunks instead of loading the entire file into memory at once.
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_FLOAT
            )
            val wavData = ByteArray(bufferSize)
            val dataPoints = ArrayList<Float>()

            val inputStream = FileInputStream(wavFilePath)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val dataInputStream = DataInputStream(bufferedInputStream)


            try {
                var bytesRead = dataInputStream.read(wavData, 0, bufferSize)

                while (bytesRead != -1) {
                    // Process the WAV data and convert it to data points suitable for the chart
                    for (i in 0 until bytesRead / 2) { // Assuming 16-bit PCM
                        val sample =
                            wavData[i * 2].toInt() and 0xFF or (wavData[i * 2 + 1].toInt() shl 8)
                        val amplitude = sample.toFloat() / SHRT_MAX.toFloat() // Normalize amplitude
                        dataPoints.add(amplitude)
                    }

                    bytesRead = dataInputStream.read(wavData, 0, bufferSize)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                dataInputStream.close()
            }

            // **Memory Leak Prevention:** Close the WAV file after processing it.
            inputStream.close()

//
//            val minValue = intArray.minOrNull()?.toFloat() ?: 0f
//            val maxValue = intArray.maxOrNull()?.toFloat() ?: 1f
//
//            val normalizedDataPoints = ArrayList<Float>()
//            for (value in intArray) {
//                val normalizedValue = (value - minValue) / (maxValue - minValue)
//                normalizedDataPoints.add(normalizedValue)
//            }

// Convert data points to Entry objects for the chart
            val audioData = ArrayList<Entry>()
            // Convert data points to Entry objects for the chart
            for (i in dataPoints.indices) {
                val entry = Entry(i.toFloat(), dataPoints[i])
                audioData.add(entry)
            }

            // Create a LineDataSet with the audio data
            val dataSet = LineDataSet(audioData, "Heart Beat Wave (zoom for detail)")
            dataSet.color = R.color.green
            dataSet.setDrawCircles(false)

            // Create a LineData object and set the LineDataSet
            val lineData = LineData(dataSet)

            // Set the LineData object to the chart
            signalView.data = lineData

            // now modify viewport
            signalView.setVisibleXRangeMaximum(10000F) // allow 20 values to be displayed at once on the x-axis, not more
            signalView.moveViewToX(100F) // set the left edge of the chart to x-index 10
            // moveViewToX(...) also calls invalidate()

            Log.e("processwav", "Refresh signalview")
            // Refresh the chart
            signalView.invalidate()

        }


    }
}