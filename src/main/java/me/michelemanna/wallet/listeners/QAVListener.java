package me.michelemanna.wallet.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.config.DocumentType;
import me.zombie_striker.qav.api.events.PlayerEnterQAVehicleEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QAVListener implements Listener {
    private final List<String> required = WalletPlugin.getInstance().getDocumentTypes()
            .values()
            .stream()
            .filter(document -> document.hooks().contains("QualityArmoryVehicles2"))
            .map(DocumentType::type)
            .toList();

    @EventHandler
    public void onVehicleJoin(PlayerEnterQAVehicleEvent event) {
        if (required.isEmpty()) return;

        boolean has = false;

        for (ItemStack item : event.getPlayer().getInventory().getContents()) {
            if (item == null || !item.getType().equals(Material.PAPER)) continue;

            String key = NBT.get(item, (nbt) -> {
                return nbt.getString("document-type");
            });

            if (key == null || !required.contains(key)) continue;

            has = true;
            break;
        }

        if (!has) {
            event.setCanceled(true);
            event.getPlayer().sendMessage(WalletPlugin.getInstance().getMessage("listeners.vehicle.no-license"));
        }
    }
}