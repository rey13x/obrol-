package com.bagas.obrol.network.service

import com.bagas.obrol.domain.model.device.Device
import com.bagas.obrol.network.model.call.NetworkCallEnd
import com.bagas.obrol.network.model.call.NetworkCallRequest
import com.bagas.obrol.network.model.call.NetworkCallResponse
import com.bagas.obrol.network.model.keepalive.NetworkKeepalive
import com.bagas.obrol.network.model.message.NetworkAudioMessage
import com.bagas.obrol.network.model.message.NetworkFileMessage
import com.bagas.obrol.network.model.message.NetworkMessageAck
import com.bagas.obrol.network.model.message.NetworkTextMessage
import com.bagas.obrol.network.model.profile.NetworkProfileRequest
import com.bagas.obrol.network.model.profile.NetworkProfileResponse
import com.bagas.obrol.network.model.typing.NetworkTypingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientService {
    suspend fun sendKeepalive(ipAddress: String, ownDevice: Device, networkKeepalive: NetworkKeepalive) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect((InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_KEEPALIVE)))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkKeepalive).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendProfileRequest(ipAddress: String, ownDevice: Device, networkProfileRequest: NetworkProfileRequest) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect((InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_PROFILE_REQUEST)))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkProfileRequest).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendProfileResponse(ipAddress: String, ownDevice: Device, networkProfileRequest: NetworkProfileResponse) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect((InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_PROFILE_RESPONSE)))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkProfileRequest).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendTextMessage(ipAddress: String, ownDevice: Device, networkTextMessage: NetworkTextMessage) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect((InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_TEXT_MESSAGE)))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkTextMessage).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendFileMessage(ipAddress: String, ownDevice: Device, networkFileMessage: NetworkFileMessage) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_FILE_MESSAGE))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkFileMessage).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendAudioMessage(ipAddress: String, ownDevice: Device, networkAudioMessage: NetworkAudioMessage) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_AUDIO_MESSAGE))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkAudioMessage).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendMessageReceivedAck(ipAddress: String, ownDevice: Device, networkMessageAck: NetworkMessageAck) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_MESSAGE_RECEIVED_ACK))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkMessageAck).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendMessageReadAck(ipAddress: String, ownDevice: Device, networkMessageAck: NetworkMessageAck) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_MESSAGE_READ_ACK))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkMessageAck).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendCallRequest(ipAddress: String, ownDevice: Device, networkCallRequest: NetworkCallRequest) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_CALL_REQUEST))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkCallRequest).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendCallResponse(ipAddress: String, ownDevice: Device, networkCallResponse: NetworkCallResponse) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_CALL_RESPONSE))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkCallResponse).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendCallEnd(ipAddress: String, ownDevice: Device, networkCallEnd: NetworkCallEnd) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_CALL_END))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkCallEnd).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendCallFragment(ipAddress: String, ownDevice: Device, callFragment: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_CALL_FRAGMENT))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(callFragment)
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun sendTypingStatus(ipAddress: String, ownDevice: Device, networkTypingStatus: NetworkTypingStatus) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.bind(null)
                socket.connect(InetSocketAddress(InetAddress.getByName(ipAddress), ServerService.PORT_TYPING_STATUS))

                ownDevice.ipAddress = socket.localAddress.hostAddress?.toString()

                val outputStream = socket.getOutputStream()
                outputStream.write(Json.encodeToString(networkTypingStatus).encodeToByteArray())
                outputStream.close()
            } catch (_: Exception) {

            }
        }
    }
}