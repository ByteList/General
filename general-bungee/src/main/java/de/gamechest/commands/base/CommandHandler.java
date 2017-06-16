package de.gamechest.commands.base;

import de.gamechest.GameChest;
import de.gamechest.commands.*;
import de.gamechest.commands.ban.BanCommand;
import de.gamechest.commands.ban.KickCommand;
import de.gamechest.commands.ban.UnbanCommand;
import de.gamechest.commands.cloud.*;
import de.gamechest.commands.msg.MsgCommand;
import de.gamechest.commands.msg.RCommand;
import de.gamechest.commands.NickListCommand;
import de.gamechest.commands.rank.PremiumCommand;
import de.gamechest.commands.rank.RankCommand;
import de.gamechest.commands.rank.ToggleRankCommand;
import de.gamechest.commands.report.bug.BugInfoCommand;
import de.gamechest.commands.report.bug.BugReportCommand;
import de.gamechest.commands.report.PlayerReportCommand;

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
        registerCommand(new GotoCommand());
        registerCommand(new JoinCommand());
        registerCommand(new ServerCommand());
        registerCommand(new StopCommand());

        registerCommand(new BanCommand());
        registerCommand(new KickCommand());
        registerCommand(new UnbanCommand());

        registerCommand(new NickListCommand());
//        registerCommand(new NickHistoryCommand());

        registerCommand(new PremiumCommand());
        registerCommand(new RankCommand());
        registerCommand(new ToggleRankCommand());

        registerCommand(new MsgCommand());
        registerCommand(new RCommand());

        registerCommand(new CoinsCommand());
        registerCommand(new GcgCommand());
        registerCommand(new HelpCommand());
        registerCommand(new HubCommand());
        registerCommand(new ListCommand());
        registerCommand(new OnlineTimeCommand());
        registerCommand(new PingCommand());
        registerCommand(new StatsCommand());
        registerCommand(new PlayerinfoCommand());
        registerCommand(new VersionCommand());

        registerCommand(new BugReportCommand());
        registerCommand(new BugInfoCommand());
        registerCommand(new PlayerReportCommand());
    }

    /**
     * Unregister all general and cloud commands.
     * This method is only for the GCGeneral#onDisable() method.
     */
    public static void unregisterAllCommands() {
        for(GCCommand command : commands)
            unregisterCommand(command);
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
