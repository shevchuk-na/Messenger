package model.service;

import model.MasterModel;
import model.net.Connection;
import model.utils.SettingsUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterTCPConnectionService implements Runnable {

    private ServerSocket incomingSocket;
    private MasterModel model;
    private boolean alive = true;

    public MasterTCPConnectionService(MasterModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        while(incomingSocket == null) {
            try {
                incomingSocket = new ServerSocket(SettingsUtil.getInstance().getLocalPort());
                System.out.println("Created master server socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (alive) {
            Socket newSocket = null;
            try {
                newSocket = incomingSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Connection newConnection = new Connection(newSocket);
            System.out.println("New connection: " + newConnection.getIp());
            model.getConnections().add(newConnection);
        }
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void closeServerSocket() {
        try {
            incomingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
