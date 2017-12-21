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
            ConnectAnswerMessage handshakeAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, connectOperationMessage.getUuid(), ConnectStatus.HANDSHAKE_OK);
            clientChannel.send(handshakeAnswerMessage);
            LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
        } else {
            LOG.info("Получен не HANDSHAKE запрос от нового клиента. Message: {}", connectOperationMessage);
        }
    }

    private void handleConnectionDemandExistsClient(MessageChannel clientChannel, ConnectOperation connectOperation, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        if (connectOperation.equals(ConnectOperation.AUTH)) {
            handleAuthDemand(clientChannel, clientAddress, connectOperationMessage);
        } else if (connectOperation.equals(ConnectOperation.DISCONNECT)) {
            handleDisconnectMessage(clientChannel, clientAddress, connectOperationMessage);
        } else if (connectOperation.equals(ConnectOperation.HANDSHAKE)) {
            LOG.info("Получено handshake сообщение от уже установившего связь клиента");
        }
    }

    private void handleAuthDemand(MessageChannel clientChannel, Address clientAddress, ConnectOperationMessage connectOperationMessage) {
        LOG.info("Сообщение об аутентификации клиента: " + clientAddress + ", " + connectOperationMessage);
        UserDTO userDTO = (UserDTO) connectOperationMessage.getAdditionalObject();
        LOG.info("Получен запрос на аутентификацию от: " + connectOperationMessage.getFrom() + ", " + userDTO);
        ConnectStatus authStatus;
        if (isClientAuth(clientChannel)) {
            authStatus = ConnectStatus.ALREADY_AUTH;
        } else {
            authStatus = Database.getAuthStatus(userDTO);
        }
        if (authStatus.equals(ConnectStatus.AUTH_OK)) {
            addAuthClient(clientChannel, userDTO.getName());  // сохраняем в карте авторизованного пользователя
        }
        ConnectAnswerMessage authAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, connectOperationMessage.getUuid(), authStatus);
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
