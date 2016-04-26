package com.turqmelon.PopulaceChat.Channels;


import com.turqmelon.Populace.Resident.Resident;

import java.util.*;

public class ChannelManager {

    private static List<ChatChannel> channels = new ArrayList<>();
    private static Map<UUID, UUID> focus = new HashMap<>();

    public static ChatChannel getFocus(Resident resident){
        ChatChannel channel = null;
        if (focus.containsKey(resident.getUuid())){
            channel = getChannel(focus.get(resident.getUuid()));
        }
        if (channel == null){
            channel = getDefaultChannel();
        }
        return channel;
    }

    public static Map<UUID, UUID> getFocus() {
        return focus;
    }

    public static boolean focusChannel(Resident resident, ChatChannel chatChannel){
        if (chatChannel.canJoin(resident)){
            chatChannel.join(resident);
            focus.put(resident.getUuid(), chatChannel.getUuid());
            return true;
        }
        return false;
    }

    public static ChatChannel getDefaultChannel(){
        for(ChatChannel chatChannel : getChannels()){
            if (chatChannel.isDefaultChannel()){
                return chatChannel;
            }
        }
        return null;
    }

    public static ChatChannel getChannel(String name){
        for(ChatChannel chatChannel : getChannels()){
            if (chatChannel.getName().equalsIgnoreCase(name)||
                    chatChannel.getShortName().equalsIgnoreCase(name)){
                return chatChannel;
            }
        }
        return null;
    }

    public static ChatChannel getChannel(UUID uuid){
        for(ChatChannel chatChannel : getChannels()){
            if(chatChannel.getUuid().equals(uuid)){
                return chatChannel;
            }
        }
        return null;
    }

    public static List<ChatChannel> getChannels() {
        return channels;
    }
}
