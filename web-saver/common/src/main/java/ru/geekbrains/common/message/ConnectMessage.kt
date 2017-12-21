package ru.geekbrains.common.message

import ru.geekbrains.common.dto.ConnectOperation
import ru.geekbrains.common.dto.ConnectStatus
import java.util.*

// сообщения, касающиеся операций соединения с сервером
class ConnectOperationMessage(from: Address, to: Address, val connectOperation: ConnectOperation, val additionalObject: Any?)
    : Message(from, to, ConnectOperationMessage::class.java)

class ConnectAnswerMessage(from: Address, to: Address, val toMessage: UUID, val connectStatus: ConnectStatus)
    : Message(from, to, ConnectAnswerMessage::class.java)
