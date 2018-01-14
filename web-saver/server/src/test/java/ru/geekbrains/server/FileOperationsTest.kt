package ru.geekbrains.server

import kotlinx.coroutines.experimental.async
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import ru.geekbrains.common.CommonData
import ru.geekbrains.common.CommonData.*
import ru.geekbrains.common.channel.SocketClientManagedChannel
import ru.geekbrains.common.dto.*
import ru.geekbrains.common.message.*
import ru.geekbrains.common.utils.FileUtils
import ru.geekbrains.common.utils.FileUtils.*
import ru.geekbrains.server.db.Database
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class FileOperationsTest {
    companion object {
        lateinit var server: Server
        val clientNumber = AtomicInteger(0)
        val REGISTER_NAME = "test_client"
        val CLIENT_FOLDER = CLIENTS_FOLDERS_PATH + FILE_SEPARATOR + REGISTER_NAME
        val ROOT_FOLDER = ""

        @BeforeAll
        @JvmStatic
        fun startServer() {
            // запускаем в отдельном потоке, чтобы не было блокировок
            async {
                server = Server()
                server.start("src/test/resources/test-data.db")
            }
            TimeUnit.MILLISECONDS.sleep(1000)  // wait server initialization
            if (isFolderExists(CLIENT_FOLDER)) {
                deleteDirectory(CLIENT_FOLDER)
            }
        }

        @AfterAll
        @JvmStatic
        fun stopServer() {
            Database.clearDatabase()
            server.stop()
        }
    }

    lateinit var client: SocketClientManagedChannel
    var clientAddress = Address("")

    // перед каждым тестом стартуем нового клиента и подключаемся к серверу
    @BeforeEach
    fun startNewClientAndHandshake() {
        Database.clearDatabase()  // очищаем базу данных

        client = SocketClientManagedChannel("localhost", CommonData.SERVER_PORT)
        client.init()

        // перед каждым тестом делаем handshake нового клиента на сервере
        clientAddress = Address("test-client-" + clientNumber.addAndGet(1))

        val handshakeDemandMessage = ConnectOperationMessage(clientAddress, SERVER_ADDRESS, ConnectOperation.HANDSHAKE, null)
        client.send(handshakeDemandMessage)

        val handshakeAnswerMessage: ConnectAnswerMessage = client.take() as ConnectAnswerMessage

        assertEquals(handshakeDemandMessage.uuid, handshakeAnswerMessage.toMessage)     // проверяем, что ответ на наше сообщение
        assertEquals(ConnectStatus.HANDSHAKE_OK, handshakeAnswerMessage.connectStatus)

        // регистрируем нового клиента (добавляя его в базу данных)
        val registerNewClientMessage = ConnectOperationMessage(clientAddress, SERVER_ADDRESS, ConnectOperation.REGISTER, UserDTO(REGISTER_NAME, "qwerty"))
        client.send(registerNewClientMessage)

        val registerAnswerMessage: ConnectAnswerMessage = client.take() as ConnectAnswerMessage

        assertEquals(registerNewClientMessage.uuid, registerAnswerMessage.toMessage)
        assertEquals(ConnectStatus.REGISTER_OK, registerAnswerMessage.connectStatus)

        // аутентифицируем клиента на сервере
        val authDemandMessage = ConnectOperationMessage(clientAddress, SERVER_ADDRESS, ConnectOperation.AUTH, UserDTO(ConnectionToServerTest.REGISTER_NAME, "qwerty"))
        client.send(authDemandMessage)

        val authAnswerMessage: ConnectAnswerMessage = client.take() as ConnectAnswerMessage

        assertEquals(authDemandMessage.uuid, authAnswerMessage.toMessage)     // проверяем, что ответ на наше сообщение
        assertEquals(ConnectStatus.AUTH_OK, authAnswerMessage.connectStatus)

        // создаём для клиента новую папку
        val createFolderMessage = FileMessage(clientAddress, SERVER_ADDRESS, FileObjectToOperate.FOLDER, FileOperation.CREATE, null, false)
        client.send(createFolderMessage)

        val createFolderAnswer: FileAnswer = client.take() as FileAnswer

        assertEquals(createFolderMessage.uuid, createFolderAnswer.toMessage)
        assertEquals(FileStatus.OK, createFolderAnswer.fileStatus)
    }

    @AfterEach
    fun afterTest() {
        // после каждого теста удаляем папку клиента
        FileUtils.deleteDirectory(CLIENT_FOLDER)
    }

    @Test
    fun getFileListTest() {

    }

    @Test
    fun createNewFolderTest() {

    }

    @Test
    fun createNewFileTest() {
        val activeFolder = ROOT_FOLDER;

        val fileName = "test-file.txt"
        val fileContent = byteArrayOf(1, 2, 3, 5, 7, 11, 13, 17, 19, 23)
        val fileDTO = FileDTO(activeFolder, fileName, fileContent)

        val createNewFileMessage = FileMessage(clientAddress, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.CREATE, fileDTO, false)
        client.send(createNewFileMessage)

        val createNewFileAnswer: FileAnswer = client.take() as FileAnswer

        assertEquals(createNewFileMessage.uuid, createNewFileAnswer.toMessage)
        assertEquals(FileStatus.OK, createNewFileAnswer.fileStatus)

        // проверяем, что файл действительно создан
        assertTrue(isFileExists(CLIENT_FOLDER, fileName))
        // проверяем содержимое файла
        val filePath = Paths.get(CLIENT_FOLDER + FILE_SEPARATOR + fileName)
        assertArrayEquals(fileContent, Files.readAllBytes(filePath))
    }

    @Test
    fun deleteFilesTest() {

    }

    @Test
    fun renameFileTest() {

    }

    @Test
    fun downloadFileTest() {

    }
}
