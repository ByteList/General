package de.gamechest.stats;

import de.gamechest.GameChest;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.stats.clickattack.DatabaseClickAttack;
import de.gamechest.database.stats.clickattack.DatabaseClickAttackObject;
import de.gamechest.database.stats.deathrun.DatabaseDeathRun;
import de.gamechest.database.stats.deathrun.DatabaseDeathRunObject;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuell;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuellObject;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefence;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefenceObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 11.04.2017.
 */
public class Stats {

    private HashMap<UUID, HashMap<DatabaseClickAttackObject, Integer>> ca_cache = new HashMap<>();
    private HashMap<UUID, HashMap<DatabaseShulkerDefenceObject, Integer>> sd_cache = new HashMap<>();
    private HashMap<UUID, HashMap<DatabaseJumpDuellObject, Integer>> jd_cache = new HashMap<>();
    private HashMap<UUID, HashMap<DatabaseDeathRunObject, Integer>> dr_cache = new HashMap<>();

    private final String prefix = "§6Stats §8\u00BB ";
    private final DatabaseManager databaseManager;
    
    public Stats() {
        this.databaseManager = GameChest.getInstance().getDatabaseManager();
    }

    /**
     * Returns the ClickAttack Sql class
     * @return
     */
    public DatabaseClickAttack getClickAttack() {
        return databaseManager.getDatabaseClickAttack();
    }

    public DatabaseShulkerDefence getShulkerDefence() {
        return databaseManager.getDatabaseShulkerDefence();
    }

    public DatabaseDeathRun getDeathRun() {
        return databaseManager.getDatabaseDeathRun();
    }

    public DatabaseJumpDuell getJumpDuell() {
        return databaseManager.getDatabaseJumpDuell();
    }

    // CA-Cache

    public void createCACache(UUID uuid) {
        if(existsCACache(uuid)) return;

        HashMap<DatabaseClickAttackObject, Integer> cache = new HashMap<>();
        for(DatabaseClickAttackObject types : DatabaseClickAttackObject.values())
            if(types != DatabaseClickAttackObject.RANK && types != DatabaseClickAttackObject.UUID) cache.put(types, 0);

        ca_cache.put(uuid, cache);
    }

    public void deleteCACache(UUID uuid) {
        ca_cache.remove(uuid);
    }

    public boolean existsCACache(UUID uuid) {
        return ca_cache.containsKey(uuid);
    }

    public void addCACacheValue(UUID uuid, DatabaseClickAttackObject type, Integer value) {
        if(!existsCACache(uuid)) return;

        Integer oldValue = getCACacheValue(uuid, type);
        Integer newValue = oldValue+value;

        ca_cache.get(uuid).put(type, newValue);
    }

    public Integer getCACacheValue(UUID uuid, DatabaseClickAttackObject type) {
        if(!existsCACache(uuid)) return 0;

        return ca_cache.get(uuid).get(type);
    }

    // SD-Cache

    public  void createSDCache(UUID uuid) {
        if(existsSDCache(uuid)) return;

        HashMap<DatabaseShulkerDefenceObject, Integer> cache = new HashMap<>();
        for(DatabaseShulkerDefenceObject types : DatabaseShulkerDefenceObject.values())
            if(types != DatabaseShulkerDefenceObject.RANK && types != DatabaseShulkerDefenceObject.UUID) cache.put(types, 0);

        sd_cache.put(uuid, cache);
    }

    public void deleteSDCache(UUID uuid) {
        sd_cache.remove(uuid);
    }

    public  boolean existsSDCache(UUID uuid) {
        return sd_cache.containsKey(uuid);
    }

    public  void addSDCacheValue(UUID uuid, DatabaseShulkerDefenceObject type, Integer value) {
        if(!existsSDCache(uuid)) return;

        Integer oldValue = getSDCacheValue(uuid, type);
        Integer newValue = oldValue+value;

        sd_cache.get(uuid).put(type, newValue);
    }

    public  Integer getSDCacheValue(UUID uuid, DatabaseShulkerDefenceObject type) {
        if(!existsSDCache(uuid)) return 0;

        return sd_cache.get(uuid).get(type);
    }

    // JD-Cache

    public  void createJDCache(UUID uuid) {
        if(existsJDCache(uuid)) return;

        HashMap<DatabaseJumpDuellObject, Integer> cache = new HashMap<>();
        for(DatabaseJumpDuellObject types : DatabaseJumpDuellObject.values())
            if(types != DatabaseJumpDuellObject.RANK && types != DatabaseJumpDuellObject.UUID) cache.put(types, 0);

        jd_cache.put(uuid, cache);
    }

    public  void deleteJDCache(UUID uuid) {
        jd_cache.remove(uuid);
    }

    public  boolean existsJDCache(UUID uuid) {
        return jd_cache.containsKey(uuid);
    }

    public  void addJDCacheValue(UUID uuid, DatabaseJumpDuellObject type, Integer value) {
        if(!existsJDCache(uuid)) return;

        Integer oldValue = getJDCacheValue(uuid, type);
        Integer newValue = oldValue+value;

        jd_cache.get(uuid).put(type, newValue);
    }

    public  Integer getJDCacheValue(UUID uuid, DatabaseJumpDuellObject type) {
        if(!existsJDCache(uuid)) return 0;

        return jd_cache.get(uuid).get(type);
    }

    // DR-Cache

    public  void createDRCache(UUID uuid) {
        if(existsDRCache(uuid)) return;

        HashMap<DatabaseDeathRunObject, Integer> cache = new HashMap<>();
        for(DatabaseDeathRunObject types : DatabaseDeathRunObject.values())
            if(types != DatabaseDeathRunObject.RANK && types != DatabaseDeathRunObject.UUID) cache.put(types, 0);

        dr_cache.put(uuid, cache);
    }

    public  void deleteDRCache(UUID uuid) {
        dr_cache.remove(uuid);
    }

    public  boolean existsDRCache(UUID uuid) {
        return dr_cache.containsKey(uuid);
    }

    public  void addDRCacheValue(UUID uuid, DatabaseDeathRunObject type, Integer value) {
        if(!existsDRCache(uuid)) return;

        Integer oldValue = getDRCacheValue(uuid, type);
        Integer newValue = oldValue+value;

        dr_cache.get(uuid).put(type, newValue);
    }

    public  Integer getDRCacheValue(UUID uuid, DatabaseDeathRunObject type) {
        if(!existsDRCache(uuid)) return 0;

        return dr_cache.get(uuid).get(type);
    }

    public  void addCacheToDatabase(UUID uuid, StatsType statsType) {
        if(statsType == StatsType.CLICKATTACK) {
            if(!existsCACache(uuid)) return;
            Player p = Bukkit.getPlayer(uuid);
            if(p.isOnline()) p.sendMessage(prefix+"§eDeine Rundenstatistik:");
            for(DatabaseClickAttackObject cacheType : DatabaseClickAttackObject.values()) {
                if(cacheType != DatabaseClickAttackObject.RANK && cacheType != DatabaseClickAttackObject.UUID) {
                    Integer cacheValue = getCACacheValue(uuid, cacheType);
                    Integer dbValue = getClickAttack().getDatabaseElement(uuid, cacheType).getAsInt();

                    getClickAttack().setDatabaseObject(uuid, cacheType, dbValue+cacheValue);
                    if(cacheType == DatabaseClickAttackObject.POINTS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Punkte: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseClickAttackObject.KILLS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Kills: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseClickAttackObject.DEATHS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Tode: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseClickAttackObject.CLICKED_BLOCKS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Benutzte Blöcke: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseClickAttackObject.EARNED_COINS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Verdiente Coins: §a"+cacheValue);
                    }
                }
            }
            deleteCACache(uuid);
            if(p.isOnline()) p.sendMessage("");
        }

        if(statsType == StatsType.SHULKERDEFENCE) {
            if(!existsSDCache(uuid)) return;
            Player p = Bukkit.getPlayer(uuid);
            if(p.isOnline()) p.sendMessage(prefix+"§eDeine Rundenstatistik:");
            for(DatabaseShulkerDefenceObject cacheType : DatabaseShulkerDefenceObject.values()) {
                if(cacheType != DatabaseShulkerDefenceObject.RANK && cacheType != DatabaseShulkerDefenceObject.UUID) {
                    Integer cacheValue = getSDCacheValue(uuid, cacheType);
                    Integer dbValue = getShulkerDefence().getDatabaseElement(uuid, cacheType).getAsInt();

                    getShulkerDefence().setDatabaseObject(uuid, cacheType, dbValue+cacheValue);
                    if(cacheType == DatabaseShulkerDefenceObject.POINTS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Punkte: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseShulkerDefenceObject.KILLS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Kills: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseShulkerDefenceObject.DEATHS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Tode: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseShulkerDefenceObject.KILLED_SHULKERS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Getötete Shulker: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseShulkerDefenceObject.EARNED_COINS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Verdiente Coins: §a"+cacheValue);
                    }
                }
            }
            deleteSDCache(uuid);
            if(p.isOnline()) p.sendMessage("");
        }

        if(statsType == StatsType.JUMPDUELL) {
            if(!existsJDCache(uuid)) return;
            Player p = Bukkit.getPlayer(uuid);
            if(p.isOnline()) p.sendMessage(prefix+"§eDeine Rundenstatistik:");
            for(DatabaseJumpDuellObject cacheType : DatabaseJumpDuellObject.values()) {
                if(cacheType != DatabaseJumpDuellObject.RANK && cacheType != DatabaseJumpDuellObject.UUID) {
                    Integer cacheValue = getJDCacheValue(uuid, cacheType);
                    Integer dbValue = getJumpDuell().getDatabaseElement(uuid, cacheType).getAsInt();

                    getJumpDuell().setDatabaseObject(uuid, cacheType, dbValue+cacheValue);
                    if(cacheType == DatabaseJumpDuellObject.POINTS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Punkte: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseJumpDuellObject.FAILS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Fails: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseJumpDuellObject.EARNED_COINS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Verdiente Coins: §a"+cacheValue);
                    }
                }
            }
            deleteJDCache(uuid);
            if(p.isOnline()) p.sendMessage("");
        }

        if(statsType == StatsType.DEATHRUN) {
            if(!existsDRCache(uuid)) return;
            Player p = Bukkit.getPlayer(uuid);
            if(p.isOnline()) p.sendMessage(prefix+"§eDeine Rundenstatistik:");
            for(DatabaseDeathRunObject cacheType : DatabaseDeathRunObject.values()) {
                if(cacheType != DatabaseDeathRunObject.RANK && cacheType != DatabaseDeathRunObject.UUID) {
                    Integer cacheValue = getDRCacheValue(uuid, cacheType);
                    Integer dbValue = getDeathRun().getDatabaseElement(uuid, cacheType).getAsInt();
                    getDeathRun().setDatabaseObject(uuid, cacheType, dbValue+cacheValue);
                    if(cacheType == DatabaseDeathRunObject.POINTS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Punkte: §a"+cacheValue);
                    }
                    if(cacheType == DatabaseDeathRunObject.EARNED_COINS) {
                        if(p.isOnline()) p.sendMessage("§8\u00BB §7Verdiente Coins: §a"+cacheValue);
                    }
                }
            }
            deleteJDCache(uuid);
            if(p.isOnline()) p.sendMessage("");
        }
    }


    public enum StatsType {
        CLICKATTACK, SHULKERDEFENCE, JUMPDUELL, DEATHRUN
    }
}
