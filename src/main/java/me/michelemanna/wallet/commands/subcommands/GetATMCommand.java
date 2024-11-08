package me.michelemanna.wallet.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetATMCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("wallet.getatm")) {
            player.sendMessage(WalletPlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        ItemStack item = new ItemStack(Material.valueOf(WalletPlugin.getInstance().getConfig().getString("atm-material")));
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(101);
        meta.setDisplayName(WalletPlugin.getInstance().getMessage("items.atm.name"));

        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.setBoolean("is-atm", true);
        });

        player.getInventory().addItem(item);
    }
}
