# PopulaceChat
A chat add-on for Populace. Adds basic channel support and town-specific chats for servers running [Populace](https://github.com/Turqmelon/Populace).

**This project is not maintained.** It was created about 3 years ago for an experiment.

## Commands
* `/ch join <Channel>` Join a channel you are not part of
* `/ch leave <Channel>` Leave a channel currently visible to you
* `/ch focus <Channel>` Change your chat focus to a channel
* `/ch list` View all channels you have access to
* `/ch create <Channel> <Short Name>` Create a new chat channel
* `/ch delete <Channel>` Delete a chat channel
* `/ch default <Channel>` Change the default chat channel
* `/ch leaveable <Channel>` Toggle whether or not players can leave a channel
* `/ch slowmode <Channel> <Seconds>` Adjust how long players must wait between sending messages to a channel
* `/ch color <Channel> <Chat Color>` Adjust the color for a channel

## Permissions
* `populace.channels.admin` Administer chat channels
* `populace.join.CHANNEL` Allows users to join and see a channel
* `populace.send.CHANNEL` Allows users to send messages to a channel
* `populace.chat.noslow` Bypass slowmode on chat channels
