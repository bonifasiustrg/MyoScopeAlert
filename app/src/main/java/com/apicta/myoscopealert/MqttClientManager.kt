package com.apicta.myoscopealert

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import java.nio.ByteBuffer
import java.util.Optional
import java.util.UUID

class MqttClientManager {
    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier(UUID.randomUUID().toString())
        .serverHost("broker.hivemq.com")
        .serverPort(1883)
        .buildAsync()

    init {
        connect()
    }

    private fun connect() {
        client.connectWith()
            .simpleAuth()
            .username("iwahyu")
            .password("DE@re123456".toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.e("MqttClientManager", "Connection error: ${throwable.message}")
                } else {
                    if (connAck != null && connAck.isSessionPresent) {
                        Log.e("MqttClientManager", "Connected successfully with existing session")
                        Log.e("MqttClientManager", "Received message: ${connAck.toString()}")
                    } else {
                        Log.e("mqtt", "Connected successfully with a new session")
                        subscribe()
                        publish()
                    }
                }
            }
    }

    private fun subscribe() {
        client.subscribeWith()
            .topicFilter("fromAPI")
            .callback { publish: Mqtt3Publish? ->
                val message = publish?.payload
                Log.e("MqttClientManager", "Received message: ${processMqttMessage(message)}")
            }
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.e("MqttClientManager", "Failure to subscribe")
                } else {
                    Log.e("MqttClientManager", "Successful subscription")
                }
            }
    }

    val msg = "hello world from android"
    private fun publish() {
        client.publishWith()
            .topic("fromMobile")
            .payload(msg.toByteArray())
            .send()
            .whenComplete { publish: Mqtt3Publish?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.e("MqttClientManager", "Failure to publish")
                } else {
                    Log.e("MqttClientManager", "Successful publish \"$msg\"\n$publish")
                }
            }
    }

    private fun processMqttMessage(optionalBuffer: Optional<ByteBuffer>?): String {
        return if (optionalBuffer != null && optionalBuffer.isPresent) {
            val buffer = optionalBuffer.get()
            val payloadBytes = ByteArray(buffer.remaining())
            buffer.get(payloadBytes)

            val payloadString = payloadBytes.toString(Charsets.UTF_8)
            "Received message payload: $payloadString"
        } else {
            "Received empty message"
        }
    }
}