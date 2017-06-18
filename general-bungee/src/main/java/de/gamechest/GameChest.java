package de.gamechest;

import com.voxelboxstudios.resilent.GCPacketServer;
import de.gamechest.commands.base.CommandHandler;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.ban.Reason;
import de.gamechest.database.rank.Rank;
import de.gamechest.listener.LoginListener;
import de.gamechest.listener.PlayerDisconnectListener;
import de.gamechest.listener.PostLoginListener;
import de.gamechest.listener.ProxyPingListener;
import de.gamechest.nick.Nick;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by ByteList on 09.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameChest extends Plugin {

    @Getter
    private static GameChest instance;

    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private Nick nick;
    @Getter
    private ConnectManager connectManager;

    public final String prefix = "§2GameChest §8\u00BB ";
    public String pr_nick = "§5Nick §8\u00BB ";
    public String pr_stats = "§6Stats §8\u00BB ";
    public String pr_kick = "§cKick §8\u00BB ";
    public String pr_ban = "§cBan §8\u00BB ";
    public String pr_bug = "§9BugReport §8\u00BB ";
    public final String pr_msg = "§f§o[§c§oPrivat§f§o] ";


    public HashMap<ProxiedPlayer, ProxiedPlayer> TELL_FROM_TO = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.nick = new Nick();
        this.connectManager = new ConnectManager();
        initDatabase();

        GCPacketServer.start();

        CommandHandler.registerAllCommands();

//        insertNicks();

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());
        getProxy().getPluginManager().registerListener(this, new PostLoginListener());
        getProxy().getPluginManager().registerListener(this, new ProxyPingListener());

        getProxy().getConsole().sendMessage(prefix+"§aEnabled!");
    }

    @Override
    public void onDisable() {
        getProxy().getConsole().sendMessage(prefix+"§cDisabled!");
    }

    private void initDatabase() {
        try {
            this.databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            this.databaseManager.init();
            getProxy().getConsole().sendMessage(prefix+"§eDatabase - §aConnected!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasRank(UUID uuid, Rank rank) {
        DatabasePlayer databasePlayer = databaseManager.getDatabasePlayer(uuid);
        return databasePlayer.existsPlayer() && databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt() <= rank.getId();
    }

    public boolean equalsRank(UUID uuid, Rank rank) {
        DatabasePlayer databasePlayer = databaseManager.getDatabasePlayer(uuid);
        return databasePlayer.existsPlayer() && Objects.equals(databasePlayer.getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt(), rank.getId());
    }

    public String getBanMessage(UUID uuid) {
        DatabaseBan databaseBan = databaseManager.getDatabaseBan();
        String endDate = databaseBan.getDatabaseElement(uuid, DatabaseBanObject.END_DATE).getAsString();
        String extra = null;
        if(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getObject() != null)
            extra = databaseBan.getDatabaseElement(uuid, DatabaseBanObject.EXTRA_MESSAGE).getAsString();
        return
                "§cDu wurdest bis zum §a"+(endDate.equals("-1") ? "§4permanent" : endDate)+"§c vom §6Game-Chest.de Netzwerk§c gebannt."
                        + "\n" + "\n" +
                        "§cGrund: §e"+ Reason.getReason(databaseBan.getDatabaseElement(uuid, DatabaseBanObject.REASON).getAsString())
                                        + (extra != null ? " ("+extra+")" : "")
                        + "\n" + "\n" +
                        "§7§oDu kannst einen §a§neinmaligen§7§o Entbannungsantrag im Support stellen."
                        + "\n" +
                        "§6Unser Regelwerk findest du unter: §agame-chest.de/regelwerk";
    }

//    private void insertNicks() {
//        System.out.println("Init Nicks...");
//        HashMap<String, String> list = new HashMap<>();
//        list.put("KnoeterichHD", "carletto66");
//        list.put("KaffesatzNab", "classic594");
//        list.put("TeppichLp_Xx", "aftertast");
//        list.put("Schnuefflex", "domvito123");
//        list.put("Feuerwerman_2005", "cougar88");
//        list.put("LogischerLuke203", "daniel1206");
//        list.put("Bubblegummer", "jacob4431");
//        list.put("LOGOS_2005_Fen", "ericcronin");
//        list.put("DragonEvil68", "BaZoOKaMoE");
//        list.put("Hashtagger", "BuffyTVS");
//        list.put("NotchHD_", "Blokkiesam");
//        list.put("MCproYT", "cheers");
//        list.put("CookiePlayZ", "colbyo");
//        list.put("Rexey", "dixib");
//        list.put("xCrasherHD", "domthebomb123");
//        list.put("CaveGamerYT", "jerl999");
//        list.put("ZionLP", "hjerrild123");
//        list.put("MelizzlHD", "hellshell");
//        list.put("ProExeCution", "farrar1");
//        list.put("MrDurios_", "drkollins");
//        list.put("GomminaHD", "emerica2");
//        list.put("ZetinCraftHD", "hansjoergl");
//        list.put("MonsterElii", "Hoags11");
//        list.put("_WoodenSword", "cilence");
//        list.put("GraumannHQ", "Awesominator01");
//        list.put("Flcokengamer", "Asparagus");
//        list.put("WahnsinHD", "aleebs");
//        list.put("Piccio", "chadwick12");
//        list.put("Katzenface_", "chadley253");
//        list.put("Sockenmoster", "chrisseh");
//        list.put("SplexxCraxer", "corgblam");
//        list.put("CapureHD", "chocky10");
//        list.put("LowGr0und", "cookie1337");
//        list.put("Mputiaren", "chune0413");
//        list.put("Rukkie", "Cheasify");
//        list.put("Shokkie", "Bammargera23");
//        list.put("DiPlayz", "Addracyn");
//        list.put("Gr0undling", "Basemind");
//        list.put("FrontCrafterYT", "chayton50000");
//        list.put("UdoGamingHD", "chaser132");
//        list.put("LogischerFel1x", "cici820");
//        list.put("Stralekilian", "Clammers");
//        list.put("_x_Mathias_x_", "chrisc377");
//        list.put("JetztRedIch007", "Cdavis");
//        list.put("Wenzala", "ciaranb64");
//        list.put("ScrealmXL", "Bluecar15");
//        list.put("SuperPlay3r", "BoneHunter");
//        list.put("SiggiSchnitzt", "boothboy");
//        list.put("EcriLP", "ceejay1022");
//        list.put("MatzePlays", "blobfish12");
//        list.put("Anominous", "bigfootyeti");
//        list.put("Alertguy1", "creeperded");
//        list.put("B3dm4st3r", "coolwalker");
//        list.put("AndreasRedet", "DA_SWAMPMONSTA");
//        list.put("_RazorLP", "cocosboy10");
//        list.put("Ofenkartofel", "dandilion");
//        list.put("DerLaborant", "dallas13");
//        list.put("LucasRex", "Clubwho");
//        list.put("Mausia01", "bradrocks");
//        list.put("C0bbleman", "bob606060");
//        list.put("thunderboy_", "defender14");
//        list.put("_BorisDieBestie_", "hiphoplary");
//        list.put("HDGround", "costou12"); noSkin
//
//        list.put("EzRek4Life", "drdiggler"); noSkin
//
//        int i = 0;
//
//        for(String nick : list.keySet()) {
//            String skinname = list.get(nick);
//            Skin skin = new Skin(skinname);
//            databaseManager.getDatabaseNick().createNick(i, nick, skin.getSkinValue(), skin.getSkinSignature());
//            i++;
//        }
//    }
}
