package model;

import model.data.DataPacket;
import model.data.Message;
import model.data.ModuleNames;
import model.data.WelcomeInfo;
import model.net.Client;
import model.net.Connection;
import model.service.IncomingPacketService;
import model.utils.SettingsUtil;
import model.utils.JsonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ClientModel implements Model {

    private Connection masterServer;
    private MessengerModel model;
    private IncomingPacketService incomingPacketService;

    public ClientModel(MessengerModel model, Connection masterServer) {
        this.model = model;
        this.masterServer = masterServer;
        System.out.println(this.masterServer.getIp() + " is the master now");
        boolean connected = false;
        Socket master = new Socket();
        for(int i = 0; i < 3 && !connected; i++) {
            try {
                System.out.println("Connecting to " + masterServer.getIp() + ":" + masterServer.getPort());
                master.connect(new InetSocketAddress(masterServer.getIp(), masterServer.getPort()));
                System.out.println("Connected to " + master.getInetAddress() + ":" + master.getPort());
                this.masterServer.setSocket(master);
                incomingPacketService = new IncomingPacketService(masterServer, this);
                Thread incomingPacketServiceThread = new Thread(incomingPacketService);
                incomingPacketServiceThread.setName("Incoming packet service thread");
                incomingPacketServiceThread.setDaemon(true);
                incomingPacketServiceThread.start();
                connected = true;
                WelcomeInfo info = new WelcomeInfo(SettingsUtil.getInstance().getName(), SettingsUtil.getInstance().getIncomingPort());
                DataPacket welcomePacket = new DataPacket(model.getLocalConnection().getIp(), masterServer.getIp(), ModuleNames.Welcome, JsonUtil.getInstance().welcomeInfoToJson(info));
                sendPacket(welcomePacket, masterServer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage(String text, Client receiver) {
        DataPacket message;
        if(receiver == null) {
            message = new DataPacket(model.getLocalConnection().getIp(), null, ModuleNames.Message, text);
            sendPacket(message, masterServer);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), null, text));
        } else if(!receiver.getIp().equals(model.getLocalConnection().getIp())){
            message = new DataPacket(model.getLocalConnection().getIp(), receiver.getIp(), ModuleNames.Message, text);
            sendPacket(message, masterServer);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        } else {
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        }

    }

    @Override
    public void sendPacket(DataPacket packet, Connection connection) {
        String toSend = JsonUtil.getInstance().dataPacketToJson(packet);
        try {
            connection.getDos().writeUTF(toSend);
            connection.getDos().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processPacket(DataPacket packet, Connection sender) {
        switch (packet.getModule()) {
            case Message:
                model.getMessages().add(new Message(model.findNameByIp(packet.getSender()), model.findNameByIp(packet.getReceiver()), packet.getPayload()));
                break;
            case Update:
                List<Client> updateList = JsonUtil.getInstance().jsonToClientList(packet.getPayload());
                model.getClients().setAll(updateList);
                break;
        }
    }

    @Override
    public void checkConnection(Connection connection) {
        System.out.println("Master is gone!");
        if(connection == masterServer){
            incomingPacketService.setAlive(false);
            Client oldestClient = model.getClients().get(1);
            if(model.getClients().size() > 2){
                for(int i = 2; i < model.getClients().size(); i++){
                    Client contender = model.getClients().get(i);
                    if(contender.getCreatingDate().before(oldestClient.getCreatingDate())){
                        oldestClient = contender;
                    }
                }
            }
            try {
                masterServer.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(oldestClient.getIp().equals(model.getLocalConnection().getIp())){
                model.becomeMaster();
            } else {
                Connection newMaster = new Connection(oldestClient.getIp(), oldestClient.getPort());
                model.connectToNewMaster(newMaster);
            }
        }
    }

    @Override
    public void updateName() {
        sendServiceMessage();
    }

    private void sendServiceMessage() {
        DataPacket servicePacker = new DataPacket(model.getLocalConnection().getIp(), masterServer.getIp(), ModuleNames.Update, SettingsUtil.getInstance().getName());
        sendPacket(servicePacker, masterServer);
    }
}
