package model;

import model.data.DataPacket;
import model.net.Client;
import model.net.Connection;

public interface Model {

    void sendMessage(String text, Client receiver);

    void updateName();

    void sendPacket(DataPacket packet, Connection connection);

    void processPacket(DataPacket packet, Connection sender);

    void checkConnection(Connection connection);
}
