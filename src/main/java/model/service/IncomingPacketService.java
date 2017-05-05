package model.service;

import model.Model;
import model.data.DataPacket;
import model.net.Connection;
import model.utils.JsonUtil;

import java.io.DataInputStream;
import java.io.IOException;

public class IncomingPacketService implements Runnable {

    private Connection connection;
    private Model model;
    private DataInputStream dis;
    private boolean alive = true;

    public IncomingPacketService(Connection connection, Model model) {
        this.connection = connection;
        this.model = model;
    }

    @Override
    public void run() {
        while(alive) {
            if (dis == null) {
                if (connection.getSocket() != null) {
                    try {
                        dis = new DataInputStream(connection.getSocket().getInputStream());
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        while(alive) {
            try {
                String received = dis.readUTF();
                DataPacket packet = JsonUtil.getInstance().jsonToDataPacket(received);
                if(packet != null) {
                    model.processPacket(packet, connection);
                }
            } catch (IOException e) {
                model.checkConnection(connection);
            }

        }
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
