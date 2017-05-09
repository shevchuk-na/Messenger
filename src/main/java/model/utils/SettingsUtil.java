package model.utils;

import java.io.*;


public class SettingsUtil {

    private static final int DEFAULT_PORT = 15001;
    private static SettingsUtil settingsUtil = new SettingsUtil();
    private File configFile = new File("config/config.ini");
    private int localPort;
    private int remotePort;
    private String name;

    private SettingsUtil() {
        try {
            if (!configFile.exists()) {
                try {
                    File settingsFolder = new File(configFile.getParent());
                    if (!settingsFolder.exists()) {
                        settingsFolder.mkdir();
                    }
                    configFile.createNewFile();
                    localPort = DEFAULT_PORT;
                    remotePort = DEFAULT_PORT;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
                String configLine;
                while ((configLine = fileReader.readLine()) != null) {
                    if (!configLine.equals("")) {
                        String name = configLine.trim().substring(0, configLine.lastIndexOf("="));
                        String data = configLine.substring(configLine.lastIndexOf("=") + 1);
                        if (!data.equals("")) {
                            switch (name) {
                                case "local_port":
                                    try {
                                        localPort = Integer.parseInt(data);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "remote_port":
                                    try {
                                        remotePort = Integer.parseInt(data);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "name":
                                    this.name = data;
                            }
                        }
                    }
                }
                fileReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static SettingsUtil getInstance() {
        return settingsUtil;
    }

    public boolean settingsLoaded() {
        return !((localPort == 0) || (remotePort == 0));
    }

    public boolean profileLoaded() {
        return (name != null);
    }

    public boolean settingsFileExists() {
        return configFile.exists();
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getName() {
        return name;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public SaveConstants saveProfile(String name) {
        this.name = name;
        return writeConfigToFile();
    }

    public SaveConstants saveSettings(int localPort, int remotePort) {
        this.localPort = localPort;
        this.remotePort = remotePort;
        return writeConfigToFile();
    }

    private SaveConstants writeConfigToFile() {
        try {
            PrintWriter printWriter = new PrintWriter(configFile);
            printWriter.println("name=" + name);
            printWriter.println("local_port=" + localPort);
            printWriter.println("remote_port=" + remotePort);
            printWriter.flush();
            printWriter.close();
            return SaveConstants.Success;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return SaveConstants.Failure;
        }
    }
}
