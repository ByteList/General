package de.gamechest.updater;

import de.gamechest.database.DatabaseManager;
import de.gamechest.updater.console.CommandHandler;
import de.gamechest.updater.console.commands.EndCommand;
import de.gamechest.updater.console.commands.HelpCommand;
import de.gamechest.updater.log.LoggingOutPutStream;
import de.gamechest.updater.log.UpdaterLogger;
import de.gamechest.updater.updaters.JenkinsUpdater;
import de.gamechest.updater.updaters.StatsUpdater;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ByteList on 11.09.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Updater {

    public boolean isRunning;
    @Getter
    private static Updater instance;
    @Getter
    private ConsoleReader consoleReader;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private Logger logger;
    @Getter
    private String version = "unknown";

    private String stopDate;
    @Getter
    private DatabaseManager databaseManager;

    private StatsUpdater statsUpdater;
    private JenkinsUpdater jenkinsUpdater;

    public Updater() throws Exception {
        instance = this;
        isRunning = false;
        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        version = v[0]+":"+v[1].substring(0, 7);
        stopDate = System.getProperty("de.gamechest.updater.autostop", "03:55");

        // This is a workaround for quite possibly the weirdest bug I have ever encountered in my life!
        // When jansi attempts to extract its natives, by default it tries to extract a specific version,
        // using the loading class's implementation version. Normally this works completely fine,
        // however when on Windows certain characters such as - and : can trigger special behaviour.
        // Furthermore this behaviour only occurs in specific combinations due to the parsing done by jansi.
        // For example test-test works fine, but test-test-test does not! In order to avoid this all together but
        // still keep our versions the same as they were, we set the override property to the essentially garbage version
        // ByteCloud. This version is only used when extracting the libraries to their temp folder.
        System.setProperty("library.jansi.version", "Updater");

        AnsiConsole.systemInstall();
        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);

        logger = new UpdaterLogger("Updater", consoleReader);
        System.setErr(new PrintStream(new LoggingOutPutStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutPutStream(logger, Level.INFO), true));

        this.statsUpdater = new StatsUpdater();
        this.jenkinsUpdater = new JenkinsUpdater();

        this.commandHandler = new CommandHandler();

        this.commandHandler.registerCommand(new EndCommand());
        this.commandHandler.registerCommand(new HelpCommand());
    }

    public void start() {
        this.isRunning = true;
        try {
            this.databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            this.databaseManager.init();
            this.logger.info("Connected to database!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        statsUpdater.start();
        jenkinsUpdater.start();
    }

    public void stop() {
        this.isRunning = false;
        new Thread("Shutdown Thread") {

            @Override
            public void run() {
                logger.info("Shutting down...");
                cleanStop();
            }
        }.start();
    }

    private void cleanStop() {
        for (Handler handler : getLogger().getHandlers()) {
            handler.close();
        }
        System.exit( 0 );
    }

    public void startStopThread() {
        if(stopDate.equals("false")) {
            this.logger.info("Auto-Stop is disabled.");
            return;
        }

        new Thread("Auto-Stop Thread") {

            @Override
            public void run() {
                logger.info("Auto-Stop will be executed at "+stopDate+".");

                while (isRunning) {
                    try {
                        Thread.sleep(60000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String date = new SimpleDateFormat("HH:mm").format(new Date());

                    if(date.equals(stopDate)) {
                        logger.info("** Auto-Stop executed at "+stopDate+" **");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Updater.instance.stop();
                    }
                }
            }
        }.start();
    }
}
