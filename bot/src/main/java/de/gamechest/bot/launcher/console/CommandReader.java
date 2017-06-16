package de.gamechest.bot.launcher.console;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.embed.FieldEmbed;
import de.gamechest.bot.DiscordBot;
import de.gamechest.bot.launcher.BotLauncher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by ByteList on 27.01.2017.
 */
public class CommandReader {

    public static void execute(String command) throws IOException {
        String[] args = command.split(" ");

        if (args.length > 0) {
            command = args[0];
        }
        int i = 0;
        if (command.equalsIgnoreCase("help")) {
            BotLogger.getLogger().info("List of all commands:");
            BotLogger.getLogger().info("help - show this list");
            BotLogger.getLogger().info("end  - stop the cloud");
            BotLogger.getLogger().info("restart  - stop & start the cloud");
            BotLogger.getLogger().info("host  - show you're host address");
            i = 1;
        }

        if (command.equalsIgnoreCase("end")) {
            BotLauncher.shutdown();
            i = 1;
        }

        if (command.equalsIgnoreCase("restart")) {
            i = 1;
        }

        if (command.equalsIgnoreCase("quit")) {
            System.exit(1);
        }

        if (command.equalsIgnoreCase("host")) {
            try {
                BotLogger.getLogger().info("Your host address: " + InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                BotLogger.getLogger().info("UnknownHost: " + e.getMessage());
            }
            i = 1;
        }

        if(command.equalsIgnoreCase("discord")) {
            BotLauncher.getDiscordBot().sendMessage("System", "`*User:* ConsolemasterMC`");
            i = 1;
        }

        if(command.equalsIgnoreCase("discban")) {
            DiscordEmbed discordEmbed = DiscordEmbed.builder()
                    .title("")
                    .fields(Arrays.asList(
                            FieldEmbed.builder()
                                .name("User:")
                                .value("ConsolemasterMC")
                                .build(),
                            FieldEmbed.builder()
                                .name("Reason:")
                                .value("CLIENT")
                                .build(),
                            FieldEmbed.builder()
                                    .name("Extra Message:")
                                    .value("58f780 - KillAura")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Staff-Only:")
                                    .value("NPC: 5 Sec: 7")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Sender:")
                                    .value("System-3")
                                    .build()
                    ))
                    .build();

            DiscordMessage discordMessage = DiscordMessage.builder()
                    .username("System")
                    .content("__**Ban - 02.05.2017 10:49**__")
                    .avatarUrl(DiscordBot.getAvatar())
                    .embed(discordEmbed)
                    .build();
            BotLauncher.getDiscordBot().sendDiscordMessage(discordMessage);
            i = 1;
        }

        if(command.equalsIgnoreCase("discbug")) {
            DiscordEmbed discordEmbed = DiscordEmbed.builder()
                    .title("")
                    .fields(Arrays.asList(
                            FieldEmbed.builder()
                                    .name("Bug-Id:")
                                    .value("#BR5")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Reason:")
                                    .value("TS_VERIFY")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("ServerId:")
                                    .value("fallback")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Extra Message:")
                                    .value("wie geht das?")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Reporter:")
                                    .value("ByteList")
                                    .build(),
                            FieldEmbed.builder()
                                    .name("Previous-ServerId:")
                                    .value("*null*")
                                    .build()
                    ))
                    .build();

            DiscordMessage discordMessage = DiscordMessage.builder()
                    .username("System")
                    .content("__**Bug-Report - 30.04.2017 19:16**__")
                    .avatarUrl(DiscordBot.getAvatar())
                    .embed(discordEmbed)
                    .build();
            BotLauncher.getDiscordBot().sendDiscordMessage(discordMessage);
            i = 1;
        }

        if (i == 0) {
            BotLogger.getLogger().info("Command unknown. Use 'help' for a list of available commands.");
        }
    }
}
