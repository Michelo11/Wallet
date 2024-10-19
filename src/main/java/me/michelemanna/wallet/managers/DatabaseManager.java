package me.michelemanna.wallet.managers;

import me.michelemanna.wallet.WalletPlugin;

import me.michelemanna.wallet.managers.providers.ConnectionProvider;
import me.michelemanna.wallet.managers.providers.MySQLProvider;
import me.michelemanna.wallet.managers.providers.SQLiteProvider;

import java.sql.*;

public class DatabaseManager {
    private final ConnectionProvider provider;

    public DatabaseManager(WalletPlugin plugin) throws SQLException, ClassNotFoundException {
        String type = plugin.getConfig().getString("mysql.type", "sqlite");

        if (type.equalsIgnoreCase("mysql")) {
            provider = new MySQLProvider();
        } else {
            provider = new SQLiteProvider();
        }

        provider.connect();
    }

    public void close() {
        try {
            provider.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}