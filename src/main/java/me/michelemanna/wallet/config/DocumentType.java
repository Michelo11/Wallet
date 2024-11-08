package me.michelemanna.wallet.config;

import de.tr7zw.changeme.nbtapi.NBT;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public record DocumentType(String type, Material material, int customModelData, String name, List<String> lore, int arguments, List<String> hooks) {
    public void give(Player player, String[] args, UUID uuid) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(customModelData);
        meta.setDisplayName(replacePlaceholders(name, player, args));
        meta.setLore(lore.stream().map(line -> replacePlaceholders(line, player, args)).toList());

        item.setItemMeta(meta);

        NBT.modify(item, (nbt) -> {
            nbt.setString("document", uuid.toString());
            nbt.setString("document-type", type);
            nbt.setString("document-owner", player.getUniqueId().toString());
        });

        player.getInventory().addItem(item);
    }

    private String replacePlaceholders(String message, Player player, String[] args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("%args_" + i + "%", args[i]);
        }

        return ChatColor.translateAlternateColorCodes('&',PlaceholderAPI.setPlaceholders(player, message));
    }
}