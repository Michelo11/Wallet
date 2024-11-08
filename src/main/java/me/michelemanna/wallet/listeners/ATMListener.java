package me.michelemanna.wallet.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.michelemanna.wallet.WalletPlugin;
import me.michelemanna.wallet.conversations.PinConversation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ATMListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getItemInHand().getType().equals(Material.valueOf(WalletPlugin.getInstance().getConfig().getString("atm-material")))) return;

        boolean atm = NBT.get(event.getItemInHand(), (nbt) -> {
            return nbt.hasTag("is-atm");
        });

        if (!atm) return;

        ReadWriteNBT nbt = new NBTBlock(event.getBlock()).getData();

        nbt.setBoolean("is-atm", true);
    }

    @EventHandler
    public  void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasItem()) return;

        Block block = event.getClickedBlock();

        if (block == null || !block.getType().equals(Material.valueOf(WalletPlugin.getInstance().getConfig().getString("atm-material")))) return;

        ReadWriteNBT nbt = new NBTBlock(block).getData();

        if (!nbt.getBoolean("is-atm")) return;

        ItemStack hand = event.getItem();

        if (hand == null || !hand.getType().equals(Material.PAPER)) return;

        int pin = NBT.get(hand, (item) -> {
            return item.getInteger("pin");
        });

        String cardOwner = NBT.get(hand, (item) -> {
            if (!item.hasTag("card-owner")) return null;

            return item.getString("card-owner");
        });

        if (cardOwner == null) return;

        new ConversationFactory(WalletPlugin.getInstance())
                .withEscapeSequence("cancel")
                .withModality(false)
                .withLocalEcho(false)
                .withFirstPrompt(new PinConversation(pin, UUID.fromString(cardOwner)))
                .buildConversation(event.getPlayer())
                .begin();
    }
}