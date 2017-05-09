package model.service;

import model.MasterModel;
import model.data.DataPacket;
import model.net.Connection;
import model.utils.JsonUtil;

import java.io.IOException;
import java.util.LinkedList;

public class MasterRouterService implements Runnable {

    private MasterModel model;
    private LinkedList<DataPacket> packetsToSend;
    private boolean alive = true;

    public MasterRouterService(MasterModel model) {
        this.model = model;
        packetsToSend = new LinkedList<>();
    }

    @Override
    public void run() {
        while (alive) {
            if (packetsToSend.isEmpty()) {
                Thread.yield();
            } else {
                DataPacket packet = packetsToSend.get(0);
                if (packet.getReceiver() != null) {
                    Connection receiver = model.findConnectionByIp(packet.getReceiver());
                    writeData(packet, receiver);
                } else {
                    for (Connection connection : model.getConnections()) {
                        if (!packet.getSender().equals(connection.getIp())) {
                            writeData(packet, connection);
                        }
                    }
                }
                packetsToSend.remove(0);
            }
        }
    }

    private void writeData(DataPacket data, Connection receiver) {
        try {
            receiver.getDos().writeUTF(JsonUtil.getInstance().dataPacketToJson(data));
            receiver.getDos().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addPacketToSend(DataPacket packet) {
        packetsToSend.add(packet);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
