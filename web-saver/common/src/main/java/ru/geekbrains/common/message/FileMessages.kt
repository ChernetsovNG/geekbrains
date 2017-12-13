package ru.geekbrains.common.message

import ru.geekbrains.common.dto.AnswerStatus
import ru.geekbrains.common.dto.CreationStatus

// классы сообщений для манипуляций с файлами на сервере
// создать на сервере папку для пользователя
class CreateFolderDemand(from: Address, to: Address)
    : Message(from, to, CreateFolderDemand::class.java)

class CreateFolderAnswer(from: Address, to: Address, val creationStatus: CreationStatus, val additionalMessage: String)
    : Message(from, to, CreateFolderAnswer::class.java)

// создать на сервере новый файл
class CreateNewFileDemand(from: Address, to: Address, val fileName: String, val filePayload: ByteArray)
    : Message(from, to, CreateNewFileDemand::class.java)

class CreateNewFileAnswer(from: Address, to: Address, val creationStatus: CreationStatus, val additionalMessage: String)
    : Message(from, to, CreateNewFileAnswer::class.java)

// удалить файл
class DeleteFileDemand(from: Address, to: Address, val fileName: String)
    : Message(from, to, DeleteFileDemand::class.java)

class DeleteFileAnswer(from: Address, to: Address, val answerStatus: AnswerStatus, val additionalMessage: String)
    : Message(from, to, DeleteFileAnswer::class.java)

// получить список файлов
class GetFileNameList(from: Address, to: Address)
    : Message(from, to, GetFileNameList::class.java)

class FileNameList(from: Address, to: Address, val fileNamesList: List<String>, val commonAnswerStatus: AnswerStatus, val additionalMessage: String)
    : Message(from, to, FileNameList::class.java)

// получить содержимое файла
class GetFilePayload(from: Address, to: Address, val fileName: String)
    : Message(from, to, GetFilePayload::class.java)

class FilePayloadAnswer(from: Address, to: Address, val commonAnswerStatus: AnswerStatus, val filePayload: ByteArray, val additionalMessage: String)
    : Message(from, to, FilePayloadAnswer::class.java)