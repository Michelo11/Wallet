package me.michelemanna.wallet;

import me.michelemanna.wallet.commands.WalletCommand;
import me.michelemanna.wallet.listeners.BanknoteListener;
import me.michelemanna.wallet.managers.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public final class WalletPlugin extends JavaPlugin {
    private static WalletPlugin instance;
    private DatabaseManager databaseManager;
    private Economy economy;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;

        try {
            this.databaseManager = new DatabaseManager(this);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.economy = this.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        this.getCommand("wallet").setExecutor(new WalletCommand(this));
        this.getServer().getPluginManager().registerEvents(new BanknoteListener(), this);
    }

    @Override
    public void onDisable() {
        this.databaseManager.close();
    }

    public List<String> getMessages(String path) {
        return this.getConfig().getStringList("messages." + path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages." + path, "&cMessage not found: " + path));
    }

    public Economy getEconomy() {
        return economy;
    }

    public static WalletPlugin getInstance() {
        return instance;
    }
}