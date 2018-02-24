package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.*;
import de.gamechest.verify.Verify;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class BotListener implements TS3Listener {

    protected final TS3ApiAsync apiAsync;

    public BotListener() {
        apiAsync = Verify.getInstance().getTeamspeakBot().getApiAsync();
    }

    @Override
    public void onTextMessage(TextMessageEvent textMessageEvent) {
    }

    @Override
    public void onClientJoin(ClientJoinEvent clientJoinEvent) {
    }

    @Override
    public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
    }

    @Override
    public void onServerEdit(ServerEditedEvent serverEditedEvent) {
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent channelEditedEvent) {
    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {
    }

    @Override
    public void onClientMoved(ClientMovedEvent clientMovedEvent) {
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent channelCreateEvent) {
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent channelMovedEvent) {
    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {
    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {
    }
}
