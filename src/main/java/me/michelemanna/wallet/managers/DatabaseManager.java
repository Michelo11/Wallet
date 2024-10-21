package me.michelemanna.wallet.managers;

import me.michelemanna.wallet.WalletPlugin;

import me.michelemanna.wallet.config.Document;
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

    public void createDocument(Document document) {
        try {
            Connection connection = provider.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO documents (uuid, type, player, arguments) VALUES (?, ?, ?, ?)");

            statement.setString(1, document.getUuid().toString());
            statement.setString(2, document.getType().type());
            statement.setString(3, document.getPlayer().toString());
            statement.setString(4, String.join(";", document.getArguments()));

            statement.executeUpdate();
            statement.close();

            provider.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            provider.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}