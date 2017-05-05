package model.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.data.DataPacket;
import model.data.WelcomeInfo;
import model.net.Client;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final JsonUtil instance = new JsonUtil();
    private Gson gson = new Gson();

    private JsonUtil() {

    }

    public String dataPacketToJson(DataPacket packet){
        return gson.toJson(packet);
    }

    public DataPacket jsonToDataPacket(String json){
        return gson.fromJson(json, DataPacket.class);
    }

    public String clientListToJson(List<Client> clientList){
        return gson.toJson(clientList);
    }

    public List<Client> jsonToClientList(String json){
        return gson.fromJson(json, new TypeToken<List<Client>>(){}.getType());
    }

    public static JsonUtil getInstance(){
        return instance;
    }

    public WelcomeInfo jsonToWelcomeInfo(String json){
        return gson.fromJson(json, WelcomeInfo.class);
    }

    public String welcomeInfoToJson(WelcomeInfo info){
        return gson.toJson(info);
    }
}
