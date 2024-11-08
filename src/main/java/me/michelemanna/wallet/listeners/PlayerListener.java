package me.michelemanna.wallet.listeners;

import me.michelemanna.wallet.WalletPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!WalletPlugin.getInstance().getConfig().getBoolean("resourcepack.enabled", true)) {
            return;
        }

        event.getPlayer().setResourcePack(WalletPlugin.getInstance().getConfig().getString("resourcepack.url", "https://github.com/Michelo11/Wallet/releases/download/v1.0/Wallet.zip"));
    }
}
