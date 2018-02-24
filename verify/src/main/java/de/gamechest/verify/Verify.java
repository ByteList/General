package de.gamechest.verify;

import de.gamechest.verify.bot.TeamspeakBot;
import de.gamechest.verify.commands.UnverifyCommand;
import de.gamechest.verify.listener.Listeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by ByteList on 29.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Verify extends JavaPlugin {

    @Getter
    private static Verify instance;
    @Getter
    private String version = "unknown";

    @Getter
    private TeamspeakBot teamspeakBot;

    public final String prefix = "§6Verify §8\u00BB ";

    @Override
    public void onEnable() {
        instance = this;
        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        this.teamspeakBot = new TeamspeakBot();

        for(World world : Bukkit.getWorlds()) {
            world.setStorm(false);
            world.setThundering(false);
            world.setKeepSpawnInMemory(false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRuleValue("randomTickSpeed", "0");
            world.setAnimalSpawnLimit(0);
            world.setAmbientSpawnLimit(0);
            world.setWaterAnimalSpawnLimit(0);
            world.setTime(0);
            world.setFullTime(1);
            world.setAutoSave(false);
        }

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        getCommand("unverify").setExecutor(new UnverifyCommand());

        getServer().getConsoleSender().sendMessage(prefix + "§aEnabled!");
    }

    @Override
    public void onDisable() {
        teamspeakBot.getQuery().exit();
        getServer().getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }
}