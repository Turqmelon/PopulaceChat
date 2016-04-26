package com.turqmelon.PopulaceChat.Channels;


import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.Populace.Resident.ResidentManager;
import com.turqmelon.Populace.Utils.Msg;
import com.turqmelon.PopulaceChat.Channels.core.GlobalChat;
import com.turqmelon.PopulaceChat.Channels.core.TownChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public abstract class ChatChannel {

    private UUID uuid;
    private String name;
    private String shortName;
    private ChatColor color;

    private boolean defaultChannel = false;
    private boolean leaveable = true;

    private List<Resident> listeners = new ArrayList<>();

    public ChatChannel(UUID uuid, String name, String shortName, ChatColor color) {
        this.uuid = uuid;
        this.name = name;
        this.shortName = shortName;
        this.color = color;
    }

    public ChatChannel(JSONObject object){
        this.uuid = UUID.fromString((String) object.get("uuid"));
        this.name = (String) object.get("name");
        this.shortName = (String) object.get("shortname");
        this.color = ChatColor.valueOf((String) object.get("color"));
        this.defaultChannel = (boolean) object.get("default");
        this.leaveable = (boolean) object.get("leaveable");

        JSONArray listeners = (JSONArray) object.get("listeners");
        for(Object o : listeners){
            String resid = (String)o;
            Resident resident = ResidentManager.getResident(UUID.fromString(resid));
            if (resident != null){
                getListeners().add(resident);
            }
        }
    }

    @Override
    public String toString() {
        return toJSON().toJSONString();
    }

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        object.put("uuid", getUuid().toString());
        object.put("name", getName());
        object.put("shortname", getShortName());
        object.put("color", getColor().name());

        object.put("default", isDefaultChannel());
        object.put("leaveable", isLeaveable());

        JSONArray listeners = new JSONArray();
        for(Resident resident : getListeners()){
            listeners.add(resident.getUuid().toString());
        }

        object.put("listeners", listeners);

        if ((this instanceof GlobalChat)){
            object.put("type", "global");
        }
        else if ((this instanceof TownChat)){
            object.put("type", "town");
        }
        else{
            object.put("type", "custom");
        }

        return object;
    }


    protected abstract String formatMessage(Player player, Resident sender, String message);

    public void chat(Player player, Resident resident, String message){
        if (canJoin(resident) && canChat(resident)){

            join(resident);
            sendMessage(formatMessage(player, resident, message));

        }
        else{
            resident.sendMessage(Msg.ERR + "You may not chat in " + getColor() + getName() + "§c.");
        }
    }

    public void leave(Resident resident){
        if (getListeners().contains(resident)){
            if (isLeaveable()){
                getListeners().remove(resident);
                resident.sendMessage(Msg.OK + "You've left " + getColor() + getName() + "§a.");
            }
            else{
                resident.sendMessage(Msg.ERR + "You may not leave " + getColor() + getName() + "§c.");
            }
        }
        else{
            resident.sendMessage(Msg.ERR + "You're not in " + getColor() + getName() + "§c.");
        }
    }

    public void join(Resident resident){

        if (canJoin(resident)){
            if (!getListeners().contains(resident)){
                getListeners().add(resident);
                resident.sendMessage(Msg.OK + "You've joined " + getColor() + getName() + "§a.");
            }
        }
        else{
            resident.sendMessage(Msg.ERR + "You can't enter " + getColor() + getName() + ChatColor.RED + ".");
        }

    }

    public boolean isLeaveable() {
        return leaveable && !isDefaultChannel();
    }

    public void setLeaveable(boolean leaveable) {
        this.leaveable = leaveable;
    }

    public boolean isDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(boolean defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public void sendMessage(String message){

        String msg = getColor() + "§l[" + getShortName() + "] " + getColor() + message;

        for(Resident resident : getListeners()){
            if (!canJoin(resident))continue;;
            resident.sendMessage(msg);
        }

    }

    protected String getDisplayName(Player player, Resident resident) {
        return (resident.getTown() != null ? resident.getTown().getLevel().getColor() + "[" + resident.getTown().getName() + "] " + ChatColor.WHITE : "") +
                player.getDisplayName();
    }

    public abstract boolean canJoin(Resident resident);
    public abstract boolean canChat(Resident resident);

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public ChatColor getColor() {
        return color;
    }

    public List<Resident> getListeners() {
        return listeners;
    }
}
