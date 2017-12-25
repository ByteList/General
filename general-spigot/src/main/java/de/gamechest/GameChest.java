package de.gamechest;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCPacketClient;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.chatlog.ChatLog;
import de.gamechest.coins.Coins;
import de.gamechest.commands.*;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import de.gamechest.listener.CommandListener;
import de.gamechest.nick.Nick;
import de.gamechest.reflector.PacketInjector;
import de.gamechest.stats.Stats;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 09.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameChest extends JavaPlugin {

    private static final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public HashMap<UUID, Rank> rankCache = new HashMap<>();

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
    private Coins coins;
    @Getter
    private PacketInjector packetInjector;

    public final String prefix = "§2GameChest §8\u00BB ";

    @Getter
    private String version = "unknown";

    @Override
    public void onEnable() {
        instance = this;

        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        initDatabase();

        this.stats = new Stats();
        this.packetInjector = new PacketInjector();
        this.chatLog = new ChatLog();
        this.nick = new Nick();
        this.coins = new Coins();

        if(!Bukkit.getServerName().contains("nonBungee")) {
            GCPacketClient.start();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("packet", "RegisterNewClient");
            if(isCloudEnabled())
                jsonObject.addProperty("serverId", ByteCloudCore.getInstance().getCloudHandler().getServerId());
            else
                jsonObject.addProperty("serverId", getServer().getServerName());
            GCPacketClient.sendPacket(jsonObject);
        }

//        getCommand("chatlog").setExecutor(new ChatlogCommand());
        getCommand("opme").setExecutor(new OpmeCommand());
        getCommand("serverid").setExecutor(new ServerIdCommand());
        getCommand("nick").setExecutor(new NickCommands());
        getCommand("fakeplugins").setExecutor(new FakePluginCommand());

        getServer().getPluginManager().registerEvents(new CommandListener(), this);

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

    private static char randomChar() {
        return POOL[ThreadLocalRandom.current().nextInt(POOL.length)];
    }

    public static String randomKey(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(randomChar());
        }
        return kb.toString();
    }

    public String randomNumber(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return kb.toString();
    }

    public boolean hasRank(UUID uuid, Rank rank) {
        Rank playerRank;
        if(!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            if(dbPlayer.existsPlayer()) {
                playerRank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());
                rankCache.put(uuid, playerRank);
            } else {
                return false;
            }
        } else {
            playerRank = rankCache.get(uuid);
        }

        return playerRank.getId() <= rank.getId();
    }

    public boolean equalsRank(UUID uuid, Rank rank) {
        Rank playerRank;
        if(!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            if(dbPlayer.existsPlayer()) {
                playerRank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());
                rankCache.put(uuid, playerRank);
            } else {
                return false;
            }
        } else {
            playerRank = rankCache.get(uuid);
        }
        return Objects.equals(playerRank.getId(), rank.getId());
    }

    public Rank getRank(UUID uuid) {
        if(!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            Rank rank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());
            rankCache.put(uuid, rank);
            return rank;
        } else {
            return rankCache.get(uuid);
        }
    }

    public boolean isRankToggled(UUID uuid) {
        return false;
    }

    public boolean isCloudEnabled() {
        return getServer().getPluginManager().isPluginEnabled("ByteCloudAPI");
    }


    public String getDisplayname(Player player) {
        Rank rank = getRank(player.getUniqueId());
        if(this.nick.isNicked(player.getUniqueId()))
            rank = Rank.SPIELER;

        return rank.getColor() + player.getName();
    }
}
