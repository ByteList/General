package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.common.AsyncTasks;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.commands.*;
import de.gamechest.verify.bot.listener.ClientJoinListener;
import de.gamechest.verify.bot.listener.ClientLeaveListener;
import de.gamechest.verify.bot.listener.ClientMoveListener;
import de.gamechest.verify.bot.listener.TextMessageListener;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
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
    private final int[] specialIds = { 11, 12, 13, 14, 29, 16, 55 },
            supportNotifyIds = { 13, 14, notifyServerGroupId }, noSupportChannelIds = { 112, 204, 97, 98, 726, 727, 93 };

    private final String noSupportMessage = "[size=14][b][color=RED]Der Support ist geschlossen, da kein supportendes Teammitglied zur Verfügung steht.[/color][/b][/size]\n\n\n";

    private int queryId;
    @Getter
    private final ArrayList<Integer> supportMemberIds = new ArrayList<>();

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


           api.addTS3Listeners(
                   new ClientJoinListener(apiAsync),
                   new ClientLeaveListener(apiAsync),
                   new TextMessageListener(apiAsync, queryId),
                   new ClientMoveListener(apiAsync)
           );

           commandManager = new CommandManager();
           commandManager.registerCommands(
                   new HelpBotCommand(apiAsync),
                   new NoMessageBotCommand(apiAsync),
                   new NoPokeBotCommand(apiAsync),
                   new VerifyBotCommand(apiAsync),
                   new GamesBotCommand(apiAsync),
                   new UnverifyBotCommand(apiAsync),
                   new SupportBotCommand(apiAsync)
           );

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
        for (int serverGroupId : specialIds) {
            if (client.isInServerGroup(serverGroupId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSupportNotifyGroup(Client client) {
        for (int supportNotifyId : supportNotifyIds) {
            if (client.isInServerGroup(supportNotifyId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInNoSupportChannel(Client client) {
        for (int noSupportChannelId : noSupportChannelIds) {
            if (client.getChannelId() == noSupportChannelId) {
                return true;
            }
        }
        return false;
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
        HashMap<ChannelProperty, String> properties = new HashMap<>();
        ArrayList<Client> clientInChannel = new ArrayList<>();
        AtomicInteger hasGroup = new AtomicInteger(0), canSupport = new AtomicInteger(0);

        AsyncTasks.getInstance().runTaskAsync(()-> {
            ChannelInfo channel = this.getApi().getChannelInfo(this.supportWaitChannelId);

            this.getApi().getClients().forEach(client -> {
                if(this.hasSupportNotifyGroup(client)) {
                    if(!this.isInNoSupportChannel(client) || supportMemberIds.contains(client.getId())) {
                        canSupport.addAndGet(1);
                    }
                    hasGroup.addAndGet(1);
                }
                if(client.getChannelId() == this.supportWaitChannelId)
                    clientInChannel.add(client);
            });
            String newTopic = "supportende Teammitglieder: "+canSupport.get()+"/"+hasGroup.get();

            if(canSupport.get() > 0) {
                if(!channel.getName().equals("Support | Warteraum")) {
                    properties.put(ChannelProperty.CHANNEL_MAXCLIENTS, "1");
                    properties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "1");
                    properties.put(ChannelProperty.CHANNEL_NAME, "Support | Warteraum");
                    properties.put(ChannelProperty.CHANNEL_TOPIC, newTopic);
                    properties.put(ChannelProperty.CHANNEL_DESCRIPTION, channel.getDescription().replace(noSupportMessage, ""));
                    this.getApi().editChannel(this.supportWaitChannelId, properties);
                } else if(!channel.getTopic().equals(newTopic)) {
                    properties.put(ChannelProperty.CHANNEL_TOPIC, newTopic);
                    this.getApi().editChannel(this.supportWaitChannelId, properties);
                }
            } else {
                if(!channel.getName().equals("Support | Warteraum [Geschlossen]")) {
                    clientInChannel.forEach(client -> {
                        this.getApiAsync().kickClientFromChannel(client);
                        this.getApiAsync().sendPrivateMessage(client.getId(), "Der Support wurde geschlossen, da kein supportendes Teammitglied zur Verfügung steht.");
                    });
                    properties.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
                    properties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
                    properties.put(ChannelProperty.CHANNEL_NAME, "Support | Warteraum [Geschlossen]");
                    properties.put(ChannelProperty.CHANNEL_TOPIC, newTopic);
                    properties.put(ChannelProperty.CHANNEL_DESCRIPTION, noSupportMessage+channel.getDescription());
                    this.getApi().editChannel(this.supportWaitChannelId, properties);
                } else if(!channel.getTopic().equals(newTopic)) {
                    properties.put(ChannelProperty.CHANNEL_TOPIC, newTopic);
                    this.getApi().editChannel(this.supportWaitChannelId, properties);
                }
            }
        });
    }


    @Override
    public String toString() {
        return "TeamspeakBot{v:"+Verify.getInstance().getVersion()+"}";
    }
}
