package com.turqmelon.PopulaceChat.Commands;

import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.Populace.Resident.ResidentManager;
import com.turqmelon.Populace.Utils.Msg;
import com.turqmelon.PopulaceChat.Channels.ChannelManager;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import com.turqmelon.PopulaceChat.Channels.custom.CustomChannel;
import com.turqmelon.PopulaceChat.PopulaceChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player)sender;
        Resident resident = ResidentManager.getResident(player);
        if (resident == null){
            sender.sendMessage(Msg.ERR + "No resident data.");
            return true;
        }

        if (args.length == 0){
            sendHelp(sender);
        }
        else{
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("join") && args.length == 2){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    chatChannel.join(resident);
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else if (cmd.equalsIgnoreCase("leave") && args.length == 2){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    chatChannel.leave(resident);
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else if (cmd.equalsIgnoreCase("focus") && args.length == 2){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    if (ChannelManager.focusChannel(resident, chatChannel)){
                        sender.sendMessage(Msg.OK + "Focused " + chatChannel.getColor() + chatChannel.getName() + ".");
                    }
                    else{
                        sender.sendMessage(Msg.ERR + "You can't focus " + chatChannel.getColor() + chatChannel.getName() + "§c.");
                    }
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else if (cmd.equalsIgnoreCase("list")){
                List<String> channels = new ArrayList<>();
                for(ChatChannel channel : ChannelManager.getChannels()){
                    if (channel.canJoin(resident)){
                        channels.add(channel.getColor() + channel.getName() + "§8");
                    }
                }
                sender.sendMessage(Msg.INFO + "There are " + channels.size() + " channels: §f" + channels.toString().replace("[", "").replace("]", ""));
            }
            else if (cmd.equalsIgnoreCase("create") && args.length == 4 && sender.hasPermission("populace.channels.admin")){
                String name = args[1];
                String shortName = args[2];
                try {
                    ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
                    if (ChannelManager.getChannel(name) == null && ChannelManager.getChannel(shortName) == null){
                        ChatChannel channel = new CustomChannel(UUID.randomUUID(), name, shortName, color);
                        ChannelManager.getChannels().add(channel);
                        if (ChannelManager.getChannels().size() == 1){
                            channel.setDefaultChannel(true);
                        }
                        sender.sendMessage(Msg.OK + "Channel created.");
                        ChannelManager.focusChannel(resident, channel);
                        ChatChannel chatChannel = ChannelManager.getDefaultChannel();
                        for(Player p : Bukkit.getOnlinePlayers()){
                            Resident r = ResidentManager.getResident(p);
                            if (r == null)continue;
                            if (chatChannel != null && !chatChannel.getListeners().contains(r)){
                                chatChannel.join(r);
                            }
                        }

                        PopulaceChat.saveData();

                    }
                    else{
                        sender.sendMessage(Msg.ERR + "Name or shortname taken.");
                    }
                }catch (Exception ex){
                    sender.sendMessage(Msg.ERR + ex.getMessage());
                }
            }
            else if (cmd.equalsIgnoreCase("delete") && args.length == 2 && sender.hasPermission("populace.channels.admin")){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    ChannelManager.getChannels().remove(chatChannel);
                    try {
                        PopulaceChat.saveData();
                    } catch (IOException e) {
                        sender.sendMessage(Msg.ERR + e.getMessage());
                    }
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else if (cmd.equalsIgnoreCase("default") && args.length == 2 && sender.hasPermission("populace.channels.admin")){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    for(ChatChannel channel : ChannelManager.getChannels()){
                        channel.setDefaultChannel(false);
                    }
                    chatChannel.setDefaultChannel(true);
                    sender.sendMessage(Msg.OK + "Default channel updated.");
                    try {
                        PopulaceChat.saveData();
                    } catch (IOException e) {
                        sender.sendMessage(Msg.ERR + e.getMessage());
                    }
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else if (cmd.equalsIgnoreCase("leaveable") && args.length == 2 && sender.hasPermission("populace.channels.admin")){
                ChatChannel chatChannel = ChannelManager.getChannel(args[1]);
                if (chatChannel != null){
                    if (chatChannel.isDefaultChannel()){
                        sender.sendMessage(Msg.ERR + "The default channel is never leaveable.");
                        return true;
                    }
                    chatChannel.setLeaveable(!chatChannel.isLeaveable());
                    sender.sendMessage(Msg.OK + "Leaveable status updated: " + chatChannel.isLeaveable());
                    try {
                        PopulaceChat.saveData();
                    } catch (IOException e) {
                        sender.sendMessage(Msg.ERR + e.getMessage());
                    }
                }
                else{
                    sender.sendMessage(Msg.ERR + args[1] + " doesn't exist.");
                }
            }
            else{
                sendHelp(sender);
            }
        }

        return true;
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(Msg.INFO + "§nChat Commands");
        sender.sendMessage(Msg.INFO + "/ch join §f<Channel> §7§o(or /<Channel>)");
        sender.sendMessage(Msg.INFO + "/ch leave §f<Channel>");
        sender.sendMessage(Msg.INFO + "/ch focus §f<Channel> §7§o(or /<Channel>)");
        sender.sendMessage(Msg.INFO + "/ch list");
        if (sender.hasPermission("populace.channels.admin")){
            sender.sendMessage(Msg.INFO + "/ch create §f<Name> <Short Name> <Color>");
            sender.sendMessage(Msg.INFO + "/ch delete §f<Channel>");
            sender.sendMessage(Msg.INFO + "/ch default §f<Channel>");
            sender.sendMessage(Msg.INFO + "/ch leaveable §f<Channel>");
        }
    }
}
