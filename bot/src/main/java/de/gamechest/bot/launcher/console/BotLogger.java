package de.gamechest.bot.launcher.console;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.*;

/**
 * Created by ByteList on 27.01.2017.
 */
public class BotLogger extends Logger {

    private static Logger logger;

    public BotLogger(String name) {

        super(name, null);
        setLevel(Level.ALL);

        try {
            File file = new File("logs");
            if (!file.exists()) file.mkdirs();

            LogFormatter logFormatter = new LogFormatter();

            FileHandler fileHandler = new FileHandler("logs/" + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(Calendar.getInstance().getTime()) + ".log");
            fileHandler.setFormatter(logFormatter);
            addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(logFormatter);
            consoleHandler.setLevel(Level.INFO);
            addHandler(consoleHandler);

            setLogger(this);

            System.out.println("[Logger] Loaded!");
        } catch (IOException ex) {
            System.err.println("[Logger] FileLogging failed.");
            ex.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    private static void setLogger(Logger logger) {
        BotLogger.logger = logger;
    }

}
