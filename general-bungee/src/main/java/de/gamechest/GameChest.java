package de.gamechest;

import com.voxelboxstudios.resilent.GCPacketServer;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.CloudPermissionCheck;
import de.gamechest.coins.Coins;
import de.gamechest.commands.base.CommandHandler;
import de.gamechest.common.Chest;
import de.gamechest.common.ChestPrefix;
import de.gamechest.common.Rank;
import de.gamechest.common.bungee.BungeeChest;
import de.gamechest.common.bungee.BungeeChestPlugin;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.ban.DatabaseBan;
import de.gamechest.database.ban.DatabaseBanObject;
import de.gamechest.database.ban.Reason;
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
public class GameChest extends Plugin implements BungeeChestPlugin {

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
    @Getter
    private ArrayList<UUID> preLogin = new ArrayList<>();

    private final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public List<ProxiedPlayer> onlineTeam = new ArrayList<>();

    public HashMap<ProxiedPlayer, ProxiedPlayer> TELL_FROM_TO = new HashMap<>();

    @Override
    public void onEnable() {
        BungeeChest.setInstance(instance = this);
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

        ByteCloudMaster.getInstance().setPermissionCheck(new PermissionCheck());

        getProxy().getConsole().sendMessage(ChestPrefix.PREFIX + "§aEnabled!");
    }

    @Override
    public void onDisable() {
        partyManager.onStop();
//        teamspeakBot.stop();
        getProxy().getConsole().sendMessage(ChestPrefix.PREFIX + "§cDisabled!");
    }

    private void initDatabase() {
        try {
            this.databaseManager = new DatabaseManager("game-chest.de", 27017, "server-gc", "Passwort007", "server");
            this.databaseManager.init();
            getProxy().getConsole().sendMessage(ChestPrefix.PREFIX + "§eDatabase - §aConnected!");
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

    @Override
    public boolean isCloudEnabled() {
        return getProxy().getPluginManager().getPlugin("ByteCloudAPI") != null;
    }

    @Override
    public String getPlayername(UUID uuid) {
        DatabasePlayer dbPlayer = new DatabasePlayer(this.databaseManager, uuid, DatabasePlayerObject.LAST_NAME);
        if(dbPlayer.existsPlayer()) {
            return dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
        }
        return null;
    }

    @Override
    public void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
    }

    public static class PermissionCheck implements CloudPermissionCheck<ProxiedPlayer> {

        @Override
        public boolean hasPermission(String permission, ProxiedPlayer checker) {
            return GameChest.getInstance().hasRank(checker.getUniqueId(), Chest.getPermissionRank().getOrDefault(permission, Rank.DEVELOPER));
        }

        @Override
        public String getNoPermissionMessage() {
            return "§cDu hast keine Berechtigung für diesen Befehl!";
        }
    }

//    private void insertNicks() {
//        new Thread("Init Nicks Thread") {
//            @Override
//            public void run() {
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
//                list.put("B3dm4st3r", "betorqa");
//                list.put("AndreasRedet", "DA_SWAMPMONSTA");
//                list.put("_RazorLP", "cygnusdesign");
//                list.put("Ofenkartofel", "dandilion");
//                list.put("DerLaborant", "dallas13");
//                list.put("LucasRex", "Clubwho");
//                list.put("Mausia01", "bradrocks");
//                list.put("C0bbleman", "bob606060");
//                list.put("thunderboy_", "defender14");
//                list.put("_BorisDieBestie_", "hiphoplary");
//                list.put("HDGround", "costou12");// noSkin

//                list.put("EzRek4Life", "drdiggler");// noSkin
//
//                list.put("NinjaTaksforce", "Nanoll");
//                list.put("ProllMC", "Jimmypl");
//                list.put("Santastic", "sonicthegamer");
//                list.put("LeoLatschik", "Poddal");
//                list.put("Heanker", "Winhard");
//                list.put("KingOfWorldz", "leoumbrella");
//                list.put("StitchyB0y", "OrangeRage");
//                list.put("PriesterFr3553", "acdccharlie");
//                list.put("RIckTheChIck", "zyguy25");
//                list.put("Bl0ckFuerst", "oliver4888");
//                list.put("OlliVonWell", "MissLPop");
//                list.put("PvPManHD_", "TheKingShooter");
//                list.put("SuperTomm", "dpanell");
//                list.put("plsshoot21", "zenkl");
//                list.put("myhegrid", "low");
//                list.put("ThunderplayerHD", "Piepen_Paule");
//                list.put("Icemaster001", "eagletamer225");
//                list.put("ItzPaule7", "Vczelda1998");
//                list.put("UmadAndso", "thebeef94");
//                list.put("_EpicGlasses_", "UselessMouth");
//                list.put("Masteroppa", "Mandez228");
//                list.put("CrackMasterIV", "Epic428");
//                list.put("CollCill", "Rzco");
//                list.put("DuckThatShxt", "MLGDuck");
//                list.put("YellowFistus", "FuriousRobot");
//                list.put("HighMen", "Best_lp");
//                list.put("ThatsBlacked", "TheStealthyLion");
//                list.put("aufTour_", "SoupTheBagel");
//                list.put("Grimmichy", "pad0n");
//                list.put("WhiteRainboy", "creeperkiller644");
//                list.put("_RazorFace", "_DarkRazor_");
//                list.put("Nexusbro", "jeddhillon");
//                list.put("Plattphillip", "sam_power");
//                list.put("CaptianDurk", "jedisimin");
//                list.put("GrazerYT", "TBeat");
//                list.put("ScheiteHD", "adam070803");
//                list.put("TakeMeHomer_", "RoanHawk");
//                list.put("StrugglingSam", "Sogeking_Sama");
//                list.put("LookBehindU", "Dan");
//                list.put("Nassasine", "kRISStELLA");
//                list.put("AtomicStallac", "Spirec");
//                list.put("LilTimXx", "tim901204");
//                list.put("KrossChriss", "sam_power");
//                list.put("MuscleSamuel", "Nicho930");
//                list.put("IMinedMuffis", "MixCrazy");
//                list.put("WholeHole", "Bloggman");
//                list.put("SchnuffelPusher", "kat88101");
//                list.put("BrakedBad", "italiansausage");
//                list.put("R0flBlocky", "Troffl");
//                list.put("Be4rdyB0y", "Creepy_Crawlers");
//                list.put("_Herobrain_", "TheRN095");
//                list.put("GhettoPrayer", "zShadowBR");
//                list.put("LeckerBratze00", "Sketchgirl28");
//                list.put("Tr00lMaul", "Lends");
//                list.put("Photovoltaic", "ReaktorPlays");
//                list.put("NotAMelon_", "devetious7");
//                list.put("Dild0lliver", "towskii");
//                list.put("Stumpfscheadel", "Twixellzz");
//                list.put("xSupranleader", "xSupraex");
//                list.put("ChickdIch", "calippoblue");
//                list.put("XxFichDichsxX", "XLokChanX");
//                list.put("CringeGrince", "ViirusOner");
//                list.put("CasualFr3ss3", "Flint_n_steel_");
//                list.put("LetsDockl", "awilson");
//                list.put("2Cool2Win", "Faith");
//                list.put("laurin2008", "AeonBlack8");
//                list.put("_ChesterThat_", "Forzaje");
//                list.put("WilsOnonrOad", "razbaz11");
//                list.put("Finanzberater", "danibakero");
//                list.put("Shr3kIsL0ve", "pixelstick100");
//                list.put("dokkingTurtle", "luisay");
//                list.put("PCforbidden", "PGebharrd");
//                list.put("de4dpool", "kuromod");
//                list.put("Oedem", "jdhovie");
//                list.put("_SpIderman_", "crimina");
//                list.put("C_U_LOL", "aretu");
//                list.put("RealT0ny", "Tehovarlord");
//                list.put("Eosine", "minnnydude1");
//                list.put("Lionbrett", "skiye");
//                list.put("iNtHeBuSh", "edibledude");
//                list.put("evryD4y", "jader_zr007");
//                list.put("n0Tg4YaT4Ll", "lightningx10");
//                list.put("s3riousB00Y", "TheBestPTE");
//                list.put("lookingdum", "jdog4999");
//                list.put("chromakEy", "pandrales");
//                list.put("PolloHermano", "glider");
//                list.put("QuasimodoB34t3r", "LucasLotus");
//                list.put("Minemaster00", "nicopop");
//                list.put("mockinggay", "Mockingjay_21");
//                list.put("sp33dRubbler", "MadeBomb");
//                list.put("FullHouseTime_", "Majczel97");
//                list.put("N3verUni0n", "Paralyx");
//                list.put("SkillNotKennidy", "Ragsy");
//                list.put("Brickminister", "Hammer145");
//                list.put("chilledOfLife", "speedd");
//                list.put("danielChef", "Webess");
//                list.put("kardman", "flashdadash99");
//                list.put("SpinnetFidget", "iandamater");
//                list.put("t4keMYm0ney", "Liquidman01");
//                list.put("Stammbruder", "oolo");
//                list.put("Lutsched", "SilenZFinnyBoy");
//                list.put("WurzelbhdlngPLS", "TheTroller");
//                list.put("oestrogenbuddy", "EpicOstrich");
//                list.put("kaaaarl", "effect117");
//                list.put("NyanTHC", "CheesyChan");
//                list.put("SawUrMum", "Bozz99");
//                list.put("M4ngel", "Extempia");
//                list.put("Alphamenshc", "AlphaGamesHD");
//                list.put("HiatNight", "_Piglet__");
//                list.put("FavoritPilz", "CrazyCreeper1o1");
//                list.put("W00denKnive", "DirtPig");
//                list.put("Isol4ted", "memphis_nutella");
//                list.put("lolPrank3rYEE", "31h3");
//                list.put("Mallemade", "AnimusCustodius");
//                list.put("Epicleptic", "PablElTroll");
//                list.put("Wasagirl", "Consolation");
//                list.put("Speccy", "TheVkz2000");
//                list.put("BallfOfSteel", "Smaug");
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
