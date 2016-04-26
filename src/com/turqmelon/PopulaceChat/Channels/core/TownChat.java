package com.turqmelon.PopulaceChat.Channels.core;


import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

public class TownChat extends ChatChannel {
    public TownChat(UUID uuid, String name, String shortName, ChatColor color) {
        super(uuid, name, shortName, color);
    }

    public TownChat(JSONObject object){
        super(object);
    }

    @Override
    protected String formatMessage(Player player, Resident sender, String message) {
        return sender.getTown().getRank(sender).getPrefix() + sender.getName() + getColor() + ": " + message;
    }

    @Override
    public boolean canJoin(Resident resident) {
        return resident.getTown() != null;
    }

    @Override
    public boolean canChat(Resident resident) {
        return resident.getTown() != null;
    }
}
