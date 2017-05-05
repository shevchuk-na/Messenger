package model.service;

import model.MasterModel;
import model.net.Connection;
import model.net.ConnectionMessages;

import java.io.IOException;
import java.net.*;

public class UDPService implements Runnable{

    private NetworkDiscovererService service;
    private DatagramSocket socket;
    private boolean isAlive = true;

    public UDPService(NetworkDiscovererService service, DatagramSocket socket) {
        this.service = service;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(true){
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);
                socket.setSoTimeout(500);
                socket.receive(packet);
                if(!packet.getAddress().equals(service.getLocalConnection().getIp())) {
                    System.out.println("Got UDP packet");
                    String data = new String(packet.getData(), 0, packet.getLength());
                    if (data.equals(ConnectionMessages.RESPONSE_MESSAGE)) {
                        System.out.println("It is from master!");
                        service.setMasterServer(new Connection(packet.getAddress(), packet.getPort()));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("UDP discovery socket closed");
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
