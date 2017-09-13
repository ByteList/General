package de.gamechest.updater;

import de.gamechest.database.DatabaseManager;
import de.gamechest.database.stats.clickattack.DatabaseClickAttack;
import de.gamechest.database.stats.clickattack.DatabaseClickAttackObject;
import de.gamechest.database.stats.deathrun.DatabaseDeathRun;
import de.gamechest.database.stats.deathrun.DatabaseDeathRunObject;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuell;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuellObject;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefence;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefenceObject;
import de.gamechest.updater.console.CommandHandler;
import de.gamechest.updater.console.commands.EndCommand;
import de.gamechest.updater.console.commands.HelpCommand;
import de.gamechest.updater.log.LoggingOutPutStream;
import de.gamechest.updater.log.UpdaterLogger;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private final String version = "1.0";

    private String stopDate;

    private DatabaseManager databaseManager;

    public Updater() throws Exception {
        instance = this;
        isRunning = false;
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
        DatabaseClickAttack databaseClickAttack = this.databaseManager.getDatabaseClickAttack();
        DatabaseShulkerDefence databaseShulkerDefence = this.databaseManager.getDatabaseShulkerDefence();
        DatabaseDeathRun databaseDeathRun = this.databaseManager.getDatabaseDeathRun();
        DatabaseJumpDuell databaseJumpDuell = this.databaseManager.getDatabaseJumpDuell();
        new Thread("Updater Thread") {
            @Override
            public void run() {
                while (isRunning) {
                    // ClickAttack
                    ArrayList<UUID> players = new ArrayList<>(databaseClickAttack.getPlayers());

                    players.sort((o1, o2) -> {
                        Long point1 = databaseClickAttack.getDatabaseElement(o1, DatabaseClickAttackObject.POINTS).getAsLong(); // 500
                        Long point2 = databaseClickAttack.getDatabaseElement(o2, DatabaseClickAttackObject.POINTS).getAsLong(); // 600

                        return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
                    });

                    int rank = 0;

                    for(UUID uuid : players) {
                        rank++;
                        databaseClickAttack.setDatabaseObject(uuid, DatabaseClickAttackObject.RANK, rank);
                    }
                    logger.info("Updated ClickAttack stats!");

                    // ShulkerDefence
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    players = new ArrayList<>(databaseShulkerDefence.getPlayers());

                    players.sort((o1, o2) -> {
                        Long point1 = databaseShulkerDefence.getDatabaseElement(o1, DatabaseShulkerDefenceObject.POINTS).getAsLong(); // 500
                        Long point2 = databaseShulkerDefence.getDatabaseElement(o2, DatabaseShulkerDefenceObject.POINTS).getAsLong(); // 600

                        return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
                    });

                    rank = 0;

                    for(UUID uuid : players) {
                        rank++;
                        databaseShulkerDefence.setDatabaseObject(uuid, DatabaseShulkerDefenceObject.RANK, rank);
                    }
                    logger.info("Updated ShulkerDefence stats!");

                    // DeathRun
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    players = new ArrayList<>(databaseDeathRun.getPlayers());

                    players.sort((o1, o2) -> {
                        Long point1 = databaseDeathRun.getDatabaseElement(o1, DatabaseDeathRunObject.POINTS).getAsLong(); // 500
                        Long point2 = databaseDeathRun.getDatabaseElement(o2, DatabaseDeathRunObject.POINTS).getAsLong(); // 600

                        return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
                    });

                    rank = 0;

                    for(UUID uuid : players) {
                        rank++;
                        databaseDeathRun.setDatabaseObject(uuid, DatabaseDeathRunObject.RANK, rank);
                    }
                    logger.info("Updated DeathRun stats!");

                    // JumpDuell
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    players = new ArrayList<>(databaseJumpDuell.getPlayers());

                    players.sort((o1, o2) -> {
                        Long point1 = databaseJumpDuell.getDatabaseElement(o1, DatabaseJumpDuellObject.POINTS).getAsLong(); // 500
                        Long point2 = databaseJumpDuell.getDatabaseElement(o2, DatabaseJumpDuellObject.POINTS).getAsLong(); // 600

                        return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
                    });

                    rank = 0;

                    for(UUID uuid : players) {
                        rank++;
                        databaseJumpDuell.setDatabaseObject(uuid, DatabaseJumpDuellObject.RANK, rank);
                    }
                    logger.info("Updated JumpDuell stats!");

                    try {
                        logger.info("Waiting 30 minutes for next update...");
                        Thread.sleep(60000L*30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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
