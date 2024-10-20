package me.michelemanna.wallet.commands;

import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.subcommands.GetCommand;
import me.michelemanna.wallet.commands.subcommands.WithdrawCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletCommand implements TabExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public WalletCommand(WalletPlugin plugin) {
        subCommands.put("withdraw", new WithdrawCommand());
        subCommands.put("get", new GetCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(WalletPlugin.getInstance().getMessage("commands.player-only"));
            return true;
        }

        if (args.length == 0) {
            subCommands.get("help").execute((Player) sender, args);
            return true;
        }

        if (subCommands.containsKey(args[0].toLowerCase())) {
            subCommands.get(args[0].toLowerCase()).execute((Player) sender, args);
            return true;
        }

        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>(this.subCommands.keySet());
    }
}