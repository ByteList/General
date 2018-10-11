package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.AsyncTasks;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.commands.*;
import de.gamechest.verify.bot.listener.ClientJoinListener;
import de.gamechest.verify.bot.listener.ClientLeaveListener;
import de.gamechest.verify.bot.listener.TextMessageListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Created by ByteList on 01.05.2017.
 */
public class TeamspeakBot {

    public final int
            noPokeServerGroupId = 53,
            noMessageServerGroupId = 54,
            notifyServerGroupId = 46,
            verifyServerGroupId = 55,
            supportWaitChannelId = 99;
    @Getter
    private final int[] specialIds = {11, 12, 13, 14, 29, 16, 55}, supportNotifyIds = { 13, 14, notifyServerGroupId };

    private int queryId;

    @Getter
    private TS3Query query;
    @Getter
    private TS3Api api;
    @Getter
    private TS3ApiAsync apiAsync;
    @Getter
    private CommandManager commandManager;
    @Getter
    private AtomicInteger specialUsersOnline;

    public TeamspeakBot() {
        init();
    }

    private void init() {
       Bukkit.getScheduler().runTaskAsynchronously(Verify.getInstance(), ()-> {
           System.out.println("[Teamspeak] Try to connect...");

           TS3Config config = new TS3Config();
           config.setHost("127.0.0.1");
           config.setQueryPort(10011);
           config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
           config.setDebugLevel(Level.WARNING);
//           config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff())
           query = new TS3Query(config);
           query.connect();

           api = query.getApi();
           apiAsync = query.getAsyncApi();
           api.login("teamspeakQueryBot", "hg63Afdp");

           if(api.whoAmI() == null) {
               System.out.println("[Teamspeak] Query can not connect!");
               query.exit();
               return;
           } else {
               System.out.println("[Teamspeak] Query connected!");
           }


           api.selectVirtualServerById(1);
           queryId = api.whoAmI().getId();
           api.registerEvents(TS3EventType.SERVER, TS3EventType.TEXT_PRIVATE/*, TS3EventType.CHANNEL*/);


           api.addTS3Listeners(new ClientJoinListener(apiAsync), new ClientLeaveListener(apiAsync), new TextMessageListener(apiAsync, queryId));

           commandManager = new CommandManager();
           commandManager.registerCommands(new HelpBotCommand(apiAsync), new NoMessageBotCommand(apiAsync), new NoPokeBotCommand(apiAsync),
                   new VerifyBotCommand(apiAsync), new GamesBotCommand(apiAsync), new UnverifyBotCommand(apiAsync));

           this.specialUsersOnline = new AtomicInteger(0);
           api.setNickname("ChestBot");

           Bukkit.getScheduler().scheduleSyncRepeatingTask(Verify.getInstance(), this::checkSupport, 0L, 40L);
       });
    }


    public ClientInfo getClientInfo(int invokerId) {
        return api.getClientInfo(invokerId);
    }

    public void getClientInfoAsync(int invokerId, BotCallback<ClientInfo> callbackSuccess, BotCallback<TS3Exception> callbackFailure) {
        apiAsync.getClientInfo(invokerId).onSuccess(callbackSuccess::run).onFailure(callbackFailure::run);
    }

    public boolean isVerified(int invokerId) {
        return api.getClientInfo(invokerId).isInServerGroup(verifyServerGroupId);
    }

    public boolean hasSpecialGroup(Client client) {
        boolean b = false;
        for (int serverGroupId : specialIds) {
            if (client.isInServerGroup(serverGroupId)) {
                b = true;
                break;
            }
        }
        return b;
    }

    public boolean hasSupportNotifyGroup(Client client) {
        boolean b = false;
        for (int supportNotifyId : supportNotifyIds) {
            if (client.isInServerGroup(supportNotifyId)) {
                b = true;
                break;
            }
        }
        return b;
    }

    public void checkChannelPassword(int channelId, int invokerId, String name, String password) {
        if(name.contains(password)) {
            System.out.println(name + " :: " + password);
            apiAsync.sendPrivateMessage(invokerId, "Der Channel-Name darf das Channel-Passwort nicht beinhalten!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Verify.getInstance(), ()-> {
                HashMap<ChannelProperty, String> properties = new HashMap<>();
                properties.put(ChannelProperty.CHANNEL_NAME, name.replace(password, ""));
                api.editChannel(channelId, properties);
            }, 25L);
        }
    }

    public void checkSupport() {
        AtomicInteger i = new AtomicInteger(0);
        AsyncTasks.getInstance().runTaskAsync(()-> {
            ChannelInfo channel = this.getApi().getChannelInfo(this.supportWaitChannelId);

            this.getApi().getClients().forEach(client -> {
                if(this.hasSupportNotifyGroup(client)) {
                    i.decrementAndGet();
                }
            });

            if(i.get() > 0) {
                channel.getMap().put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED.getName(), "true");
                channel.getMap().put(ChannelProperty.CHANNEL_NAME.getName(), "Support | Warteraum");
            } else {
                channel.getMap().put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED.getName(), "false");
                channel.getMap().put(ChannelProperty.CHANNEL_NAME.getName(), "Support | Warteraum [Geschlossen]");
            }
        });
    }


    @Override
    public String toString() {
        return "TeamspeakBot{v:"+Verify.getInstance().getVersion()+"}";
    }
}
