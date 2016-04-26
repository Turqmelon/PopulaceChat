package com.turqmelon.PopulaceChat.Listeners;

import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.Populace.Resident.ResidentManager;
import com.turqmelon.Populace.Utils.Msg;
import com.turqmelon.PopulaceChat.Channels.ChannelManager;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Resident resident = ResidentManager.getResident(player);
        if (resident == null){
            return;
        }
        ChatChannel chatChannel = ChannelManager.getDefaultChannel();
        if (chatChannel != null && !chatChannel.getListeners().contains(resident)){
            chatChannel.join(resident);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event){
        if (event.isCancelled())return;
        Player player = event.getPlayer();
        Resident resident = ResidentManager.getResident(player);
        if (resident == null){
            return;
        }

        String key = event.getMessage().substring(1);
        String[] parts = key.split(" ");
        if (parts.length > 0){
            key = parts[0];
        }

        ChatChannel chatChannel = ChannelManager.getChannel(key);
        if (chatChannel != null && !key.equalsIgnoreCase("town")) {
            event.setCancelled(true);
            String message = "";
            for(int i = 1; i < parts.length; i++){
                message = message + " " + parts[i];
            }
            if (message.length() > 0){
                message = message.substring(1);
                chatChannel.chat(player, resident, message);
            }
            else{
                if (ChannelManager.focusChannel(resident, chatChannel)){
                    resident.sendMessage(Msg.OK + "Focused " + chatChannel.getColor() + chatChannel.getName() + "§a.");
                }
                else{
                    resident.sendMessage(Msg.ERR + "You can't focus " + chatChannel.getColor() + chatChannel.getName() + "§c.");
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        if (event.isCancelled())return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        Resident resident = ResidentManager.getResident(player);
        if (resident == null) {
            player.sendMessage(Msg.ERR + "No resident data.");
            return;
        }

        ChatChannel channel = ChannelManager.getFocus(resident);
        if (channel == null){
            player.sendMessage(Msg.ERR + "No target channel.");
            return;
        }

        channel.chat(player, resident, event.getMessage());

    }

}
