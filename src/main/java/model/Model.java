package model;

import model.data.DataPacket;
import model.net.Client;
import model.net.Connection;

import java.io.File;

public interface Model {

    void sendMessage(String text, Client receiver);

    void updateName();

    void sendPacket(DataPacket packet);

    void processPacket(DataPacket packet, Connection sender);

    void checkConnection(Connection connection);

    void shutdown();

    void sendFile(File file, Client receiver);

    Connection getLocalConnection();

    MessengerModel getModel();
}
