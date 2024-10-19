package me.michelemanna.wallet.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class WithdrawCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
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

        if (!WalletPlugin.getInstance().getConfig().getIntegerList("banknotes").contains(amount)) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.withdraw.invalid-amount"));
            return;
        }

        if (!WalletPlugin.getInstance().getEconomy().has(player, amount)) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.withdraw.insufficient-balance"));
            return;
        }

        WalletPlugin.getInstance().getEconomy().withdrawPlayer(player, amount);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(WalletPlugin.getInstance().getMessage("items.banknote.name").replace("%amount%", String.valueOf(amount)));
        meta.setLore(WalletPlugin.getInstance().getMessages("items.banknote.lore").stream().map(s -> s.replace("%amount%", String.valueOf(amount))).collect(Collectors.toList()));

        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.setInteger("banknote", amount);
        });

        player.getInventory().addItem(item);

        player.sendMessage(WalletPlugin.getInstance().getMessage("commands.withdraw.success").replace("%amount%", String.valueOf(amount)));
    }
}