package me.michelemanna.wallet.commands.subcommands;

import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        for (String message : WalletPlugin.getInstance().getConfig().getStringList("messages.commands.help")) {
            player.sendMessage(color(message));
        }
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}