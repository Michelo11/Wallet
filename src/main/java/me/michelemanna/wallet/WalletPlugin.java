package me.michelemanna.wallet;

import me.michelemanna.wallet.commands.WalletCommand;
import me.michelemanna.wallet.config.DocumentType;
import me.michelemanna.wallet.listeners.BanknoteListener;
import me.michelemanna.wallet.listeners.QAVListener;
import me.michelemanna.wallet.managers.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class WalletPlugin extends JavaPlugin {
    private static WalletPlugin instance;
    private DatabaseManager databaseManager;
    private Economy economy;
    final Map<String, DocumentType> documentTypes = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;

        for (String key: this.getConfig().getConfigurationSection("documents").getKeys(false)) {
            String path = "documents." + key;
            Material material = Material.matchMaterial(this.getConfig().getString(path + ".material"));
            int customModelData = this.getConfig().getInt(path + ".customModelData");
            String name = this.getConfig().getString(path + ".name");
            List<String> lore = this.getConfig().getStringList(path + ".lore");
            int arguments = this.getConfig().getInt(path + ".arguments");
            List<String> hooks = this.getConfig().getStringList(path + ".hooks");
            this.documentTypes.put(key, new DocumentType(key, material, customModelData, name, lore, arguments, hooks));
        }

        try {
            this.databaseManager = new DatabaseManager(this);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.economy = this.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        this.getCommand("wallet").setExecutor(new WalletCommand(this));
        this.getServer().getPluginManager().registerEvents(new BanknoteListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("QualityArmoryVehicles2")) {
            this.getServer().getPluginManager().registerEvents(new QAVListener(), this);
        }
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

    public Map<String, DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}