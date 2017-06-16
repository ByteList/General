package de.gamechest;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 09.02.2017.
 */
public class ConnectManager {

    private final File dir = new File("plugins/", "GCGeneral");
    private final File file = new File(dir, "config.yml");
    private Configuration cfg;



    public ConnectManager() {
        try {
            if(!dir.exists()) dir.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

                List<String> whiteList = new ArrayList<>();
                whiteList.add("b0c1f5be-093b-429c-9bb5-18801a10c32a"); //Ich
                whiteList.add("ecce8108-7c65-4211-b5dd-a76a35abb578"); //Fabi
                whiteList.add("15dab4c9-aa26-44ea-a172-1fa8596ca7f3"); //Phil
                whiteList.add("8aed50f3-d617-49ab-89ab-3bc83101c58b"); //Felix

                cfg.set("ConnectState", ConnectState.DEVELOPMENT.toString());
                cfg.set("Motd", "&cWartungsmodus");
                cfg.set("PlayerLimit", 150);
                cfg.set("WhiteList", whiteList);

                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            } else
                cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException ex) {
            cfg = null;
            ex.printStackTrace();
        }

    }

    public void setConnectState(ConnectState connectState) {
        cfg.set("ConnectState", connectState.toString());
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConnectState getConnectState() {
        return ConnectState.valueOf(cfg.getString("ConnectState").toUpperCase());
    }

    public void setPlayerLimit(int limit) {
        cfg.set("PlayerLimit", limit);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerLimit() {
        return cfg.getInt("PlayerLimit");
    }

    public void setMotd(String motd) {
        cfg.set("Motd", motd);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMotd() {
        return cfg.getString("Motd");
    }

    public void addUuidToWhiteList(UUID uuid) {
        if(!getWhiteList().contains(uuid)) {
            List<String> uuids = new ArrayList<>();
            uuids.addAll(cfg.getStringList("WhiteList"));
            uuids.add(uuid.toString());
            cfg.set("WhiteList", uuids);
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeUuidFromWhiteList(UUID uuid) {
        if(getWhiteList().contains(uuid)) {
            List<String> uuids = new ArrayList<>();
            uuids.addAll(cfg.getStringList("WhiteList"));
            uuids.remove(uuid.toString());
            cfg.set("WhiteList", uuids);
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<UUID> getWhiteList() {
        Collection<UUID> uuids = new ArrayList<>();
        for(String uuid : cfg.getStringList("WhiteList"))
            uuids.add(UUID.fromString(uuid));

        return uuids;
    }

    public enum ConnectState {
        OPEN, WHITELIST, MAINTENANCE, DEVELOPMENT
    }
}
