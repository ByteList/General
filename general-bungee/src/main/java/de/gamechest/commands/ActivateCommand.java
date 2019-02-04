package de.gamechest.commands;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.activate.DatabaseActivate;
import de.gamechest.database.activate.DatabaseActivateObject;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayer;
import de.gamechest.database.premiumplayer.DatabasePremiumPlayerObject;
import de.gamechest.common.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ByteList on 07.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ActivateCommand extends GCCommand {

    private final GameChest gameChest = GameChest.getInstance();
    private final DatabaseActivate databaseActivate = gameChest.getDatabaseManager().getDatabaseActivate();
    private final DatabasePremiumPlayer databasePremiumPlayer = gameChest.getDatabaseManager().getDatabasePremiumPlayer();

    public ActivateCommand() {
        super("activate");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(!gameChest.hasRank(pp.getUniqueId(), Rank.DEVELOPER)) {
                if(args.length == 1) {
                    String code = args[0];

                    if(!databaseActivate.existsCode(code)) {
                        pp.sendMessage(gameChest.pr_activate+"§cDieser Code existiert nicht!");
                        return;
                    }

                    if(databaseActivate.getDatabaseElement(code, DatabaseActivateObject.REDEEMER).getObject() != null) {
                        pp.sendMessage(gameChest.pr_activate+"§cDieser Code ist nicht mehr gültig!");
                        return;
                    }

                    DatabaseActivate.ActivatePurpose activatePurpose =
                            DatabaseActivate.ActivatePurpose.valueOf(databaseActivate.getDatabaseElement(code, DatabaseActivateObject.PURPOSE).getAsString());
                    databaseActivate.setDatabaseObject(code, DatabaseActivateObject.REDEEMER, pp.getUniqueId().toString());
                    switch (activatePurpose) {
                        case PREMIUM:
                            int months = databaseActivate.getDatabaseElement(code, DatabaseActivateObject.VALUE).getAsInt();
                            long end;
                            Calendar calendar = Calendar.getInstance();

                            if(databasePremiumPlayer.existsPlayer(pp.getUniqueId())){
                                if (months != -2) {
                                    calendar.add(Calendar.MONTH, months);
                                    Date date = new Date(databasePremiumPlayer.getDatabaseElement(pp.getUniqueId(), DatabasePremiumPlayerObject.ENDING_DATE).getAsLong());
                                    calendar.setTime(date);

                                    end = calendar.getTime().getTime();
                                } else {
                                    end = months;
                                }
                            } else { // TODO: 07.07.2017 !!!!
                                if (months != -2) {
                                    calendar.add(Calendar.MONTH, months);

                                    end = calendar.getTime().getTime();
                                } else {
                                    end = months;
                                }
                                databasePremiumPlayer.createPlayer(pp.getUniqueId(), end);
                                gameChest.getDatabaseManager().getAsync().getPlayer(pp.getUniqueId(), dbPlayer-> dbPlayer.setDatabaseObject(DatabasePlayerObject.RANK_ID, 7), DatabasePlayerObject.RANK_ID);
                            }


                            if (end == -2) {
                                pp.sendMessage(gameChest.prefix + "§aDu hast soeben deinen Premium-Rang erhalten!");
                                pp.sendMessage("§8\u00BB §bBitte verbinde dich neu, damit du alle Features nutzen kannst!");
                                pp.sendMessage("§8\u00BB §eDein Premium-Rang läuft nun lebenslang!");
                            } else {
                                pp.sendMessage(gameChest.prefix + "§aDu hast soeben den Premium-Rang erhalten!");
                                pp.sendMessage("§8\u00BB §bBitte verbinde dich neu, damit du alle Features nutzen kannst!");
                                pp.sendMessage("§8\u00BB §eDein Premium-Rang läuft nun bis zum: §c" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(end)));
                            }
                            break;
                        case COINS:
                            long coins = databaseActivate.getDatabaseElement(code, DatabaseActivateObject.VALUE).getAsLong();
                            gameChest.getCoins().addCoins(pp.getUniqueId(), coins);
                            pp.sendMessage(gameChest.prefix + "§aDir wurden §e"+coins+" Coins §agutgeschrieben.");
                            break;
                    }

                    return;
                }
                pp.sendMessage(gameChest.pr_activate+"§c/activate <Code>");
                return;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("info")) {
                String code = args[1];

                if(!gameChest.getDatabaseManager().getDatabaseActivate().existsCode(code)) {
                    sender.sendMessage(gameChest.pr_activate+"§cDieser Code existiert nicht!");
                    return;
                }

                sender.sendMessage(gameChest.pr_activate+"§6Infos über den Code: §c"+code);
                sender.sendMessage("§8\u00BB §7Typ: §a"+gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.PURPOSE).getAsString());
                sender.sendMessage("§8\u00BB §7Wert (Premium->Monate / Coins): §6"+
                        gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.VALUE).getAsString());
                if(gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.REDEEMER).getObject() != null) {
                    String redeemer = gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.REDEEMER).getAsString();
                    if (redeemer.startsWith("#UNUSABLE:")) {
                        sender.sendMessage("§8\u00BB §cDieser Code wurde von §e" + redeemer.replace("#UNUSABLE:", "") + "§c auf ungültig gesetzt.");
                    } else {
                        sender.sendMessage("§8\u00BB §7Eingelöst von: §a" +
                                new DatabasePlayer(gameChest.getDatabaseManager(),
                                        UUID.fromString(gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.REDEEMER).getAsString()))
                                        .getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString());
                    }
                } else sender.sendMessage("§8\u00BB §eNoch nicht eingelöst.");
                return;
            }
            if(args[0].equalsIgnoreCase("unusable")) {
                String code = args[1];

                if(!gameChest.getDatabaseManager().getDatabaseActivate().existsCode(code)) {
                    sender.sendMessage(gameChest.pr_activate+"§cDieser Code existiert nicht!");
                    return;
                }

                if(gameChest.getDatabaseManager().getDatabaseActivate().getDatabaseElement(code, DatabaseActivateObject.REDEEMER).getObject() != null) {
                    sender.sendMessage(gameChest.pr_activate+"§cDieser Code ist nicht mehr gültig!");
                    return;
                }

                gameChest.getDatabaseManager().getDatabaseActivate().setDatabaseObject(code, DatabaseActivateObject.REDEEMER, "#UNUSABLE:"+sender.getName());
                sender.sendMessage(gameChest.pr_activate+"§aDieser Code ist nun ungültig.");
                return;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("create")) {
                if(args[1].equalsIgnoreCase("premium")) {
                    int months;
                    try {
                        months = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(gameChest.pr_activate+"§c<months> = Zahl");
                        return;
                    }

                    String code = "/"+UUID.randomUUID().toString().replace("-", "cP")+"=";

                    if(gameChest.getDatabaseManager().getDatabaseActivate().existsCode(code)) {
                        sender.sendMessage(gameChest.pr_activate+"§cTry again! Error - Code already exists: §7"+code);
                        return;
                    }

                    TextComponent textComponent = new TextComponent("§8\u00BB §c"+code);
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cKlicken, um den Code dann zu kopieren.").create()));

                    gameChest.getDatabaseManager().getDatabaseActivate().createCode(code, DatabaseActivate.ActivatePurpose.PREMIUM, months);
                    sender.sendMessage(gameChest.pr_activate+"§aErstellter Code für: §6"+months+"§a Monate Premium");
                    sender.sendMessage(textComponent);
                    return;
                }
                if(args[1].equalsIgnoreCase("coins")) {
                    long coins;
                    try {
                        coins = Long.parseLong(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(gameChest.pr_activate+"§c<months> = Zahl");
                        return;
                    }

                    String code = "/"+UUID.randomUUID().toString().replace("-", "cC")+"=";

                    if(gameChest.getDatabaseManager().getDatabaseActivate().existsCode(code)) {
                        sender.sendMessage(gameChest.pr_activate+"§cTry again! Error - Code already exists: §7"+code);
                        return;
                    }

                    TextComponent textComponent = new TextComponent("§8\u00BB §c"+code);
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cKlicken, um den Code dann zu kopieren.").create()));

                    gameChest.getDatabaseManager().getDatabaseActivate().createCode(code, DatabaseActivate.ActivatePurpose.COINS, coins);
                    sender.sendMessage(gameChest.pr_activate+"§aErstellter Code für: §6"+coins+"§a Coins");
                    sender.sendMessage(textComponent);
                    return;
                }
            }
        }


        sender.sendMessage(gameChest.pr_activate+"§c/activate create <premium|coins> <months|value>");
        sender.sendMessage(gameChest.pr_activate+"§c/activate unusable <code>");
        sender.sendMessage(gameChest.pr_activate+"§c/activate info <code>");
    }
}
