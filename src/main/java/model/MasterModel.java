package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.data.DataPacket;
import model.data.Message;
import model.data.ModuleNames;
import model.data.WelcomeInfo;
import model.net.Client;
import model.net.Connection;
import model.service.IncomingPacketService;
import model.service.MasterRouterService;
import model.service.MasterTCPConnectionService;
import model.service.MasterUDPService;
import model.utils.SettingsUtil;
import model.utils.JsonUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MasterModel implements Model {

    private MessengerModel model;
    private ObservableList<Connection> connections = FXCollections.observableArrayList();
    private Map<Connection, IncomingPacketService> masterIncomingPacketServiceMap;
    private MasterRouterService masterRouterService;

    MasterModel(MessengerModel model) {
        this.model = model;
        System.out.println("I am master now!");
        model.getClients().clear();
        model.getClients().add(new Client(model.getLocalConnection().getIp(), model.getLocalConnection().getPort(), SettingsUtil.getInstance().getName(), new Date()));
        getConnections().clear();
        masterIncomingPacketServiceMap = new HashMap<>();
        MasterTCPConnectionService masterTCPConnectionService = new MasterTCPConnectionService(this);
        Thread masterTCPConnectionThread = new Thread(masterTCPConnectionService);
        masterTCPConnectionThread.setName("Master TCP connection thread");
        masterTCPConnectionThread.setDaemon(true);
        masterTCPConnectionThread.start();
        masterRouterService = new MasterRouterService(this);
        Thread masterRouterThread = new Thread(masterRouterService);
        masterRouterThread.setName("Master router thread");
        masterRouterThread.setDaemon(true);
        masterRouterThread.start();
        MasterUDPService masterUDPService = new MasterUDPService(this);
        Thread masterUDPServiceThread = new Thread(masterUDPService);
        masterUDPServiceThread.setName("Master UDP service thread");
        masterUDPServiceThread.setDaemon(true);
        masterUDPServiceThread.start();
    }

    @Override
    public void sendMessage(String text, Client receiver) {
        DataPacket message;
        if(receiver == null) {
            message = new DataPacket(model.getLocalConnection().getIp(), null, ModuleNames.Message, text);
            masterRouterService.addPacketToSend(message);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), null, text));
        } else if(!receiver.getIp().equals(model.getLocalConnection().getIp())){
            message = new DataPacket(model.getLocalConnection().getIp(), receiver.getIp(), ModuleNames.Message, text);
            masterRouterService.addPacketToSend(message);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        } else {
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        }

    }

    @Override
    public void sendPacket(DataPacket packet, Connection connection) {
        masterRouterService.addPacketToSend(packet);
    }

    @Override
    public void processPacket(DataPacket packet, Connection sender) {
        switch (packet.getModule()) {
            case Welcome:
                WelcomeInfo info = JsonUtil.getInstance().jsonToWelcomeInfo(packet.getPayload());
                model.getClients().add(new Client(sender.getIp(), info.getPort(), info.getName(), packet.getTimestamp()));
                updateClientLists();
                break;
            case Message:
                if(packet.getReceiver() == null){
                    sendPacket(packet, sender);
                    model.getMessages().add(new Message(model.findNameByIp(packet.getSender()), model.findNameByIp(packet.getReceiver()), packet.getPayload()));
                } else if(packet.getReceiver().equals(model.getLocalConnection().getIp())){
                    model.getMessages().add(new Message(model.findNameByIp(packet.getSender()), model.findNameByIp(packet.getReceiver()), packet.getPayload()));
                } else {
                    sendPacket(packet, sender);
                }
                break;
            case Update:
                updateName(sender.getIp(), packet.getPayload());
        }
    }

    public void createIncomingService(Connection connection) {
        IncomingPacketService incomingPacketService = new IncomingPacketService(connection, this);
        masterIncomingPacketServiceMap.put(connection, incomingPacketService);
        Thread incomingPacketServiceThread = new Thread(incomingPacketService);
        incomingPacketServiceThread.setName("Incoming packet service thread for" + connection.getIp());
        incomingPacketServiceThread.setDaemon(true);
        incomingPacketServiceThread.start();
    }

    private void updateName(InetAddress ip, String senderName) {
        for(int i = 0; i < model.getClients().size(); i++){
            Client client = model.getClients().get(i);
            if(client.getIp().equals(ip) && !client.getName().equals(senderName)){
                model.getClients().set(i, new Client(client.getIp(), client.getPort(), senderName, client.getCreatingDate()));
                updateClientLists();
                break;
            }
        }
    }

    private void updateClientLists() {
        DataPacket clientList = new DataPacket(model.getLocalConnection().getIp(), null,
                ModuleNames.Update, JsonUtil.getInstance().clientListToJson(model.getClients()));
        masterRouterService.addPacketToSend(clientList);
    }

    @Override
    public void checkConnection(Connection connection) {
        try {
            int read = connection.getSocket().getInputStream().read();
        } catch (IOException ignored) {

        } finally {
            System.out.println("Connection " + connection.getIp() + ":" + connection.getPort() + " has disconnected");
            getConnections().remove(connection);
            masterIncomingPacketServiceMap.get(connection).setAlive(false);
            masterIncomingPacketServiceMap.remove(connection);
            Client client = model.findClientByIp(connection.getIp());
            if (client != null) {
                model.getClients().remove(client);
                updateClientLists();
            }
        }
    }

    public Connection findConnectionByIp(InetAddress receiver) {
        if(receiver.equals(model.getLocalConnection().getIp())){
            return model.getLocalConnection();
        }
        for(Connection connection : connections){
            if(connection.getIp().equals(receiver)){
                return connection;
            }
        }
        return null;
    }

    @Override
    public void updateName() {
        model.getClients().set(0, new Client(model.getLocalConnection().getIp(), model.getLocalConnection().getPort(),
                SettingsUtil.getInstance().getName(), model.getClients().get(0).getCreatingDate()));
        updateClientLists();
    }

    public ObservableList<Connection> getConnections() {
        return connections;
    }

    public Connection getLocalConnection() {
        return model.getLocalConnection();
    }
}
