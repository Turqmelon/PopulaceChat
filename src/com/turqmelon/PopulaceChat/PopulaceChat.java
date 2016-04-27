package com.turqmelon.PopulaceChat;

import com.turqmelon.PopulaceChat.Channels.ChannelManager;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import com.turqmelon.PopulaceChat.Channels.core.GlobalChat;
import com.turqmelon.PopulaceChat.Channels.core.TownChat;
import com.turqmelon.PopulaceChat.Channels.custom.CustomChannel;
import com.turqmelon.PopulaceChat.Commands.ChatCommand;
import com.turqmelon.PopulaceChat.Listeners.ChatListener;
import com.turqmelon.PopulaceChat.Listeners.PopulaceListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class PopulaceChat extends JavaPlugin {

    private static PopulaceChat instance;

    public static void loadData() throws IOException, ParseException {

        getInstance().getLogger().log(Level.INFO, "Loading channels...");

        File dir = getInstance().getDataFolder();
        if (dir.exists()){
            File file = new File(dir, "data.json");
            if (file.exists()){

                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(new FileReader(file));

                JSONArray channels = (JSONArray) object.get("channels");
                JSONArray focus = (JSONArray) object.get("focus");

                for(Object o : focus){
                    String s = (String)o;
                    String[] sd = s.split(":");
                    UUID uuid = UUID.fromString(sd[0]);
                    UUID focusid = UUID.fromString(sd[1]);
                    ChannelManager.getFocus().put(uuid, focusid);
                }

                for(Object o : channels){
                    JSONObject obj = (JSONObject)o;
                    String type = (String) obj.get("type");
                    ChatChannel chatChannel;
                    if (type.equalsIgnoreCase("global")){
                        chatChannel = new GlobalChat(obj);
                    }
                    else if (type.equalsIgnoreCase("town")){
                        chatChannel = new TownChat(obj);
                    }
                    else{
                        chatChannel = new CustomChannel(obj);
                    }
                    ChannelManager.getChannels().add(chatChannel);
                }

                getInstance().getLogger().log(Level.INFO, "Loaded " + ChannelManager.getChannels().size() + " channels.");

            }
        }

    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    public static void saveData() throws IOException {

        File dir = getInstance().getDataFolder();
        if (!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dir, "data.json");
        if (!file.exists()){
            file.createNewFile();
        }

        JSONObject data = new JSONObject();
        JSONArray channels = new JSONArray();
        JSONArray focus = new JSONArray();

        for(ChatChannel chatChannel : ChannelManager.getChannels()){
            channels.add(chatChannel.toJSON());
        }

        for(UUID uuid : ChannelManager.getFocus().keySet()){
            UUID focusid = ChannelManager.getFocus().get(uuid);
            focus.add(uuid.toString() + ":" + focusid.toString());
        }

        data.put("channels", channels);
        data.put("focus", focus);

        FileWriter writer = new FileWriter(file);
        writer.write(data.toJSONString());
        writer.flush();
        writer.close();

    }

    @Override
    public void onDisable() {
        try {
            saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PopulaceListener(), this);

        try {
            loadData();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // First startup, make two default channels
        if (ChannelManager.getChannels().size() == 0){

            ChatChannel global = new GlobalChat(UUID.randomUUID(), "Global", "G", ChatColor.WHITE);
            global.setDefaultChannel(true);
            global.setLeaveable(false);
            ChannelManager.getChannels().add(global);

            ChatChannel town = new TownChat(UUID.randomUUID(), "Town", "T", ChatColor.DARK_AQUA);
            town.setDefaultChannel(false);
            town.setLeaveable(false);
            ChannelManager.getChannels().add(town);

            try {
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getCommand("chat").setExecutor(new ChatCommand());

    }

    public static PopulaceChat getInstance(){
        return instance;
    }
}
