package de.gamechest.bot.launcher;

import de.gamechest.bot.DiscordBot;
import de.gamechest.bot.TeamspeakBot;
import de.gamechest.bot.launcher.console.BotLogger;
import de.gamechest.bot.launcher.console.CommandReader;
import de.gamechest.bot.launcher.console.LoggingOutPutStream;
import de.gamechest.database.DatabaseManager;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;

import static de.gamechest.bot.launcher.console.BotLogger.getLogger;

/**
 * Created by ByteList on 01.05.2017.
 */
public class BotLauncher {

    public static boolean isRunning;

    @Getter
    private static ConsoleReader consoleReader;
    @Getter
    private static DiscordBot discordBot;
    @Getter
    private static TeamspeakBot teamspeakBot;

    @Getter
    private static DatabaseManager databaseManager;

    public static void main(String[] args) throws IOException {
        isRunning = true;
        System.setProperty("library.jansi.version", "ChestBot");
        System.out.println("Java-Version: "+System.getProperty( "java.class.version" ));

        try {
            AnsiConsole.systemInstall();
            consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents(false);
            consoleReader.setPrompt(">");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BotLogger botLogger = new BotLogger("ChestBot");
        System.setErr(new PrintStream(new LoggingOutPutStream(botLogger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutPutStream(botLogger, Level.INFO), true));

        try {
            botLogger.info("Connecting to database...");
            databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            databaseManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            botLogger.info("Database connected!");
        }

        botLogger.info("Starting bots...");

        discordBot = new DiscordBot();
        teamspeakBot = new TeamspeakBot();

        String line;
        while (BotLauncher.isRunning && (line = consoleReader.readLine(">")) != null) {
            CommandReader.execute(line);
        }
    }

    public static void shutdown() {
        isRunning = false;
        new Thread(() -> {
//            discordBot.sendMessage("System", "```Bot disconnected!```");
            if(teamspeakBot.logout()) {
                BotLogger.getLogger().info("[Teamspeak] Bot disconnected!");
            } else {
                BotLogger.getLogger().info("[Teamspeak] Bot can not disconnect!");
            }
            try {
                Thread.sleep(3700L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // closing all handlers after last message
            for(Handler handler : getLogger().getHandlers()) handler.close();
            System.exit(0);
        }, "Shutdown-Thread").start();
    }

    public static String getAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "*error*";
    }
}
