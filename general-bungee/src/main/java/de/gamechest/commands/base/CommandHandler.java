package de.gamechest.commands.base;

import de.gamechest.GameChest;
import de.gamechest.commands.*;
import de.gamechest.commands.ban.BanCommand;
import de.gamechest.commands.ban.KickCommand;
import de.gamechest.commands.ban.UnbanCommand;
import de.gamechest.commands.cloud.GotoCommand;
import de.gamechest.commands.cloud.JoinCommand;
import de.gamechest.commands.cloud.ServerCommand;
import de.gamechest.commands.cloud.StopCommand;
import de.gamechest.commands.msg.MsgCommand;
import de.gamechest.commands.msg.RCommand;
import de.gamechest.commands.party.PartyChatCommand;
import de.gamechest.commands.party.PartyCommand;
import de.gamechest.commands.rank.PremiumCommand;
import de.gamechest.commands.rank.RankCommand;
import de.gamechest.commands.report.PlayerReportCommand;
import de.gamechest.commands.report.bug.BugInfoCommand;
import de.gamechest.commands.report.bug.BugReportCommand;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ByteList on 06.03.2017.
 */
public class CommandHandler {
    
    private static List<GCCommand> commands = new LinkedList<>();

    private static final GameChest gameChest = GameChest.getInstance();
    
    /**
     * Register all general and cloud commands.
     * This method is only for the GCGeneral#onEnable() method.
     */
    public static void registerAllCommands() {

        GCCommand[] commands = {
                new GotoCommand(),
                new JoinCommand(),
                new ServerCommand(),
                new StopCommand(),

                new BanCommand(),
                new KickCommand(),
                new UnbanCommand(),

                new NickListCommand(),
        //        new NickHistoryCommand(),

                new PremiumCommand(),
                new RankCommand(),
                new ActivateCommand(),

                new MsgCommand(),
                new RCommand(),
                new MeCommand(),

                new TeamchatCommand(),

                new PartyCommand(),
                new PartyChatCommand(),

                new CoinsCommand(),
                new GcgCommand(),
                new HelpCommand(),
                new HubCommand(),
                new ListCommand(),
                new OnlineTimeCommand(),
                new PingCommand(),
                new StatsCommand(),
                new PlayerinfoCommand(),
                new VersionCommand(),

                new BugReportCommand(),
                new BugInfoCommand(),
                new PlayerReportCommand()
        };

        for(GCCommand command : commands) registerCommand(command);
    }

    /**
     * With the registerCommand() method you can register a command.
     * You need only the command base GCCommand!
     * @param command
     * @return  if command successfully registered: true
     *          else: false
     */
    public static boolean registerCommand(GCCommand command) {
        if(commands.contains(command)) {
            gameChest.getLogger().warning("[GCG/CommandHandler] Command "+command.getName()+" already registered!");
            return false;
        }
        gameChest.getProxy().getPluginManager().registerCommand(gameChest, command);
        commands.add(command);
        return true;
    }

    /**
     * With the unregisterCommand() method you can unregister a command.
     * You need only the command base GCCommand!
     * @param command
     * @return  if command successfully unregistered: true
     *          else: false
     */
    public static boolean unregisterCommand(GCCommand command) {
        if(!commands.contains(command)) {
            gameChest.getLogger().warning("[GCG/CommandHandler] Command "+command.getName()+" is not registered!");
            return false;
        }
        gameChest.getProxy().getPluginManager().unregisterCommand(command);
        commands.remove(command);
        return true;
    }
}
