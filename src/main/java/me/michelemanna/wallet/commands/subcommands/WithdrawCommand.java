package me.michelemanna.wallet.commands.subcommands;

import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.entity.Player;

public class WithdrawCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("wallet.withdraw")) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.withdraw.usage"));
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.withdraw.invalid-amount"));
            return;
        }

        WalletPlugin.getInstance().withdraw(player, amount, player.getUniqueId());
    }
}