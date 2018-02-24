package de.gamechest.verify.bot.commands;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
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
    public void execute(Integer invokerId, String[] args) {
        if (!teamspeakBot.hasSpecialGroup(invokerId)) {
            apiAsync.sendPrivateMessage(invokerId, "Du musst verifiziert sein!");
            return;
        }
        if(args.length != 1) {
            apiAsync.sendPrivateMessage(invokerId, "Benutzung: [B]!games "+ Arrays.toString(gameTypes.keySet().toArray(new String[gameTypes.size()]))+"[/B]");
            return;
        }
        ClientInfo clientInfo = teamspeakBot.getClientInfo(invokerId);
        if(clientInfo == null) {
            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
            return;
        }
        if(args[0].contains(",")) {
            StringBuilder removed = new StringBuilder();
            StringBuilder added = new StringBuilder();
            StringBuilder wrongGroup = new StringBuilder();
            for(String game : args[0].split(",")) {
                gameTypes.forEach((s, serverGroupId) -> {
                    if(s.equalsIgnoreCase(game)) {
                        if(clientInfo.isInServerGroup(serverGroupId)) {
                            apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            removed.append(s).append("[/B],[B]");
                        } else {
                            apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
                            added.append(s).append("[/B],[B]");
                        }
                    } else {
                        wrongGroup.append(game).append("[/B],[B]");
                    }
                });
            }
            removed.append("#");
            added.append("#");
            wrongGroup.append("#");
            if(removed.toString().contains(",")) {
                apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus folgenden Spielen entfernt: [B]" + removed.toString().replace(",#", "[/B]"));
            }
            if(added.toString().contains(",")) {
                apiAsync.sendPrivateMessage(invokerId, "Du wurdest folgenden Spielen hinzugefügt: [B]" + added.toString().replace(",#", "[/B]"));
            }
            if(wrongGroup.toString().contains(",")) {
                apiAsync.sendPrivateMessage(invokerId, "Folgende Spiele-Gruppen existieren nicht: [B]" + wrongGroup.toString().replace(",#", "[/B]"));
            }
        } else {
            String game = args[0];
            gameTypes.forEach((s, serverGroupId) -> {
                if(s.equalsIgnoreCase(game)) {
                    if(clientInfo.isInServerGroup(serverGroupId)) {
                        apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
                        apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus dem Spiel [B]"+game+"[/B] entfernt.");
                    } else {
                        apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
                        apiAsync.sendPrivateMessage(invokerId, "Du wurdest dem Spiel [B]"+game+"[/B] hinzugefügt.");
                    }
                } else {
                    apiAsync.sendPrivateMessage(invokerId, "Die Spiele-Gruppe [B]"+game+"[/B] exisiert nicht!");
                }
            });
        }
//        teamspeakBot.getClientInfoAsync(invokerId, clientInfo -> {
//            if(args[0].contains(",")) {
//                StringBuilder removed = new StringBuilder();
//                StringBuilder added = new StringBuilder();
//                StringBuilder wrongGroup = new StringBuilder();
//                for(String game : args[0].split(",")) {
//                    gameTypes.forEach((s, serverGroupId) -> {
//                        if(s.equalsIgnoreCase(game)) {
//                            if(clientInfo.isInServerGroup(serverGroupId)) {
//                                apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
//                                removed.append(s).append("[/B],[B]");
//                            } else {
//                                apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
//                                added.append(s).append("[/B],[B]");
//                            }
//                        } else {
//                            wrongGroup.append(game).append("[/B],[B]");
//                        }
//                    });
//                }
//                removed.append("#");
//                added.append("#");
//                wrongGroup.append("#");
//                if(removed.toString().contains(",")) {
//                    apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus folgenden Spielen entfernt: [B]" + removed.toString().replace(",#", "[/B]"));
//                }
//                if(added.toString().contains(",")) {
//                    apiAsync.sendPrivateMessage(invokerId, "Du wurdest folgenden Spielen hinzugefügt: [B]" + added.toString().replace(",#", "[/B]"));
//                }
//                if(wrongGroup.toString().contains(",")) {
//                    apiAsync.sendPrivateMessage(invokerId, "Folgende Spiele-Gruppen existieren nicht: [B]" + wrongGroup.toString().replace(",#", "[/B]"));
//                }
//            } else {
//                String game = args[0];
//                gameTypes.forEach((s, serverGroupId) -> {
//                    if(s.equalsIgnoreCase(game)) {
//                        if(clientInfo.isInServerGroup(serverGroupId)) {
//                            apiAsync.removeClientFromServerGroup(serverGroupId, clientInfo.getDatabaseId());
//                            apiAsync.sendPrivateMessage(invokerId, "Du wurdest aus dem Spiel [B]"+game+"[/B] entfernt.");
//                        } else {
//                            apiAsync.addClientToServerGroup(serverGroupId, clientInfo.getDatabaseId());
//                            apiAsync.sendPrivateMessage(invokerId, "Du wurdest dem Spiel [B]"+game+"[/B] hinzugefügt.");
//                        }
//                    } else {
//                        apiAsync.sendPrivateMessage(invokerId, "Die Spiele-Gruppe [B]"+game+"[/B] exisiert nicht!");
//                    }
//                });
//            }
//        }, e -> {
//            e.printStackTrace();
//            apiAsync.sendPrivateMessage(invokerId, "[COLOR=red]Ups! Da ist etwas schief gelaufen! Bitte kontaktiere die Administration.[/COLOR]");
//        });
    }
}
