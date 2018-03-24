package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import de.gamechest.verify.Verify;
import de.gamechest.verify.bot.BotCommand;
import de.gamechest.verify.bot.TeamspeakBot;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GamesBotCommand extends BotCommand {

    private final TeamspeakBot teamspeakBot = Verify.getInstance().getTeamspeakBot();

    private final HashMap<String, Integer> gameTypes = new HashMap<>();

    public GamesBotCommand(TS3ApiAsync apiAsync) {
        super(apiAsync, "games", "Zeige, welche Spiele du momentan spielst.");
        gameTypes.put("GTA", 64);
        gameTypes.put("CSGO", 65);
        gameTypes.put("Overwatch", 66);
        gameTypes.put("PUBG", 67);
    }

    @Override
    public void execute(String invokerUniqueId, Integer invokerId, String[] args) {
        if(args.length != 1) {
            apiAsync.sendPrivateMessage(invokerId, "Benutzung: [B]!games "+ Arrays.toString(gameTypes.keySet().toArray(new String[gameTypes.size()]))+"[/B]");
            return;
        }
        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
            if (!teamspeakBot.hasSpecialGroup(clientInfo)) {
                apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
                return;
            }
            if(args[0].contains(",")) {
                StringBuilder removed = new StringBuilder();
                StringBuilder added = new StringBuilder();
                StringBuilder wrongGroup = new StringBuilder();

                for(String game : args[0].split(",")) {
                    if(!isGame(game)) {
                        wrongGroup.append(game).append("[/B],[B]");
                    } else {
                        for (String g : gameTypes.keySet()) {
                            if(g.equalsIgnoreCase(game)) {
                                game = g;
                                break;
                            }
                        }
                        int serverGroupId = gameTypes.get(game);
                        if(clientInfo.isInServerGroup(serverGroupId)) {
                            apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            removed.append(game).append("[/B],[B]");
                        } else {
                            apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            added.append(game).append("[/B],[B]");
                        }
                    }
                }

                removed.append("#");
                added.append("#");
                wrongGroup.append("#");

                if(removed.toString().contains(",")) {
                    apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus folgenden Spielen entfernt: [B]" + removed.toString().replace(",[B]#", ""));
                }
                if(added.toString().contains(",")) {
                    apiAsync.sendPrivateMessage(invokerId, "Du wurdest folgenden Spielen hinzugefügt: [B]" + added.toString().replace(",[B]#", ""));
                }
                if(wrongGroup.toString().contains(",")) {
                    apiAsync.sendPrivateMessage(invokerId, "Folgende Spiele-Gruppen existieren nicht: [B]" + wrongGroup.toString().replace(",[B]#", ""));
                }
            } else {
                String game = args[0];

                if(!isGame(game)) {
                    apiAsync.sendPrivateMessage(invokerId, "Die Spiele-Gruppe [B]"+game+"[/B] exisiert nicht!");
                    return;
                }

                for (String g : gameTypes.keySet()) {
                    int serverGroupId = gameTypes.get(g);
                    if(g.equalsIgnoreCase(game)) {
                        if(clientInfo.isInServerGroup(serverGroupId)) {
                            apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus dem Spiel [B]"+g+"[/B] entfernt.");
                        } else {
                            apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            apiAsync.sendPrivateMessage(invokerId, "Du wurdest dem Spiel [B]"+g+"[/B] hinzugefügt.");
                        }
                        break;
                    }
                }
            }
        }, e -> {
            e.printStackTrace();
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
        });
    }

    private boolean isGame(String s) {
        for (String game : gameTypes.keySet()) {
            if(game.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
