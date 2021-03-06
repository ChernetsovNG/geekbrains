package ru.geekbrains.client.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.geekbrains.common.channel.SocketClientChannel
import ru.geekbrains.common.message.FileAnswer
import ru.geekbrains.common.message.FileMessage
import ru.geekbrains.common.message.Message
import java.net.NetworkInterface
import java.net.SocketException
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

// объект для запуска задачи и ожидания её завершения
object ClientUtils {
    const val PAUSE_MS = 250L
    const val WAIT_TIME_SEC = 20  // время ожидания ответа сервера (чтобы избежать бесконечного цикла)

    private val LOG: Logger = LoggerFactory.getLogger(ClientUtils::class.java)

    // Отправить сообщение и дождаться ответа от сервера
    fun sendMessageAndAwaitAnswer(clientChannel: SocketClientChannel, message: Message): Message? {
        val neededAnswerClass = getNeededAnswerClass(message)
        clientChannel.send(message)
        var startTime = System.nanoTime() * 1.0
        var deltaTime = 0.0
        try {
            while (deltaTime < WAIT_TIME_SEC) {  // block and wait server answer
                val answer = clientChannel.take()
                if (answer.isClass(neededAnswerClass)) {
                    val fileAnswer = answer as FileAnswer
                    if (fileAnswer.toMessage == message.uuid) {  // если сообщение пришло в ответ на отправленное
                        return fileAnswer
                    }
                } else {
                    val newTime = System.nanoTime() * 1.0
                    deltaTime += (newTime - startTime) / 1e9
                    startTime = newTime
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS)
                }
            }
        } catch (e: InterruptedException) {
            LOG.error(e.message)
        }
        return null
    }

    // Находим список MAC-адресов данного хоста. Будем считать его уникальными именем клиента
    fun getMacAddress(): String {
        try {
            val macList = ArrayList<String>()

            val networks = NetworkInterface.getNetworkInterfaces()

            while (networks.hasMoreElements()) {
                val network = networks.nextElement()
                val mac = network.hardwareAddress

                if (mac != null) {
                    val sb = StringBuilder()
                    for (i in mac.indices) {
                        sb.append(String.format("%02X%s", mac[i], if (i < mac.size - 1) "-" else ""))
                    }
                    macList.add(sb.toString())
                }
            }

            return macList.stream().collect(Collectors.joining("|"))
        } catch (e: SocketException) {
            LOG.error(e.message)
        }

        return ""
    }

    // по классу сообщения определяем ожидаемый класс ответа
    private fun getNeededAnswerClass(message: Message): Class<out Message> {
        if (message.isClass(FileMessage::class.java)) {
            return FileAnswer::class.java
        } else {
            throw RuntimeException("Unknown message: " + message)
        }
    }
}