package model.service;

import model.MasterModel;
import model.net.Connection;
import model.net.ConnectionStatus;
import model.utils.SettingsUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterTCPConnectionService implements Runnable {

    private ServerSocket incomingSocket;
    private MasterModel model;

    public MasterTCPConnectionService(MasterModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        while(incomingSocket == null) {
            try {
                incomingSocket = new ServerSocket(SettingsUtil.getInstance().getIncomingPort());
                System.out.println("Created master server socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while(true){
            Socket newSocket = null;
            try {
                newSocket = incomingSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Connection newConnection = new Connection(newSocket);
            System.out.println("New connection: " + newConnection.getIp());
            newConnection.setStatus(ConnectionStatus.connected);
            model.getConnections().add(newConnection);
            model.createIncomingService(newConnection);
        }
    }

}
