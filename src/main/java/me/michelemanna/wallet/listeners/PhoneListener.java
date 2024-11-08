package me.michelemanna.wallet.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.phone.api.events.PhoneEvent;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.config.DocumentType;
import me.zombie_striker.qav.api.events.PlayerEnterQAVehicleEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PhoneListener implements Listener {
    private final List<String> required = WalletPlugin.getInstance().getDocumentTypes()
            .values()
            .stream()
            .filter(document -> document.hooks().contains("Phone"))
            .map(DocumentType::type)
            .toList();

    @EventHandler
    public void onPhone(PhoneEvent event) {
        if (required.isEmpty()) return;

        boolean has = false;
        UUID uuid = null;

        for (ItemStack item : event.getPlayer().getInventory().getContents()) {
            if (item == null || !item.getType().equals(Material.PAPER)) continue;

            String key = NBT.get(item, (nbt) -> {
                return nbt.getString("document-type");
            });

            if (key == null || !required.contains(key)) continue;

            uuid = NBT.get(item, (nbt) -> {
                return UUID.fromString(nbt.getString("document-owner"));
            });

            has = true;
            break;
        }

        if (!has) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(WalletPlugin.getInstance().getMessage("listeners.phone.no-sim"));
            return;
        }

        event.setPhoneOwner(uuid);
    }
}