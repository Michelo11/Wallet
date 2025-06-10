package me.michelemanna.wallet;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.wallet.commands.WalletCommand;
import me.michelemanna.wallet.config.DocumentType;
import me.michelemanna.wallet.listeners.*;
import me.michelemanna.wallet.managers.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;
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

        if (this.getConfig().get("banknotes") instanceof List<?>) {
            List<Integer> banknotes = this.getConfig().getIntegerList("banknotes");
            this.getConfig().set("banknotes", null);

            for (int banknote: banknotes) {
                this.getConfig().set("banknotes." + banknote, banknote);
            }
        }

        for (String key: this.getConfig().getConfigurationSection("documents").getKeys(false)) {
            String path = "documents." + key;
            Material material = Material.matchMaterial(this.getConfig().getString(path + ".material"));
            int customModelData = this.getConfig().getInt(path + ".custom-model-data");
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
        this.getServer().getPluginManager().registerEvents(new ATMListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("QualityArmoryVehicles2")) {
            this.getServer().getPluginManager().registerEvents(new QAVListener(), this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Phone")) {
            this.getServer().getPluginManager().registerEvents(new PhoneListener(), this);
        }
    }

    @Override
    public void onDisable() {
        this.databaseManager.close();
    }

    public void withdraw(Player player, int amount, UUID cardOwner) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(cardOwner);

        if (!this.getEconomy().has(target, amount)) {
            player.sendMessage(this.getMessage("commands.withdraw.insufficient-balance"));
            return;
        }

        this.getEconomy().withdrawPlayer(target, amount);

        List<Integer> denominations = this.getConfig().getConfigurationSection("banknotes")
            .getKeys(false)
            .stream()
            .map(Integer::parseInt)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        int remainingAmount = amount;
        Map<Integer, Integer> banknotesToGive = new HashMap<>();

        for (int denomination : denominations) {
            if (remainingAmount >= denomination) {
                int count = remainingAmount / denomination;
                banknotesToGive.put(denomination, count);
                remainingAmount -= count * denomination;
            }
        }

        for (Map.Entry<Integer, Integer> entry : banknotesToGive.entrySet()) {
            int denomination = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(this.getMessage("items.banknote.name").replace("%amount%", String.valueOf(denomination)));
                meta.setLore(this.getMessages("items.banknote.lore").stream()
                    .map(s -> s.replace("%amount%", String.valueOf(denomination)))
                    .collect(Collectors.toList()));
                meta.setCustomModelData(this.getConfig().getInt("banknotes." + denomination));

                item.setItemMeta(meta);

                NBT.modify(item, (nbt) -> {
                    nbt.setInteger("banknote", denomination);
                });

                player.getInventory().addItem(item);
            }
        }

        player.sendMessage(this.getMessage("commands.withdraw.success").replace("%amount%", String.valueOf(amount)));
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