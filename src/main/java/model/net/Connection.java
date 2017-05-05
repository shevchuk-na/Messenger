package model.net;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Connection implements Serializable{

    private InetAddress ip;
    private int port;
    private Socket socket;
    private DataOutputStream dos;
    private ConnectionStatus status;

    public Connection(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Connection(Socket socket) {
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
        this.socket = socket;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIp() {
        return ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public int getPort() {
        return port;
    }
}
