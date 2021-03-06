package de.gamechest.commands.report;

import de.gamechest.GameChest;
import de.gamechest.commands.base.GCCommand;
import de.gamechest.common.ChestPrefix;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 25.04.2017.
 */
public class PlayerReportCommand extends GCCommand {

    private final GameChest gameChest = GameChest.getInstance();

    public PlayerReportCommand() {
        super("report");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length > 1) {
            String name = args[0];
            ProxiedPlayer player = gameChest.getProxy().getPlayer(name);

            if(player == null) {
                sender.sendMessage(ChestPrefix.PREFIX_REPORT +"§cDer Spieler ist nicht online!");
                return;
            }

            if(sender.getName().equals(player.getName())) {
                sender.sendMessage(ChestPrefix.PREFIX_REPORT+"§cDu kannst dich nicht selbst melden!");
                return;
            }

            StringBuilder reason = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            reason.append("§$#~$~§/~");

            TextComponent accept = new TextComponent("§e"+player.getServer().getInfo().getName());
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join "+player.getServer().getInfo().getName()));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aVerbinde dich auf den Server.").create()));

            if(gameChest.onlineTeam.size() < 1) {
                sender.sendMessage(ChestPrefix.PREFIX_REPORT+"§cEs ist momentan kein Teammitglied online!");
                return;
            }

            gameChest.onlineTeam.forEach(teamPlayer -> {
                teamPlayer.sendMessage(ChestPrefix.PREFIX_REPORT+"§a"+sender.getName()+"§7 hat §c"+player.getName()+"§7 reportet!");
                teamPlayer.sendMessage(new TextComponent("§8\u00BB §7Server: "), accept);
                teamPlayer.sendMessage("§8\u00BB §7Grund: §e"+reason.toString().replace(" §$#~$~§/~", ""));
            });
            sender.sendMessage(ChestPrefix.PREFIX_REPORT+"§eDu hast §c"+player.getName()+"§e erfolgreich gemeldet!");
            return;
        }


        sender.sendMessage(ChestPrefix.PREFIX_REPORT+"§4Der Missbrauch des Befehls kann zu einem Bann führen!");
        sender.sendMessage(ChestPrefix.PREFIX_REPORT+"§c/report <Spieler> [Grund]");
    }
}
