package me.michelemanna.wallet.config;

import me.michelemanna.wallet.WalletPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Document {
    private final UUID uuid = UUID.randomUUID();
    private final DocumentType type;
    private final UUID player;
    private final String[] arguments;

    public Document(DocumentType type, UUID player, String[] arguments) {
        this.type = type;
        this.player = player;
        this.arguments = arguments;
    }

    public void save() {
        WalletPlugin.getInstance().getDatabaseManager().createDocument(this);
    }

    public void give(Player player) {
        this.type.give(player, this.arguments, uuid);
    }

    public DocumentType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getPlayer() {
        return player;
    }

    public String[] getArguments() {
        return arguments;
    }
}
