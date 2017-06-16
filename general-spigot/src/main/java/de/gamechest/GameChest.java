package de.gamechest;

import de.gamechest.chatlog.ChatLog;
import de.gamechest.commands.ChatlogCommand;
import de.gamechest.commands.NickCommands;
import de.gamechest.commands.OpmeCommand;
import de.gamechest.commands.ServerIdCommand;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import de.gamechest.nick.Nick;
import de.gamechest.reflector.PacketInjector;
import de.gamechest.stats.Stats;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Created by ByteList on 09.04.2017.
 */
public class GameChest extends JavaPlugin {

    private static final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random rnd = new Random();
    private static char randomChar()
    {
        return POOL[rnd.nextInt(POOL.length)];
    }
    public static String randomKey(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(randomChar());
        }
        return kb.toString();
    }

    @Getter
    private static GameChest instance;

    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private Stats stats;
    @Getter
    private ChatLog chatLog;
    @Getter
    private Nick nick;
    @Getter
    private PacketInjector packetInjector;

    public final String prefix = "§2GameChest §8\u00BB ";

    @Override
    public void onEnable() {
        instance = this;

        initDatabase();

        this.stats = new Stats();
        this.packetInjector = new PacketInjector();
        this.chatLog = new ChatLog();
        this.nick = new Nick();

//        getServer().getPluginManager().registerEvents(new JoinListener(), this);
//        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        getCommand("chatlog").setExecutor(new ChatlogCommand());
        getCommand("opme").setExecutor(new OpmeCommand());
        getCommand("serverid").setExecutor(new ServerIdCommand());
        getCommand("nick").setExecutor(new NickCommands());

        getServer().getConsoleSender().sendMessage(prefix+"§aEnabled!");
    }

    @Override
    public void onDisable() {

        getServer().getConsoleSender().sendMessage(prefix+"§cDisabled!");
    }

    private void initDatabase() {
        try {
            this.databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            this.databaseManager.init();
            getServer().getConsoleSender().sendMessage(prefix+"§eDatabase - §aConnected!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasRank(UUID uuid, Rank rank) {
        DatabasePlayer databasePlayer = databaseManager.getDatabasePlayer(uuid);
        return databasePlayer.existsPlayer() && databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt() <= rank.getId();
    }

    public boolean equalsRank(UUID uuid, Rank rank) {
        DatabasePlayer databasePlayer = databaseManager.getDatabasePlayer(uuid);
        return databasePlayer.existsPlayer() && Objects.equals(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt(), rank.getId());
    }

}
