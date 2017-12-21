package ru.geekbrains.server.handler;

import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.ConnectOperationMessage;

import java.util.Map;
import java.util.Optional;

public interface ConnectDemandHandler {
    void handleConnectDemand(Address clientAddress, MessageChannel clientChannel, ConnectOperationMessage message);

    Map<MessageChannel, Address> getClientAddressMap();

    Optional<String> getClientName(MessageChannel clientChannel);

    void addAuthClient(MessageChannel clientChannel, String userName);

    void removeAuthClient(MessageChannel clientChannel);

    boolean isClientAuth(MessageChannel clientChannel);

    void addNewClientChannel(MessageChannel clientChannel);

    void removeClientChannel(MessageChannel clientChannel);
}
