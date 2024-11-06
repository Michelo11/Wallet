package me.michelemanna.wallet.commands;

import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.subcommands.*;
import org.bukkit.Bukkit;
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
        subCommands.put("give", new GiveCommand());
        subCommands.put("getcard", new GetCardCommand());
        subCommands.put("getatm", new GetATMCommand());
        subCommands.put("help", new HelpCommand());
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
        if (args.length == 1) {
            return new ArrayList<>(this.subCommands.keySet());
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            if (args.length == 3) return new ArrayList<>(WalletPlugin.getInstance().getDocumentTypes().keySet());
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("getcard")) {
            if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("withdraw")) {
            if (args.length == 2) return WalletPlugin.getInstance().getConfig().getIntegerList("banknotes").stream().sorted().map(String::valueOf).toList();
        }

        return new ArrayList<>();
    }
}