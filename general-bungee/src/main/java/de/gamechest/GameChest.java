package de.gamechest;

import com.voxelboxstudios.resilent.GCPacketServer;
import de.gamechest.coins.Coins;
import de.gamechest.commands.base.CommandHandler;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.ban.Reason;
import de.gamechest.database.rank.Rank;
import de.gamechest.listener.*;
import de.gamechest.nick.Nick;
import de.gamechest.packet.PacketHandlerGC;
import de.gamechest.party.PartyManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 09.04.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameChest extends Plugin {

    @Getter
    private static GameChest instance;
    @Getter
    private String version = "unknown";

    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private Nick nick;
    @Getter
    private Coins coins;
    @Getter
    private ConnectManager connectManager;
    @Getter
    private PartyManager partyManager;
    @Getter
    private PacketHandlerGC packetHandler;

    public HashMap<UUID, Rank> rankCache = new HashMap<>();

    public final String prefix = "§2GameChest §8\u00BB ";
    public final String pr_nick = "§5Nick §8\u00BB ";
    public final String pr_stats = "§6Stats §8\u00BB ";
    public final String pr_kick = "§cKick §8\u00BB ";
    public final String pr_ban = "§cBan §8\u00BB ";
    public final String pr_report = "§9Report §8\u00BB ";
    public final String pr_bug = "§9BugReport §8\u00BB ";
    public final String pr_activate = "§6Activate §8\u00BB ";
    public final String pr_party = "§dParty §8\u00BB ";
    public final String pr_verify = "§6Verify §8\u00BB ";

    public final String pr_msg_private = "§f§o[§c§oPrivat§f§o] ";
    public final String pr_msg_team = "§f§o[§c§oTeam§f§o] ";
    public final String pr_msg_party = "§f§o[§d§oParty§f§o] ";

    private final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public List<ProxiedPlayer> onlineTeam = new ArrayList<>();

    public HashMap<ProxiedPlayer, ProxiedPlayer> TELL_FROM_TO = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        initDatabase();

        this.nick = new Nick();
        this.coins = new Coins();
        this.connectManager = new ConnectManager();
        this.partyManager = new PartyManager();

        GCPacketServer.start();
        this.packetHandler = new PacketHandlerGC();

        CommandHandler.registerAllCommands();

//        insertNicks();

        Listener[] listeners = {
                new LoginListener(),
                new PlayerDisconnectListener(),
                new PostLoginListener(),
                new ProxyPingListener(),
                new ServerListener(),
                new ChatListener()
        };
        for (Listener listener : listeners)
            getProxy().getPluginManager().registerListener(this, listener);

        getProxy().getConsole().sendMessage(prefix + "§aEnabled!");
    }

    @Override
    public void onDisable() {
        partyManager.onStop();
//        teamspeakBot.stop();
        getProxy().getConsole().sendMessage(prefix + "§cDisabled!");
    }

    private void initDatabase() {
        try {
            this.databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            this.databaseManager.init();
            getProxy().getConsole().sendMessage(prefix + "§eDatabase - §aConnected!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private char randomChar() {
        return POOL[ThreadLocalRandom.current().nextInt(POOL.length)];
    }

    public String randomKey(int length) {
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
        if (!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            if (dbPlayer.existsPlayer()) {
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
        if (!rankCache.containsKey(uuid)) {
            DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid);
            if (dbPlayer.existsPlayer()) {
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
        if (!rankCache.containsKey(uuid)) {
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

    public String getBanMessage(UUID uuid) {
        DatabaseBan databaseBan = databaseManager.getDatabaseBan();
        String endDate = databaseBan.getDatabaseElement(uuid, DatabaseBanObject.END_DATE).getAsString();
        String extra = null;
        if (databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getObject() != null)
            extra = databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getAsString();
        return
                "§cDu wurdest bis zum §a" + (endDate.equals("-1") ? "§4permanent" : endDate) + "§c vom §6Game-Chest.de Netzwerk§c gebannt."
                        + "\n" + "\n" +
                        "§cGrund: §e" + Reason.getReason(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.REASON).getAsString()).getReason()
                        + (extra != null ? " (" + extra + ")" : "")
                        + "\n" + "\n" +
                        "§7§oDu kannst einen §a§neinmaligen§7§o Entbannungsantrag im Support stellen."
                        + "\n" +
                        "§6Unser Regelwerk findest du unter: §agame-chest.de/regelwerk";
    }

    public boolean isCloudEnabled() {
        return getProxy().getPluginManager().getPlugin("ByteCloudAPI") != null;
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
    }

//    private void insertNicks() {
//        new Thread("Init Nicks Thread") {
//            @Override
//            public void run() {
//
//                System.out.println("Init Nicks...");
//                HashMap<String, String> list = new HashMap<>();
//                list.put("KnoeterichHD", "carletto66");
//                list.put("KaffesatzNab", "classic594");
//                list.put("TeppichLp_Xx", "aftertast");
//                list.put("Schnuefflex", "domvito123");
//                list.put("Feuerwerman_2005", "cougar88");
//                list.put("LogischerLuke203", "daniel1206");
//                list.put("Bubblegummer", "jacob4431");
//                list.put("LOGOS_2005_Fen", "ericcronin");
//                list.put("DragonEvil68", "BaZoOKaMoE");
//                list.put("Hashtagger", "BuffyTVS");
//                list.put("NotchHD_", "Blokkiesam");
//                list.put("MCproYT", "cheers");
//                list.put("CookiePlayZ", "colbyo");
//                list.put("Rexey", "dixib");
//                list.put("xCrasherHD", "domthebomb123");
//                list.put("CaveGamerYT", "jerl999");
//                list.put("ZionLP", "hjerrild123");
//                list.put("MelizzlHD", "hellshell");
//                list.put("ProExeCution", "farrar1");
//                list.put("MrDurios_", "drkollins");
//                list.put("GomminaHD", "emerica2");
//                list.put("ZetinCraftHD", "hansjoergl");
//                list.put("MonsterElii", "Hoags11");
//                list.put("_WoodenSword", "cilence");
//                list.put("GraumannHQ", "Awesominator01");
//                list.put("Flcokengamer", "Asparagus");
//                list.put("WahnsinHD", "aleebs");
//                list.put("Piccio", "chadwick12");
//                list.put("Katzenface_", "chadley253");
//                list.put("Sockenmoster", "chrisseh");
//                list.put("SplexxCraxer", "corgblam");
//                list.put("CapureHD", "chocky10");
//                list.put("LowGr0und", "cookie1337");
//                list.put("Mputiaren", "chune0413");
//                list.put("Rukkie", "Cheasify");
//                list.put("Shokkie", "Bammargera23");
//                list.put("DiPlayz", "Addracyn");
//                list.put("Gr0undling", "Basemind");
//                list.put("FrontCrafterYT", "chayton50000");
//                list.put("UdoGamingHD", "chaser132");
//                list.put("LogischerFel1x", "cici820");
//                list.put("Stralekilian", "Clammers");
//                list.put("_x_Mathias_x_", "chrisc377");
//                list.put("JetztRedIch007", "Cdavis");
//                list.put("Wenzala", "ciaranb64");
//                list.put("ScrealmXL", "Bluecar15");
//                list.put("SuperPlay3r", "BoneHunter");
//                list.put("SiggiSchnitzt", "boothboy");
//                list.put("EcriLP", "ceejay1022");
//                list.put("MatzePlays", "blobfish12");
//                list.put("Anominous", "bigfootyeti");
//                list.put("Alertguy1", "creeperded");
//                list.put("B3dm4st3r", "coolwalker");
//                list.put("AndreasRedet", "DA_SWAMPMONSTA");
//                list.put("_RazorLP", "cocosboy10");
//                list.put("Ofenkartofel", "dandilion");
//                list.put("DerLaborant", "dallas13");
//                list.put("LucasRex", "Clubwho");
//                list.put("Mausia01", "bradrocks");
//                list.put("C0bbleman", "bob606060");
//                list.put("thunderboy_", "defender14");
//                list.put("_BorisDieBestie_", "hiphoplary");
//                list.put("HDGround", "costou12");// noSkin
//
//                list.put("EzRek4Life", "drdiggler");// noSkin
//
//                int i = 0;
//
//                for (String nick : list.keySet()) {
//                    String skinname = list.get(nick);
//                    Skin skin = new Skin(skinname, true);
//                    databaseManager.getDatabaseNick().createNick(i, nick, skin.getSkinValue(), skin.getSkinSignature());
//                    i++;
//                }
//            }
//        }.start();
//    }
}
