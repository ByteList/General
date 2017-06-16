package de.gamechest.bot;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import de.gamechest.bot.launcher.console.BotLogger;
import lombok.Getter;

import java.util.logging.Logger;

/**
 * Created by ByteList on 01.05.2017.
 */
public class DiscordBot {

    /*
    Use-Guide: https://github.com/MrPowerGamerBR/TemmieWebhook
    Formatting: https://support.discordapp.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-
     */
    private final Logger logger;

    private TemmieWebhook temmieWebhook;
    @Getter
    private static final String avatar = "https://game-chest.de/wp-content/uploads/2017/03/icon-klein.png";

    public DiscordBot() {
        this.logger = BotLogger.getLogger();
        logger.info("[Discord] Try to connect...");
        temmieWebhook = new TemmieWebhook("https://discordapp.com/api/webhooks/308551636408860673/7Hu9WTPN2lI0uuAcWynIIrwAmss3JmxFTTyF1gtork3MtTHMWG1iGsbQYHpHO4gs57hY");
        logger.info("[Discord] Connected!");
//        sendMessage("System", "```Bot connected from "+BotLauncher.getAddress()+"!```");
    }


    public void sendMessage(String sender, String message) {
        DiscordMessage discordMessage = new DiscordMessage(sender, message, avatar);
        temmieWebhook.sendMessage(discordMessage);
        logger.info("[Discord/MSG] "+discordMessage.getContent());
    }

    public void sendDiscordMessage(DiscordMessage discordMessage) {
        temmieWebhook.sendMessage(discordMessage);
        logger.info("[Discord/MSG] "+discordMessage.getContent());
    }
}
