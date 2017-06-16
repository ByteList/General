package de.gamechest;

import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 26.03.2017.
 */
public class UUIDFetcher {

    private static HashMap<String, UUID> cache = new HashMap<>();

    /**
     * @param player The player
     * @return The UUID of the given player
     */
    //Uncomment this if you want the helper method for BungeeCord:
	/*
	public static UUID getUUID(ProxiedPlayer player) {
		return getUUID(player.getName());
	}
	*/

    /**
     * @param player The player
     * @return The UUID of the given player
     */
    //Uncomment this if you want the helper method for Bukkit/Spigot:
	/*
	public static UUID getUUID(Player player) {
		return getUUID(player.getName());
	}
	*/

    /**
     * @param playername The name of the player
     * @return The UUID of the given player
     */
    public static UUID getUUID(String playername) {
        if(cache.containsKey(playername)) {
            return cache.get(playername);
        }

        DatabaseUuidBuffer databaseUuidBuffer = GameChest.getInstance().getDatabaseManager().getDatabaseUuidBuffer();

        if(databaseUuidBuffer.existsPlayer(playername)) {
            UUID uuid = databaseUuidBuffer.getUUID(playername);
            cache.put(playername, uuid);
            return uuid;
        }
        return null;
    }

    /**
     * Get the uuid from the mojang api servers.
     * Can throw a StringIndexOutOfBoundsException if player uuid does not exist.
     *
     * @param playername The name of the player
     * @return The UUID of the given player
     */
    public static UUID getUnsaveUUID(String playername) {
        if(cache.containsKey(playername)) {
            return cache.get(playername);
        }
        String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

        StringBuilder result = new StringBuilder();

        readData(output, result);

        String u = result.toString();
        String uid = "";

        for (int i = 0; i <= 31; i++) {
            uid = uid + u.charAt(i);
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uid = uid + "-";
            }
        }

        UUID uuid = UUID.fromString(uid);
        cache.put(playername, uuid);
        return uuid;
    }

    private static void readData(String toRead, StringBuilder result) {
        int i = 7;

        while(i < 200) {
            if(!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

                result.append(String.valueOf(toRead.charAt(i)));

            } else {
                break;
            }

            i++;
        }
    }

    private static String callURL(String ur) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(ur);
            urlConn = url.openConnection();

            if (urlConn != null) urlConn.setReadTimeout(60 * 1000);

            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                if (bufferedReader != null) {
                    int cp;

                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }

                    bufferedReader.close();
                }
            }

            in.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
