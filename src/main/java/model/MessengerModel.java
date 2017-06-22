package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.data.Message;
import model.net.Client;
import model.net.Connection;
import model.service.NetworkDiscovererService;

import java.io.File;
import java.net.InetAddress;

public class MessengerModel {

    private Model currentModel;
    private Role role;
    private Connection localConnection;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private ObservableList<Client> clients = FXCollections.observableArrayList();

    public void initialize(){
        NetworkDiscovererService networkDiscovererService = new NetworkDiscovererService();
        localConnection = networkDiscovererService.getLocalConnection();
        if(networkDiscovererService.getMasterServer() == null){
            role = Role.Master;
            currentModel = new MasterModel(this);
        } else {
            role = Role.Client;
            currentModel = new ClientModel(this, networkDiscovererService.getMasterServer());
        }
    }

    public void sendMessage(String text, Client receiver){
        currentModel.sendMessage(text, receiver);
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public ObservableList<Client> getClients() {
        return clients;
    }

    public Client findClientByIp(InetAddress inetAddress){
        for(Client client : clients){
            if(client.getIp() != null && client.getIp().equals(inetAddress)){
                return client;
            }
        }
        return null;
    }

    String findNameByIp(InetAddress inetAddress){
        for(Client client : clients){
            if(client.getIp() != null && client.getIp().equals(inetAddress)){
                return client.getName();
            }
        }
        return null;
    }

    public void updateName() {
        currentModel.updateName();
    }

    void becomeMaster() {
        if(role == Role.Client){
            currentModel = new MasterModel(this);
            role = Role.Master;
        }
    }

    void connectToNewMaster(Connection master){
        if(role == Role.Client){
            currentModel = new ClientModel(this, master);
        }
    }

    public Connection getLocalConnection() {
        return localConnection;
    }

    public void shutdown() {
        currentModel.shutdown();
    }

    public void transferFile(File file, Client client) {
        currentModel.sendFile(file, client);
    }
}
