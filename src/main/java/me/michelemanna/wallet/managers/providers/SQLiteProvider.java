package me.michelemanna.wallet.managers.providers;

import me.michelemanna.wallet.WalletPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteProvider implements ConnectionProvider {
    private Connection connection;

    @Override
    public void connect() throws SQLException {
        File file = new File(WalletPlugin.getInstance().getDataFolder(), "database.db");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        Statement statement = connection.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `document` (" +
                "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY, " +
                "`type` TEXT NOT NULL, " +
                "`player` VARCHAR(36) NOT NULL, " +
                "`metadata` TEXT NOT NULL)");

        statement.close();
    }

    @Override
    public void disconnect() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {}
}