package me.michelemanna.wallet.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("wallet.get")) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(WalletPlugin.getInstance().getMessage("items.wallet.name"));

        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.setBoolean("wallet", true);
        });

        player.getInventory().addItem(item);

        player.sendMessage(WalletPlugin.getInstance().getMessage("commands.get.success"));
    }
}