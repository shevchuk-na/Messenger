package model.service;

import model.MasterModel;
import model.net.ConnectionMessages;
import model.utils.SettingsUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MasterUDPService implements Runnable{

    private MasterModel model;
    private boolean alive = true;
    private DatagramSocket socket;

    public MasterUDPService(MasterModel model) {
        this.model = model;

    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(SettingsUtil.getInstance().getLocalPort());
            while (alive) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);
                socket.receive(packet);
                System.out.println("Master UDP service got packet from " + packet.getAddress());
                if(!packet.getAddress().equals(model.getLocalConnection().getIp())) {
                    String data = new String(packet.getData(), 0, packet.getLength());
                    if (data.equals(ConnectionMessages.HELLO_MESSAGE)) {
                        packet = new DatagramPacket(ConnectionMessages.RESPONSE_MESSAGE.getBytes(), 0, ConnectionMessages.RESPONSE_MESSAGE.length(), packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        System.out.println("Send master response");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void closeSocket() {
        socket.close();
    }
}
