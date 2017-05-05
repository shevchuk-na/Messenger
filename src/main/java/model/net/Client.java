package model.net;

import javafx.beans.property.SimpleStringProperty;

import java.net.InetAddress;
import java.util.Date;

public class Client {

    private InetAddress ip;
    private int port;
    private String name;
    private Date creatingDate;

    public Client(InetAddress ip, int port, String name, Date creatingDate) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.creatingDate = creatingDate;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }


    public Date getCreatingDate() {
        return creatingDate;
    }

    public void setName(String name) {
        this.name = name;
    }
}
