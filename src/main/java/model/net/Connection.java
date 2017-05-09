package model.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class Connection implements Serializable{

    private InetAddress ip;
    private int port;
    private Socket socket;
    private DataOutputStream dos;

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

    public int getPort() {
        return port;
    }
}
