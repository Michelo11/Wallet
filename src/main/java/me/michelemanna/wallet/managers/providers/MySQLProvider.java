package me.michelemanna.wallet.managers.providers;

import me.michelemanna.wallet.WalletPlugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class MySQLProvider implements ConnectionProvider {
    private HikariDataSource dataSource;

    @Override
    public void connect() throws SQLException {
        ConfigurationSection cs = WalletPlugin.getInstance().getConfig().getConfigurationSection("mysql");
        Objects.requireNonNull(cs, "Unable to find the following key: mysql");
        HikariConfig config = new HikariConfig();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        config.setJdbcUrl("jdbc:mysql://" + cs.getString("host") + ":" + cs.getString("port") + "/" + cs.getString("database"));
        config.setUsername(cs.getString("username"));
        config.setPassword(cs.getString("password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(10000);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(60000);
        config.setPoolName("WalletPool");
        config.addDataSourceProperty("useSSL", cs.getBoolean("ssl"));
        config.addDataSourceProperty("allowPublicKeyRetrieval", true);

        this.dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `documents` (" +
                "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY, " +
                "`type` TEXT NOT NULL, " +
                "`player` VARCHAR(36) NOT NULL, " +
                "`arguments` TEXT NOT NULL)");

        statement.close();
        connection.close();
    }

    @Override
    public void disconnect() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}