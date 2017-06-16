package de.gamechest;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class Skin {
    private UUID uuid;
    private String name;
    private String value;
    private String signature;

    public Skin(UUID uuid) {
        this.uuid = uuid;
        load();
    }
//
//    public Skin(String name) {
//        try {
//            this.uuid = UUIDFetcher.getUUID(name);
//            load();
//        } catch (Exception ignored) {
//            this.name = "";
//            this.value = "";
//            this.signature = "";
//        }
//    }

    private void load() {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid.toString().replace("-", "") + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");

            Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A");

            String json = scanner.next();
            scanner.close();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
            for (int i = 0; i < properties.size(); i++) {
                try {
                    JSONObject property = (JSONObject) properties.get(i);
                    String name = (String) property.get("name");
                    String value = (String) property.get("value");
                    String signature = property.containsKey("signature") ? (String) property.get("signature") : null;

                    this.name = name;
                    this.value = value;
                    this.signature = signature;
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to apply auth property", e);
                }
            }
        } catch (Exception ignored) { }
    }

    public String getSkinValue() {
        return this.value;
    }

    public String getSkinName() {
        return this.name;
    }

    public String getSkinSignature() {
        return this.signature;
    }
}
