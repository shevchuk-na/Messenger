package model.data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class Message {

    private SimpleStringProperty sender;
    private SimpleStringProperty receiver;
    private SimpleStringProperty content;
    private SimpleObjectProperty<Date> timestamp;

    public Message(String sender, String receiver, String content) {
        this.sender = new SimpleStringProperty(sender);
        this.receiver = new SimpleStringProperty(receiver);
        this.content = new SimpleStringProperty(content);
        this.timestamp = new SimpleObjectProperty<>(new Date());
    }

    public String getSender() {
        return sender.get();
    }

    public SimpleStringProperty senderProperty() {
        return sender;
    }

    public String getReceiver() {
        return receiver.get();
    }

    public SimpleStringProperty receiverProperty() {
        return receiver;
    }

    public String getContent() {
        return content.get();
    }

    public SimpleStringProperty contentProperty() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp.get();
    }

    public SimpleObjectProperty<Date> timestampProperty() {
        return timestamp;
    }
}
