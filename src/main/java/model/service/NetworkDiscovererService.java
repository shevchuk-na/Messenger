package model.service;

import model.net.Connection;
import model.net.ConnectionMessages;
import model.utils.SettingsUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkDiscovererService {

    private Connection localConnection;
    private Connection masterServer;

    public NetworkDiscovererService() {
        try {
            localConnection = new Connection(discoverHostIp(), SettingsUtil.getInstance().getLocalPort());
            DatagramSocket socket = new DatagramSocket(SettingsUtil.getInstance().getLocalPort());
            String localAddress = localConnection.getIp().getHostAddress();
            InetAddress broadcastAddress = InetAddress.getByName(localAddress.substring(0, localAddress.lastIndexOf(".")) + ".255");
            DatagramPacket broadcastPacket = new DatagramPacket(ConnectionMessages.HELLO_MESSAGE.getBytes(), ConnectionMessages.HELLO_MESSAGE.length(), broadcastAddress, SettingsUtil.getInstance()
                    .getRemotePort());
            UDPService udpService = new UDPService(this, socket);
            Thread udpServiceThread = new Thread(udpService);
            udpServiceThread.setName("UDP service thread");
            udpServiceThread.setDaemon(true);
            udpServiceThread.start();
            socket.send(broadcastPacket);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            udpService.setAlive(false);
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private InetAddress discoverHostIp() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = e.nextElement();
                Enumeration<InetAddress> ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress address = ee.nextElement();
                    if (address.toString().startsWith("/192.168.") || address.toString().startsWith("/10.") || address.toString().startsWith("/172.16.")) {
                        return address;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Connection getLocalConnection() {
        return localConnection;
    }

    public Connection getMasterServer() {
        return masterServer;
    }

    public void setMasterServer(Connection masterServer) {
        this.masterServer = masterServer;
    }
}
