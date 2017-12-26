package ru.geekbrains.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.dto.ConnectOperation;
import ru.geekbrains.common.dto.ConnectStatus;
import ru.geekbrains.common.dto.UserDTO;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.ConnectAnswerMessage;
import ru.geekbrains.common.message.ConnectOperationMessage;
import ru.geekbrains.server.db.Database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.dto.ConnectStatus.*;

public class ConnectDemandHandlerImpl implements ConnectDemandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectDemandHandler.class);

    private final Map<MessageChannel, Address> connectionMap;  // карта вида <Канал для сообщений -> соответствующий ему адрес>
    private final Map<MessageChannel, String> authMap;         // карта вида <Канал -> имя авторизованного пользователя>

    public ConnectDemandHandlerImpl() {
        connectionMap = new HashMap<>();
        authMap = new HashMap<>();
    }

    @Override
    public void handleConnectDemand(Address clientAddress, MessageChannel clientChannel, ConnectOperationMessage message) {
        ConnectOperation connectOperation = message.getConnectOperation();
        if (clientAddress == null) {
            handleConnectionDemandNewClient(clientChannel, connectOperation, message);
        } else {
            handleConnectionDemandExistsClient(clientChannel, connectOperation, clientAddress, message);
        }
    }

    @Override
    public Map<MessageChannel, Address> getClientAddressMap() {
        return connectionMap;
    }

    private void handleConnectionDemandNewClient(MessageChannel clientChannel, ConnectOperation connectOperation, ConnectOperationMessage connectOperationMessage) {
        if (connectOperation.equals(ConnectOperation.HANDSHAKE)) {
            Address clientAddress = connectOperationMessage.getFrom();
            LOG.info("Получен запрос на установление соединения от: " + clientAddress + ", " + connectOperationMessage);
            connectionMap.put(clientChannel, clientAddress);
            ConnectAnswerMessage handshakeAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, connectOperationMessage.getUuid(), HANDSHAKE_OK, null);
            clientChannel.send(handshakeAnswerMessage);
            LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
        } else {
            LOG.info("Получен не HANDSHAKE запрос от нового клиента. Message: {}", connectOperationMessage);
        }
    }

    // Обработка сообщений уже соединённого клиента (прошедшего процедуру handshake)
    private void handleConnectionDemandExistsClient(MessageChannel clientChannel, ConnectOperation connectOperation, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        if (connectOperation.equals(ConnectOperation.REGISTER)) {
            handleRegisterDemand(clientChannel, clientAddress, connectOperationMessage);
        } else if (connectOperation.equals(ConnectOperation.AUTH)) {
            handleAuthDemand(clientChannel, clientAddress, connectOperationMessage);
        } else if (connectOperation.equals(ConnectOperation.DISCONNECT)) {
            handleDisconnectMessage(clientChannel, clientAddress, connectOperationMessage);
        } else if (connectOperation.equals(ConnectOperation.HANDSHAKE)) {
            LOG.info("Получено handshake сообщение от уже установившего связь клиента");
        }
    }

    private void handleRegisterDemand(MessageChannel clientChannel, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        LOG.info("Сообщение о регистрации клиента: " + clientAddress + ", " + connectOperationMessage);
        UserDTO userDTO = (UserDTO) connectOperationMessage.getAdditionalObject();
        LOG.info("Получен запрос на регистрацию: " + connectOperationMessage.getFrom() + ", " + userDTO);

        ConnectStatus registerStatus;
        String additionalMessage = null;

        // Проверяем введённое имя пользователя и пароль. Они не должны быть пустыми
        ConnectStatus userCredentialsStatus = checkUserCredentials(userDTO);
        if (userCredentialsStatus != null) {
            if (userCredentialsStatus.equals(INCORRECT_USERNAME)) {
                registerStatus = REGISTER_ERROR;
                additionalMessage = "Нельзя использовать такое имя пользователя";
            } else if (userCredentialsStatus.equals(INCORRECT_PASSWORD)) {
                registerStatus = REGISTER_ERROR;
                additionalMessage = "Нельзя использовать такой пароль";
            } else {
                registerStatus = REGISTER_ERROR;
                additionalMessage = "Непонятная ошибка";
            }
        } else {
            boolean isUserExistsInDatabase = Database.checkUserExistence(userDTO);
            if (isUserExistsInDatabase) {
                registerStatus = ALREADY_REGISTER;
            } else {
                boolean isUserInserted = Database.insertUser(userDTO);
                if (isUserInserted) {
                    registerStatus = REGISTER_OK;
                } else {
                    registerStatus = REGISTER_ERROR;
                }
            }
        }

        ConnectAnswerMessage registerAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, connectOperationMessage.getUuid(), registerStatus, additionalMessage);
        clientChannel.send(registerAnswerMessage);
        LOG.info("Направлен ответ о регистрации клиенту: " + clientAddress + ", " + registerAnswerMessage);
    }

    private ConnectStatus checkUserCredentials(UserDTO user) {
        String userName = user.getName();
        String password = user.getPassword();

        if (userName.equals("")) {
            return INCORRECT_USERNAME;
        }
        if (password.equals("")) {
            return INCORRECT_PASSWORD;
        }
        return null;
    }

    private void handleAuthDemand(MessageChannel clientChannel, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        LOG.info("Сообщение об аутентификации клиента: " + clientAddress + ", " + connectOperationMessage);
        UserDTO userDTO = (UserDTO) connectOperationMessage.getAdditionalObject();
        LOG.info("Получен запрос на аутентификацию от: " + connectOperationMessage.getFrom() + ", " + userDTO);
        ConnectStatus authStatus;
        if (isClientAuth(clientChannel)) {
            authStatus = ConnectStatus.ALREADY_AUTH;
        } else {
            authStatus = Database.getRegistrationAndAuthStatus(userDTO);
        }
        if (authStatus.equals(ConnectStatus.AUTH_OK)) {
            addAuthClient(clientChannel, userDTO.getName());  // сохраняем в карте авторизованного пользователя
        }
        ConnectAnswerMessage authAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, connectOperationMessage.getUuid(), authStatus, null);
        clientChannel.send(authAnswerMessage);
        LOG.info("Направлен ответ об аутентификации клиенту: " + clientAddress + ", " + authAnswerMessage);
    }

    private void handleDisconnectMessage(MessageChannel clientChannel, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        LOG.info("Сообщение об отключении клиента: " + clientAddress + ", " + connectOperationMessage);
        connectionMap.remove(clientChannel);
        removeAuthClient(clientChannel);
        try {
            clientChannel.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Optional<String> getClientName(MessageChannel clientChannel) {
        if (authMap.containsKey(clientChannel)) {
            return Optional.of(authMap.get(clientChannel));
        } else {
            return Optional.empty();
        }
    }

    // сохраняем в карте авторизованного пользователя
    public void addAuthClient(MessageChannel clientChannel, String userName) {
        authMap.put(clientChannel, userName);
    }

    public void removeAuthClient(MessageChannel clientChannel) {
        authMap.remove(clientChannel);
    }

    public boolean isClientAuth(MessageChannel clientChannel) {
        return authMap.containsKey(clientChannel);
    }

    @Override
    public void addNewClientChannel(MessageChannel clientChannel) {
        connectionMap.put(clientChannel, null);
    }

    @Override
    public void removeClientChannel(MessageChannel clientChannel) {
        connectionMap.remove(clientChannel);
    }
}
