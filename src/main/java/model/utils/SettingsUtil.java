package model.utils;

import java.io.*;


public class SettingsUtil {

    private static SettingsUtil settingsUtil = new SettingsUtil();
    private File configFile = new File("config/config.ini");
    private static final int DEFAULT_PORT = 15001;
    private int incomingPort;
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            String configLine;
            while ((configLine = fileReader.readLine()) != null) {
                if (!configLine.equals("")) {
                    String name = configLine.trim().substring(0, configLine.lastIndexOf("="));
                    String data = configLine.substring(configLine.lastIndexOf("=") + 1);
                    if (!data.equals("")) {
                        switch (name) {
                            case "incoming_port":
                                try {
                                    incomingPort = Integer.parseInt(data);
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
            if (incomingPort == 0) {
                incomingPort = DEFAULT_PORT;
                PrintWriter printWriter = new PrintWriter(new FileWriter(configFile, true));
                printWriter.println("incoming_port=" + DEFAULT_PORT);
                printWriter.flush();
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static SettingsUtil getInstance() {
        return settingsUtil;
    }

    public boolean settingsLoaded() {
        return !(incomingPort == 0);
    }

    public boolean profileLoaded() {
        return (name != null);
    }

    public boolean settingsFileExists() {
        return configFile.exists();
    }

    public int getIncomingPort() {
        return incomingPort;
    }

    public String getName() {
        return name;
    }

    public SaveConstants saveProfile(String name) {
        this.name = name;
        return writeConfigToFile();
    }

    public SaveConstants saveSettings(int inPort) {
        incomingPort = inPort;
        return writeConfigToFile();
    }

    private SaveConstants writeConfigToFile() {
        try {
            PrintWriter printWriter = new PrintWriter(configFile);
            printWriter.println("name=" + name);
            printWriter.println("incoming_port=" + incomingPort);
            printWriter.flush();
            printWriter.close();
            return SaveConstants.Success;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return SaveConstants.Failure;
        }
    }
}
