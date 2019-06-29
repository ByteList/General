package de.gamechest;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.GCPacketClient;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.chatlog.ChatLog;
import de.gamechest.coins.Coins;
import de.gamechest.commands.*;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.spigot.SpigotChest;
import de.gamechest.common.spigot.SpigotChestNick;
import de.gamechest.common.spigot.SpigotChestPlugin;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.fakeplayer.FakePlayerManager;
import de.gamechest.listener.CommandListener;
import de.gamechest.listener.JoinListener;
import de.gamechest.listener.PlayerStatisticIncrementListener;
import de.gamechest.listener.QuitListener;
import de.gamechest.nick.Nick;
import de.gamechest.reflector.PacketInjector;
import de.gamechest.stats.Stats;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 09.04.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameChest extends JavaPlugin implements SpigotChestPlugin {

    private static final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public HashMap<UUID, Rank> rankCache = new HashMap<>();

    @Getter
    private static GameChest instance;

    @Getter
    private YamlConfiguration configuration;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private Stats stats;
    @Getter
    private ChatLog chatLog;
    @Getter
    private SpigotChestNick nick;
    @Getter
    private Coins coins;
    @Getter
    private PacketInjector packetInjector;
    @Getter
    private FakePlayerManager fakePlayerManager;

    @Getter
    private String version = "unknown";

    @Override
    public void onEnable() {
        SpigotChest.setInstance(instance = this);

        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        this.version = v[0] + ":" + v[1].substring(0, 7);

        this.initConfig();
        this.initDatabase();

        this.stats = new Stats();
        this.packetInjector = new PacketInjector();
        this.chatLog = new ChatLog();
        this.nick = new Nick();
        this.coins = new Coins();
        this.fakePlayerManager = new FakePlayerManager();

        if (!Bukkit.getServerName().contains("nonBungee")) {
            GCPacketClient.start();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("packet", "RegisterNewClient");
            if (isCloudEnabled())
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
        getCommand("website").setExecutor(new WebsiteCommand());

        Listener[] listeners = {
                new JoinListener(),
                new QuitListener(),
                new CommandListener(),
                new PlayerStatisticIncrementListener()
        };

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        if(this.isCloudEnabled()) {
            ByteCloudCore.getInstance().setPermissionCheck(new PermissionCheck());
        }

        getServer().getConsoleSender().sendMessage(ChestPrefix.PREFIX + "§aEnabled!");
    }

    @Override
    public void onDisable() {

        getServer().getConsoleSender().sendMessage(ChestPrefix.PREFIX + "§cDisabled!");
    }

    private void initConfig() {
        File file = new File(this.getDataFolder(), "config.yml");

        this.getDataFolder().mkdirs();
        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration.options().copyDefaults(true);
        this.configuration.setDefaults(YamlConfiguration.loadConfiguration(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.yml"), Charsets.UTF_8)));
        try {
            this.configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        try {
            this.databaseManager = new DatabaseManager(this.configuration.getString("database.mongodb.host"),
                    this.configuration.getInt("database.mongodb.port"), this.configuration.getString("database.mongodb.user"),
                    this.configuration.getString("database.mongodb.password"), this.configuration.getString("database.mongodb.database"));
            this.databaseManager.init();
            getServer().getConsoleSender().sendMessage(ChestPrefix.PREFIX + "§eDatabase - §aConnected!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private char randomChar() {
        return POOL[ThreadLocalRandom.current().nextInt(POOL.length)];
    }

    @Override
    public String randomKey(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(randomChar());
        }
        return kb.toString();
    }

    @Override
    public String randomNumber(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return kb.toString();
    }

    @Override
    public boolean hasRank(UUID uuid, Rank rank) {
        return getRank(uuid).getId() <= rank.getId();
    }

    @Override
    public boolean equalsRank(UUID uuid, Rank rank) {
        return Objects.equals(getRank(uuid).getId(), rank.getId());
    }

    @Override
    public Rank getRank(UUID uuid) {
        if (!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            Rank rank = Rank.getRankById(dbPlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());
            rankCache.put(uuid, rank);
            return rank;
        } else {
            return rankCache.get(uuid);
        }
    }

    @Override
    public boolean isRankToggled(UUID uuid) {
        return false;
    }

    @Override
    public boolean isCloudEnabled() {
        return getServer().getPluginManager().isPluginEnabled("ByteCloudAPI");
    }

    @Override
    public String getPlayername(UUID uuid) {
        DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid, DatabasePlayerObject.LAST_NAME);
        if(dbPlayer.existsPlayer()) {
            return dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
        }
        return null;
    }

//    public void addPlayerToCloudServer(Player player) {
//        if (isCloudEnabled()) {
//            if(this.nick.isNicked(player.getUniqueId()))
//                ByteCloudCore.getInstance().getCloudAPI().addPlayer(player.getCustomName());
//            else
//                ByteCloudCore.getInstance().getCloudAPI().addPlayer(player.getName());
//        }
//    }
//
//    public void removePlayerFromCloudServer(Player player) {
//        if (isCloudEnabled()) {
//            if(this.nick.isNicked(player.getUniqueId()))
//                ByteCloudCore.getInstance().getCloudAPI().removePlayer(player.getCustomName());
//            else
//                ByteCloudCore.getInstance().getCloudAPI().removePlayer(player.getName());
//        }
//    }
//
//    public void addSpectatorToCloudServer(Player player) {
//        if (isCloudEnabled()) {
//            if(this.nick.isNicked(player.getUniqueId()))
//                ByteCloudCore.getInstance().getCloudAPI().addSpectator(player.getCustomName());
//            else
//                ByteCloudCore.getInstance().getCloudAPI().addSpectator(player.getName());
//        }
//    }
//
//    public void removeSpectatorFromCloudServer(Player player) {
//        if (isCloudEnabled()) {
//            if(this.nick.isNicked(player.getUniqueId()))
//                ByteCloudCore.getInstance().getCloudAPI().removeSpectator(player.getCustomName());
//            else
//                ByteCloudCore.getInstance().getCloudAPI().removeSpectator(player.getName());
//        }
//    }

    @Override
    public String getDisplayname(Player player) {
        Rank rank = getRank(player.getUniqueId());
        if (this.nick.isNicked(player.getUniqueId()))
            rank = Rank.SPIELER;

        return rank.getColor() + player.getName();
    }

    @Override
    public void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
    }

    @Override
    public void setMetadata(Metadatable object, String key, Object value, Plugin plugin) {
        object.setMetadata(key, new FixedMetadataValue(plugin,value));
    }

    @Override
    public Object getMetadata(Metadatable object, String key, Plugin plugin) {
        List<MetadataValue> values = object.getMetadata(key);
        for (MetadataValue value : values) {
            // Plugins are singleton objects, so using == is safe here
            if (value.getOwningPlugin() == plugin) {
                return value.value();
            }
        }
        return null;
    }
}
