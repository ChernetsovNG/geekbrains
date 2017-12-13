package ru.geekbrains.common.message

import ru.geekbrains.common.dto.AuthStatus

// сообщения, касающиеся соединения с сервером

class HandshakeDemandMessage(from: Address, to: Address)
    : Message(from, to, HandshakeDemandMessage::class.java)

class HandshakeAnswerMessage(from: Address, to: Address)
    : Message(from, to, HandshakeAnswerMessage::class.java)

class AuthDemandMessage(from: Address, to: Address, val username: String, val password: String)
    : Message(from, to, AuthDemandMessage::class.java)

class AuthAnswerMessage(from: Address, to: Address, val authStatus: AuthStatus, val message: String)
    : Message(from, to, AuthAnswerMessage::class.java)

class DisconnectClientMessage(from: Address, to: Address)
    : Message(from, to, DisconnectClientMessage::class.java)