package me.michelemanna.wallet.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetCardCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("wallet.getcard")) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.getcard.usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(100);
        meta.setDisplayName(WalletPlugin.getInstance().getMessage("items.card.name"));

        item.setItemMeta(meta);

        int pin = (int) (Math.random() * 10000);

        NBT.modify(item, (nbt) -> {
            nbt.setString("card-owner", target.getUniqueId().toString());
            nbt.setInteger("pin", pin);
        });

        player.getInventory().addItem(item);
        player.sendMessage(WalletPlugin.getInstance().getMessage("commands.getcard.success").replace("%player%", target.getName()).replace("%pin%", String.valueOf(pin)));
    }
}