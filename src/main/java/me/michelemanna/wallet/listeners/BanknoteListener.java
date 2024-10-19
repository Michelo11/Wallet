package me.michelemanna.wallet.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BanknoteListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null || !item.getType().equals(Material.PAPER)) return;

        int amount = NBT.get(item, (nbt) -> {
            if (!nbt.hasTag("banknote")) return -1;

            return nbt.getInteger("banknote");
        });

        if (amount < 1) return;

        event.getPlayer().getInventory().removeItem(item);

        WalletPlugin.getInstance().getEconomy().depositPlayer(event.getPlayer(), amount * item.getAmount());

        event.getPlayer().sendMessage(WalletPlugin.getInstance().getMessage("listeners.deposit.success").replace("%amount%", String.valueOf(amount * item.getAmount())));

        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
}
