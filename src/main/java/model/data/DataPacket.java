package model.data;

import java.net.InetAddress;
import java.util.Date;

public class DataPacket {

    private InetAddress sender;
    private InetAddress receiver;
    private ModuleNames module;
    private String payload;
    private Date timestamp;

    public DataPacket(InetAddress sender, ModuleNames module, String payload) {
        this.sender = sender;
        this.module = module;
        this.payload = payload;
        this.timestamp = new Date();
    }

    public DataPacket(InetAddress sender, InetAddress receiver, ModuleNames module, String payload) {
        this.sender = sender;
        this.receiver = receiver;
        this.module = module;
        this.payload = payload;
        this.timestamp = new Date();
    }

    public InetAddress getSender() {
        return sender;
    }

    public InetAddress getReceiver() {
        return receiver;
    }

    public ModuleNames getModule() {
        return module;
    }

    public String getPayload() {
        return payload;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setReceiver(InetAddress receiver) {
        this.receiver = receiver;
    }
}
