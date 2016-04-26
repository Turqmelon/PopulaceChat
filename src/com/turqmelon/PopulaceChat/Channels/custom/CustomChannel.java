package com.turqmelon.PopulaceChat.Channels.custom;

import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;


public class CustomChannel extends ChatChannel {

    public CustomChannel(UUID uuid, String name, String shortName, ChatColor color) {
        super(uuid, name, shortName, color);
    }

    public CustomChannel(JSONObject object){
        super(object);
    }

    @Override
    protected String formatMessage(Player player, Resident sender, String message) {
        return player.getDisplayName() + getColor() + ": " + message;
    }

    @Override
    public boolean canJoin(Resident resident) {
        Player player = Bukkit.getPlayer(resident.getUuid());
        return player != null && player.hasPermission("populace.chat." + getName().toLowerCase() + ".join");
    }

    @Override
    public boolean canChat(Resident resident) {
        Player player = Bukkit.getPlayer(resident.getUuid());
        return player != null && player.hasPermission("populace.chat." + getName().toLowerCase() + ".send");

    }
}
