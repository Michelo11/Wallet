package me.michelemanna.wallet.commands.subcommands;

import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import me.michelemanna.wallet.config.Document;
import me.michelemanna.wallet.config.DocumentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GiveCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("wallet.give")) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.give.usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        DocumentType documentType = WalletPlugin.getInstance().getDocumentTypes().get(args[2]);

        if (documentType == null) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.document-type-not-found"));
            return;
        }

        String[] newArgs = new String[args.length - 3];

        System.arraycopy(args, 3, newArgs, 0, args.length - 3);

        if (newArgs.length < documentType.arguments()) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.give.document-args"));
            return;
        }

        Document document = new Document(documentType, target.getUniqueId(), newArgs);

        document.give(target);
        document.save();

        player.sendMessage(WalletPlugin.getInstance().getMessage("commands.give.success").replace("%player%", target.getName()).replace("%type%", documentType.type()));
    }
}
