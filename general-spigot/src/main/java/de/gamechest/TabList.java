package de.gamechest;

import de.gamechest.database.rank.Rank;
import de.gamechest.nick.Nick;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 16.04.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class TabList {

    private static HashMap<UUID, TabListMode> playerModes = new HashMap<>();

    public static void update(Player player, TabListMode tabListMode) {
        if(tabListMode.isRank()) {
            Rank rank;
            Nick nick = GameChest.getInstance().getNick();
            if(nick.isNicked(player.getUniqueId()) || GameChest.getInstance().isRankToggled(player.getUniqueId())) {
                rank = Rank.SPIELER;
            } else {
                rank = GameChest.getInstance().getRank(player.getUniqueId());
            }
            asRank(player, rank);
        } else {
            asColor(player, tabListMode);
        }
    }

    public static void updateParty(Player player, List<UUID> partyList) {
        asParty(player, partyList);
    }

    /**
     * Removed because added to as-methods.
     * @param player was used
     */
    @Deprecated
    public static void onlyUpdatePlayers(Player player) {}

    private static void asRank(Player player, Rank rank) {
        String prefix = rank.getPrefix();
        String s = rank.getId()+rank.getShortName();

        Scoreboard playerBoard = player.getScoreboard();
        if(playerBoard == null) {
            playerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(playerBoard);
        }

        for(Player all : Bukkit.getOnlinePlayers()) {
            Scoreboard board = all.getScoreboard();
            if(board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
                all.setScoreboard(board);
            }
            Team team = board.getTeam(s);
            if(team == null) {
                team = board.registerNewTeam(s);
                team.setPrefix(prefix);
                team.setSuffix("§r");
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId() && playerModes.containsKey(all.getUniqueId())) {
                TabListMode atabListMode = playerModes.get(all.getUniqueId());
                String as;
                Team ateam;
                String aprefix;
                if (atabListMode.isRank()) {
                    Rank arank;
                    Nick nick = GameChest.getInstance().getNick();
                    if (nick.isNicked(all.getUniqueId()) || GameChest.getInstance().isRankToggled(all.getUniqueId())) {
                        arank = Rank.SPIELER;
                    } else {
                        arank = GameChest.getInstance().getRank(all.getUniqueId());
                    }
                    as = arank.getId() + arank.getShortName();
                    aprefix = arank.getPrefix();
                } else {
                    aprefix = "§" + atabListMode.getColorCode();
                    as = aprefix + "color";
                }

                ateam = playerBoard.getTeam(as);
                if (ateam == null) {
                    ateam = playerBoard.registerNewTeam(as);
                    ateam.setPrefix(aprefix);
                    ateam.setSuffix("§r");
                    ateam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                }
                ateam.addEntry(all.getName());
            }
        }
        playerModes.put(player.getUniqueId(), TabListMode.RANK);
    }

    private static void asColor(Player player, TabListMode tabListMode) {
        if(!tabListMode.isColor()) {
            throw new IllegalArgumentException(tabListMode.name()+" is not a color!");
        }
        String prefix = tabListMode.getColorCode();
        String s = prefix+"color";

        Scoreboard playerBoard = player.getScoreboard();
        if(playerBoard == null) {
            playerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(playerBoard);
        }

        for(Player all : Bukkit.getOnlinePlayers()) {
            Scoreboard board = all.getScoreboard();
            if(board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
                all.setScoreboard(board);
            }
            Team team = board.getTeam(s);
            if(team == null) {
                team = board.registerNewTeam(s);
                team.setPrefix("§"+prefix);
                team.setSuffix("§r");
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId()) {
                if(playerModes.containsKey(all.getUniqueId())) {
                    TabListMode atabListMode = playerModes.get(all.getUniqueId());
                    String as;
                    Team ateam;
                    String aprefix;
                    if (atabListMode.isRank()) {
                        Rank arank;
                        Nick nick = GameChest.getInstance().getNick();
                        if (nick.isNicked(all.getUniqueId()) || GameChest.getInstance().isRankToggled(all.getUniqueId())) {
                            arank = Rank.SPIELER;
                        } else {
                            arank = GameChest.getInstance().getRank(all.getUniqueId());
                        }
                        as = arank.getId() + arank.getShortName();
                        aprefix = arank.getPrefix();
                    } else {
                        aprefix = "§" + atabListMode.getColorCode();
                        as = prefix + "color";
                    }

                    ateam = playerBoard.getTeam(as);
                    if (ateam == null) {
                        ateam = playerBoard.registerNewTeam(as);
                        ateam.setPrefix(aprefix);
                        ateam.setSuffix("§r");
                        ateam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                    }
                    ateam.addEntry(all.getName());
                }
            }
        }
        playerModes.put(player.getUniqueId(), tabListMode);
    }

    private static void asParty(Player player, List<UUID> partyPlayers) {
        String s = "000party";

//        for(UUID uuid : partyPlayers) {
//            Player all = Bukkit.getPlayer(uuid);
//
//            String prefix = "§dParty§8 \u00BB §d";
//            Scoreboard board = all.getScoreboard();
//            if(board == null) {
//                board = Bukkit.getScoreboardManager().getNewScoreboard();
//                all.setScoreboard(board);
//            }
//            Team team = board.getTeam(s);
//            if(team == null) {
//                team = board.registerNewTeam(s);
//                team.setPrefix(prefix);
//                team.setSuffix("§r");
//                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
//            }
//            team.addEntry(all.getName());
//        }

        String prefix = "§dParty§8 \u00BB §d";

        Scoreboard playerBoard = player.getScoreboard();
        if(playerBoard == null) {
            playerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(playerBoard);
        }

        for(UUID uuid : partyPlayers) {
            Player all = Bukkit.getPlayer(uuid);
            if(all == null)
                return;
            Scoreboard board = all.getScoreboard();
            if(board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
                all.setScoreboard(board);
            }
            Team team = board.getTeam(s);
            if(team == null) {
                team = board.registerNewTeam(s);
                team.setPrefix(prefix);
                team.setSuffix("§r");
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId()) {

                team = playerBoard.getTeam(s);
                if (team == null) {
                    team = playerBoard.registerNewTeam(s);
                    team.setPrefix(prefix);
                    team.setSuffix("§r");
                    team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                }
                team.addEntry(all.getName());
            }
        }
    }


    public enum TabListMode {
        RANK("_RANK"),
        PARTY("_PARTY"),
        BLACK("0"),
        DARK_BLUE("1"),
        DARK_GREEN("2"),
        DARK_AQUA("3"),
        DARK_RED("4"),
        DARK_PURPLE("5"),
        GOLD("6"),
        GRAY("7"),
        DARK_GRAY("8"),
        BLUE("9"),
        GREEN("a"),
        AQUA("b"),
        RED("c"),
        LIGHT_PURPLE("d"),
        YELLOW("e"),
        WHITE("f");

        @Getter
        private String colorCode;

        TabListMode(String colorCode) {
            this.colorCode = colorCode;
        }

        public boolean isColor() {
            return !colorCode.equals("_RANK") || !colorCode.equals("_PARTY");
        }

        public boolean isRank() {
            return colorCode.equals("_RANK");
        }
    }
}
