package de.gamechest.chatlog;

import de.gamechest.GameChest;
import de.gamechest.database.chatlog.DatabaseChatlog;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ByteList on 11.04.2017.
 */
public class ChatLog {

    private HashMap<String, ArrayList<Integer>> usersIds = new HashMap<>();

    private HashMap<String, Long> timestamp = new HashMap<>();
    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, String> prefixes = new HashMap<>();

    private Integer messageId;

    public final String prefix = "§cLog §8\u00BB";
    private final DatabaseChatlog databaseChatlog;

    public ChatLog() {
        messageId = 0;
        databaseChatlog = GameChest.getInstance().getDatabaseManager().getDatabaseChatlog();
    }

    public void addMessage(String name, String prefix, String message) {
        Date now = new Date();
        Long time = now.getTime() / 1000L;

        if (!usersIds.containsKey(name)) {
            usersIds.put(name, new ArrayList<>());
        }

        ArrayList<Integer> list = usersIds.get(name);
        list.add(messageId);

        timestamp.put(messageId.toString(), time);
        messages.put(messageId.toString(), message);
        prefixes.put(messageId.toString(), prefix);

        messageId++;
    }

    public void createLog(CommandSender sender, String target) {
        if(!usersIds.containsKey(target)) {
            sender.sendMessage(prefix+"§7"+target+" hat noch keine Nachrichten geschrieben.");
            return;
        }

        String id = GameChest.randomKey(7);

        if(databaseChatlog.existsChatlog(id))
            id = "p"+id;

        databaseChatlog.createChatlog(id, target, usersIds, timestamp, prefixes, messages);
        sender.sendMessage(prefix + "§aChatlog wurde erstellt: §6game-chest.de/chat/"+id);
    }

    public void createServerlog() {
        String id = GameChest.randomKey(7);

        if(databaseChatlog.existsChatlog(id))
            id = "s"+id;

        databaseChatlog.createChatlog(id, usersIds, timestamp, prefixes, messages);
    }

}
