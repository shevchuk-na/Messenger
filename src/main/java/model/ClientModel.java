package model;

import model.data.*;
import model.net.Client;
import model.net.Connection;
import model.service.FileTransferService;
import model.service.IncomingPacketService;
import model.utils.JsonUtil;
import model.utils.SettingsUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ClientModel implements Model {

    private Connection masterServer;
    private MessengerModel model;
    private IncomingPacketService incomingPacketService;

    ClientModel(MessengerModel model, Connection masterServer) {
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
                WelcomeInfo info = new WelcomeInfo(SettingsUtil.getInstance().getName(), SettingsUtil.getInstance().getLocalPort());
                DataPacket welcomePacket = new DataPacket(model.getLocalConnection().getIp(), masterServer.getIp(), ModuleNames.Welcome, JsonUtil.getInstance().welcomeInfoToJson(info));
                sendPacket(welcomePacket);
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
            sendPacket(message);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), null, text));
        } else if(!receiver.getIp().equals(model.getLocalConnection().getIp())){
            message = new DataPacket(model.getLocalConnection().getIp(), receiver.getIp(), ModuleNames.Message, text);
            sendPacket(message);
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        } else {
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), text));
        }
    }

    @Override
    public void sendPacket(DataPacket packet) {
        String toSend = JsonUtil.getInstance().dataPacketToJson(packet);
        try {
            masterServer.getDos().writeUTF(toSend);
            masterServer.getDos().flush();
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
            case File:
                receiveFile(packet);
                break;
        }
    }

    private void receiveFile(DataPacket packet) {
        FileInfo fileInfo = JsonUtil.getInstance().jsonToFileInfo(packet.getPayload());
        FileTransferService fileTransferService = new FileTransferService(this, fileInfo, packet.getSender());
        Thread fileTransferServiceThread = new Thread(fileTransferService);
        fileTransferServiceThread.setName("File transfer service thread");
        fileTransferServiceThread.setDaemon(true);
        fileTransferServiceThread.start();
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
        sendPacket(servicePacker);
    }

    @Override
    public void shutdown() {
        try {
            incomingPacketService.setAlive(false);
            masterServer.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFile(File file, Client receiver) {
        if (!receiver.getIp().equals(model.getLocalConnection().getIp())) {
            FileTransferService fileTransferService = new FileTransferService(this, file, receiver.getIp());
            Thread fileTransferServiceThread = new Thread(fileTransferService);
            fileTransferServiceThread.setName("File transfer service thread");
            fileTransferServiceThread.setDaemon(true);
            fileTransferServiceThread.start();
        } else {
            model.getMessages().add(new Message(SettingsUtil.getInstance().getName(), receiver.getName(), "Can't send to self"));
        }
    }

    @Override
    public Connection getLocalConnection() {
        return model.getLocalConnection();
    }

    @Override
    public MessengerModel getModel() {
        return model;
    }
}
