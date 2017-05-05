package model.data;

public class WelcomeInfo {

    private String name;
    private int port;

    public WelcomeInfo(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
}
