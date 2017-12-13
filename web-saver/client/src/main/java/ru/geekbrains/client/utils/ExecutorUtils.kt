package ru.geekbrains.client.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.geekbrains.common.channel.SocketClientChannel
import ru.geekbrains.common.message.*
import java.util.concurrent.TimeUnit

// объект для запуска задачи и ожидания её завершения
object ExecutorUtils {
    const val THREADS_NUMBER = 1
    const val PAUSE_MS = 250L
    const val WAIT_TIME_SEC = 20  // время ожидания ответа сервера (чтобы избежать бесконечного цикла)

    private val LOG: Logger = LoggerFactory.getLogger(ExecutorUtils::class.java)

    // Отправить сообщение и дождаться ответа от сервера
    fun sendMessageAndAwaitAnswer(clientChannel: SocketClientChannel, message: Message): Message? {
        val neededAnswerClass = getNeededAnswerClass(message)
        clientChannel.send(message)
        var startTime = System.nanoTime()*1.0
        var deltaTime = 0.0
        try {
            while (deltaTime < WAIT_TIME_SEC) {  // block and wait server answer
                val answer = clientChannel.take()
                if (answer.isClass(neededAnswerClass)) {
                    return answer
                } else {
                    val newTime = System.nanoTime()*1.0
                    deltaTime += (newTime - startTime)/1e9
                    startTime = newTime
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS)
                }
            }
        } catch (e: InterruptedException) {
            LOG.error(e.message)
        }
        return null
    }

    // по классу сообщения определяем ожидаемый класс ответа
    private fun getNeededAnswerClass(message: Message): Class<out Message> {
        if (message.isClass(CreateFolderDemand::class.java)) {
            return CreateFolderAnswer::class.java
        } else if (message.isClass(CreateNewFileDemand::class.java)) {
            return CreateNewFileAnswer::class.java
        } else if (message.isClass(DeleteFileDemand::class.java)) {
            return DeleteFileAnswer::class.java
        } else if (message.isClass(GetFileNameList::class.java)) {
            return FileNameList::class.java
        } else if (message.isClass(GetFilePayload::class.java)) {
            return FilePayloadAnswer::class.java
        } else {
            throw RuntimeException("Unknown message: " + message)
        }
    }
}