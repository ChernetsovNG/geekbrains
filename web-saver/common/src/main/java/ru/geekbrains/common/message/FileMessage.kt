package ru.geekbrains.common.message

import ru.geekbrains.common.dto.FileObjectToOperate
import ru.geekbrains.common.dto.FileOperation
import ru.geekbrains.common.dto.FileStatus
import java.util.*

// классы сообщений для манипуляций с файлами на сервере
class FileMessage(from: Address, to: Address, val fileObjectToOperate: FileObjectToOperate, val fileOperation: FileOperation, val additionalObject: Any?)
    : Message(from, to, FileMessage::class.java)

class FileAnswer(from: Address, to: Address, val toMessage: UUID, val fileStatus: FileStatus, val additionalMessage: String?, val additionalObject: Any?)
    : Message(from, to, FileAnswer::class.java)
