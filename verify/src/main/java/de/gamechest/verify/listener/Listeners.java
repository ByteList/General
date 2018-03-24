package de.gamechest.verify.listener;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.BountifulAPI;
import de.gamechest.GameChest;
import de.gamechest.verify.Verify;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by ByteList on 29.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Listeners implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        e.setJoinMessage(null);
        player.setGameMode(GameMode.ADVENTURE);
        player.setWalkSpeed(0.2F);
        player.teleport(player.getWorld().getSpawnLocation());
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 200, false, false));
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
            player.hidePlayer(p);
        }

        if (gameChest.isCloudEnabled()) {
            if(gameChest.getNick().isNicked(player.getUniqueId()))
                ByteCloudCore.getInstance().getCloudAPI().addPlayer(player.getCustomName());
            else
                ByteCloudCore.getInstance().getCloudAPI().addPlayer(player.getName());
        }
        player.sendMessage(Verify.getInstance().prefix+"§c/unverify §7- §eEntferne deinen Teamspeak-Account.");
        player.sendMessage(Verify.getInstance().prefix+"§c/lobby §7- §eVerbinde dich wieder zur Lobby.");
        BountifulAPI.sendTitle(player, 5, 100000, 10, "§6Ts³ Verify-Server", "§fSchreibe den ChestBot mit §e!verify§f an.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        e.setQuitMessage(null);
        BountifulAPI.sendTitle(e.getPlayer(), 1, 1, 1, "§r", "§r");

        if(gameChest.isCloudEnabled()) {
            if(gameChest.getNick().isNicked(player.getUniqueId()))
                ByteCloudCore.getInstance().getCloudAPI().removePlayer(player.getCustomName());
            else
                ByteCloudCore.getInstance().getCloudAPI().removePlayer(player.getName());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.floor(from.getX());
        double z = Math.floor(from.getZ());
        if (Math.floor(to.getX()) != x || Math.floor(to.getZ()) != z) {
            x += 0.5;
            z += 0.5;
            e.getPlayer().teleport(new Location(from.getWorld(), x, from.getY(), z, from.getYaw(), from.getPitch()));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityAtInteract(PlayerInteractAtEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }
}
