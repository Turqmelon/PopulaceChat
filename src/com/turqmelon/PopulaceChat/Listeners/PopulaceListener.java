package com.turqmelon.PopulaceChat.Listeners;

import com.turqmelon.Populace.Events.Resident.ResidentJoinTownEvent;
import com.turqmelon.Populace.Events.Resident.ResidentLeaveTownEvent;
import com.turqmelon.Populace.Events.Town.TownCreationEvent;
import com.turqmelon.Populace.Resident.Resident;
import com.turqmelon.PopulaceChat.Channels.ChannelManager;
import com.turqmelon.PopulaceChat.Channels.ChatChannel;
import com.turqmelon.PopulaceChat.Channels.core.TownChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/******************************************************************************
 * *
 * CONFIDENTIAL                                                               *
 * __________________                                                         *
 * *
 * [2012 - 2016] Devon "Turqmelon" Thome                                      *
 * All Rights Reserved.                                                      *
 * *
 * NOTICE:  All information contained herein is, and remains                  *
 * the property of Turqmelon and its suppliers,                               *
 * if any.  The intellectual and technical concepts contained                 *
 * herein are proprietary to Turqmelon and its suppliers and                  *
 * may be covered by U.S. and Foreign Patents,                                *
 * patents in process, and are protected by trade secret or copyright law.    *
 * Dissemination of this information or reproduction of this material         *
 * is strictly forbidden unless prior written permission is obtained          *
 * from Turqmelon.                                                            *
 * *
 ******************************************************************************/
public class PopulaceListener implements Listener {

    @EventHandler
    public void onCreate(TownCreationEvent event) {
        Resident resident = event.getMayor();
        TownChat chat = null;
        for (ChatChannel channel : ChannelManager.getChannels()) {
            if ((channel instanceof TownChat)) {
                chat = (TownChat) channel;
                break;
            }
        }
        if (chat != null) {
            ChannelManager.focusChannel(resident, chat);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownJoin(ResidentJoinTownEvent event) {
        if (event.isCancelled()) return;
        Resident resident = event.getResident();
        TownChat chat = null;
        for (ChatChannel channel : ChannelManager.getChannels()) {
            if ((channel instanceof TownChat)) {
                chat = (TownChat) channel;
                break;
            }
        }
        if (chat != null) {
            ChannelManager.focusChannel(resident, chat);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownLeave(ResidentLeaveTownEvent event) {
        if (event.isCancelled()) return;
        Resident resident = event.getResident();
        ChatChannel focus = ChannelManager.getFocus(resident);
        if (focus != null && (focus instanceof TownChat)) {
            ChannelManager.focusChannel(resident, ChannelManager.getDefaultChannel());
        }
    }

}
