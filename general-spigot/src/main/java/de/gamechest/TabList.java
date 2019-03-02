package de.gamechest;

import de.gamechest.common.Rank;
import de.gamechest.common.spigot.SpigotChestNick;
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
    private static HashMap<UUID, String> customPrefix = new HashMap<>();
    private static HashMap<UUID, String> customSuffix = new HashMap<>();
    private static HashMap<UUID, String> customPos = new HashMap<>();

    public static void update(Player player, TabListMode tabListMode) {
        if(tabListMode.isRank()) {
            Rank rank;
            SpigotChestNick nick = GameChest.getInstance().getNick();
            if(nick.isNicked(player.getUniqueId()) || GameChest.getInstance().isRankToggled(player.getUniqueId())) {
                rank = Rank.SPIELER;
            } else {
                rank = GameChest.getInstance().getRank(player.getUniqueId());
            }
            asRank(player, rank);
        } else if(tabListMode.isColor()) {
            asColor(player, tabListMode);
        } else {
            throw new IllegalArgumentException(tabListMode.toString() + " cannot be updated!");
        }
    }

    public static void updateCustom(Player player, String pos, String prefix, String suffix) {
        asCustom(player, pos, prefix, suffix);
    }

    public static void updateParty(Player player, List<UUID> partyList) {
        asParty(player, partyList);
    }

    /**
     * Removed because added to as-methods.
     * @param player was used
     */
    @Deprecated
    public static void onlyUpdatePlayers(Player player) {
        throw new UnsupportedOperationException("Method is not supported yet.");
    }

    private static void asRank(Player player, Rank rank) {
        String prefix = rank.getPrefix();
        String teamName = rank.getId()+rank.getShortName();

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
            Team team = board.getTeam(teamName);
            if(team == null) {
                team = board.registerNewTeam(teamName);
            }
            team.setPrefix(prefix);
            team.setSuffix("§r");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId() && playerModes.containsKey(all.getUniqueId())) {
                update0(playerBoard, all);
            }
        }
        playerModes.put(player.getUniqueId(), TabListMode.RANK);
    }

    private static void asColor(Player player, TabListMode tabListMode) {
        if(!tabListMode.isColor()) {
            throw new IllegalArgumentException(tabListMode.name()+" is not a color!");
        }
        String prefix = tabListMode.getColorCode();
        String teamName = prefix+"color";

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
            Team team = board.getTeam(teamName);
            if(team == null) {
                team = board.registerNewTeam(teamName);
                team.setPrefix("§"+prefix);
                team.setSuffix("§r");
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId() && playerModes.containsKey(all.getUniqueId())) {
                update0(playerBoard, all);
            }
        }
        playerModes.put(player.getUniqueId(), tabListMode);
    }

    private static void asCustom(Player player, String pos, String prefix, String suffix) {
        TabListMode tabListMode = TabListMode.CUSTOM;

        String teamName = pos+"cstm";

        Scoreboard playerBoard = player.getScoreboard();
        if(playerBoard == null) {
            playerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(playerBoard);
        }

        customPrefix.put(player.getUniqueId(), prefix);
        customSuffix.put(player.getUniqueId(), suffix);
        customPos.put(player.getUniqueId(), pos);

        playerModes.put(player.getUniqueId(), tabListMode);

        for(Player all : Bukkit.getOnlinePlayers()) {
            Scoreboard board = all.getScoreboard();
            if(board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
                all.setScoreboard(board);
            }
            Team team = board.getTeam(teamName);
            if(team == null) {
                team = board.registerNewTeam(teamName);
            }
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());

            if(all.getUniqueId() != player.getUniqueId() && playerModes.containsKey(all.getUniqueId())) {
                update0(playerBoard, all);
            }
        }
    }

    private static void update0(Scoreboard playerBoard, Player player) {
        if(playerModes.containsKey(player.getUniqueId())) {
            TabListMode tabListMode = playerModes.get(player.getUniqueId());
            String teamName = "null";
            Team team;
            String prefix = "null~";
            String suffix = "§r";
            if (tabListMode.isRank()) {
                Rank rank;
                SpigotChestNick nick = GameChest.getInstance().getNick();
                if (nick.isNicked(player.getUniqueId()) || GameChest.getInstance().isRankToggled(player.getUniqueId())) {
                    rank = Rank.SPIELER;
                } else {
                    rank = GameChest.getInstance().getRank(player.getUniqueId());
                }
                teamName = rank.getId() + rank.getShortName();
                prefix = rank.getPrefix();
            } else if(tabListMode.isColor()) {
                prefix = "§" + tabListMode.getColorCode();
                teamName = prefix + "color";
            } else if(tabListMode.isCustom()) {
                prefix = customPrefix.get(player.getUniqueId());
                suffix = customSuffix.get(player.getUniqueId());
                teamName = customPos.get(player.getUniqueId()) + "cstm";
            }

            team = playerBoard.getTeam(teamName);
            if (team == null) {
                team = playerBoard.registerNewTeam(teamName);
            }
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());
        }
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
        CUSTOM("_CUSTOM"),
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
            return !colorCode.equals("_RANK") && !colorCode.equals("_PARTY") && !colorCode.equals("_CUSTOM");
        }

        public boolean isRank() {
            return colorCode.equals("_RANK");
        }

        public boolean isCustom() {
            return colorCode.equals("_CUSTOM");
        }
    }
}
