package de.gamechest.verify.bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.commands.*;
import de.gamechest.verify.bot.listener.ClientJoinListener;
import de.gamechest.verify.bot.listener.TextMessageListener;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by ByteList on 01.05.2017.
 */
public class TeamspeakBot {

    public final int
            noPokeServerGroupId = 53,
            noMessageServerGroupId = 54,
            notifyServerGroupId = 46,
            verifyServerGroupId = 55;
    @Getter
    private final int[] specialIds = {11, 12, 13, 14, 29, 16, 55};

    private int queryId;

    @Getter
    private TS3Query query;
    @Getter
    private TS3Api api;
    @Getter
    private TS3ApiAsync apiAsync;
    @Getter
    private CommandManager commandManager;

    public TeamspeakBot() {
        init();
    }

    private void init() {
       Bukkit.getScheduler().runTaskAsynchronously(Verify.getInstance(), ()-> {
           System.out.println("[Teamspeak] Try to connect...");

//           TS3Config config = new TS3Config();
//           config.setHost("127.0.0.1");
//           config.setQueryPort(10011);
//           config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
//           config.setDebugLevel(Level.WARNING);
////           config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff())
           query = new TS3Query();
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
           api.setNickname("ChestBot");
           queryId = api.whoAmI().getId();
           api.registerEvents(TS3EventType.SERVER, TS3EventType.TEXT_PRIVATE/*, TS3EventType.CHANNEL*/);

           if(api.getClientInfo(20) != null) apiAsync.sendPrivateMessage(20, "Bot started. (Version: "+Verify.getInstance().getVersion()+")");

           api.addTS3Listeners(new ClientJoinListener(apiAsync), new TextMessageListener(apiAsync, queryId));

           commandManager = new CommandManager();
           commandManager.registerCommands(new HelpBotCommand(apiAsync), new NoMessageBotCommand(apiAsync), new NoPokeBotCommand(apiAsync),
                   new VerifyBotCommand(apiAsync), new GamesBotCommand(apiAsync));
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

    public boolean hasSpecialGroup(ClientInfo clientInfo) {
        boolean b = false;
        System.out.println("Bool a: "+ false);
        for (int serverGroupId : specialIds) {
            System.out.println("sgId: "+serverGroupId);
            if (clientInfo.isInServerGroup(serverGroupId)) {
                b = true;
                break;
            }
        }
        System.out.println("Bool b: "+b);
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

    @Override
    public String toString() {
        return "TeamspeakBot{v:"+Verify.getInstance().getVersion()+"}";
    }
}
