package de.gamechest;

import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayer;
import de.gamechest.database.onlineplayer.DatabaseOnlinePlayerObject;
import de.gamechest.database.rank.Rank;
import de.gamechest.nick.Nick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Created by ByteList on 16.04.2017.
 */
public class TabList {


    public static void update(Player p, String mode) {
        apply(p, mode);
    }

    private static void apply(Player p, String mode) {
        Scoreboard board = p.getScoreboard() != null ? p.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
        Nick nick = GameChest.getInstance().getNick();

        if (mode.equalsIgnoreCase("LOBBY")) {
            for(Rank ranks : Rank.values()) {
                String s = ranks.getId()+ranks.getShortName();
                Team team = board.getTeam(s) != null ? board.getTeam(s) : board.registerNewTeam(s);
                team.setPrefix(ranks.getPrefix());
                team.setSuffix("§r");
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

                for(Player all : Bukkit.getOnlinePlayers()) {
                    DatabaseOnlinePlayer databaseOnlinePlayer = GameChest.getInstance().getDatabaseManager().getDatabaseOnlinePlayer(all.getUniqueId());
                    if ((!nick.isNicked(all.getUniqueId())) && (!databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.TOGGLED_RANK).getAsBoolean())) {
                        if(GameChest.getInstance().equalsRank(all.getUniqueId(), ranks)) {
                            team.addEntry(all.getName());
                        }
                    } else if (ranks == Rank.SPIELER) team.addEntry(all.getName());
                    if(all != p) {
                        Scoreboard sb = all.getScoreboard();
                        Rank pRank = Rank.getRankById(GameChest.getInstance().getDatabaseManager().getDatabasePlayer(p.getUniqueId()).getDatabaseElement(DatabasePlayerObject.RANK_ID).getAsInt());
                        if ((!nick.isNicked(p.getUniqueId())) && (!databaseOnlinePlayer.getDatabaseElement(DatabaseOnlinePlayerObject.TOGGLED_RANK).getAsBoolean())) {
                            if(GameChest.getInstance().equalsRank(p.getUniqueId(), ranks)) {
                                (sb.getTeam(pRank.getId()+pRank.getShortName()) != null
                                        ? sb.getTeam(pRank.getId()+pRank.getShortName())
                                        : sb.registerNewTeam(pRank.getId()+pRank.getShortName()))
                                        .addEntry(p.getName());
                            }
                        } else if (ranks == Rank.SPIELER)
                            (sb.getTeam(Rank.SPIELER.getId()+Rank.SPIELER.getShortName()) != null
                                    ? sb.getTeam(Rank.SPIELER.getId()+Rank.SPIELER.getShortName())
                                    : sb.registerNewTeam(Rank.SPIELER.getId()+Rank.SPIELER.getShortName()))
                                    .addEntry(p.getName());

                    }
                }
            }
        }
        if (mode.equalsIgnoreCase("INGAME")) {
            String s = "0ingame";
            Team team = board.getTeam(s) != null ? board.getTeam(s) : board.registerNewTeam(s);
            team.setPrefix("§7");
            team.setSuffix("§r");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

            for(Player all : Bukkit.getOnlinePlayers()) {
                team.addEntry(all.getName());
                if(all != p) {
                    Scoreboard sb = all.getScoreboard();
                    Team allTeam = sb.getTeam(s) != null ? sb.getTeam(s) : sb.registerNewTeam(s);
                    allTeam.addEntry(p.getName());

                }
            }
        }
        if (mode.equalsIgnoreCase("INGAME_RED")) {
            String s = "0ingame_red";
            Team team = board.getTeam(s) != null ? board.getTeam(s) : board.registerNewTeam(s);
            team.setPrefix("§c");
            team.setSuffix("§r");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

            for(Player all : Bukkit.getOnlinePlayers()) {
                if(all != p) {
                    Scoreboard sb = all.getScoreboard();
                    Team allTeam = sb.getTeam(s) != null ? sb.getTeam(s) : sb.registerNewTeam(s);
                    allTeam.addEntry(p.getName());

                }
            }
        }
        if (mode.equalsIgnoreCase("INGAME_BLUE")) {
            String s = "0ingame_blue";
            Team team = board.getTeam(s) != null ? board.getTeam(s) : board.registerNewTeam(s);
            team.setPrefix("§9");
            team.setSuffix("§r");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

            for(Player all : Bukkit.getOnlinePlayers()) {
                if(all != p) {
                    Scoreboard sb = all.getScoreboard();
                    Team allTeam = sb.getTeam(s) != null ? sb.getTeam(s) : sb.registerNewTeam(s);
                    allTeam.addEntry(p.getName());

                }
            }
        }
        p.setScoreboard(board);
    }
}
