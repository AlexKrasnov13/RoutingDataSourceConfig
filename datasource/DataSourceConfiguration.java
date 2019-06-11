package ru.mdimension.mda.lib.db.datasource;

/**
 * Created by ashamov on 19.12.2016.
 */

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(value = "mda.slave.enable", matchIfMissing = true, havingValue = "false")
public class DataSourceConfiguration {
    private final Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Value("${spring.datasource.hikari.jdbcUrl:#{null}}")
    private String jdbcUrl;

    @Value("${spring.datasource.hikari.maximumPoolSize:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimumIdle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.dataSourceClassName:#{null}}")
    private String dataSourceClassName;

    @Value("${spring.datasource.hikari.driverClassName:#{null}}")
    private String driverClassName;

    @Value("${spring.datasource.username:#{null}}")
    private String username;

    @Value("${spring.datasource.password:#{null}}")
    private String password;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDataSourceClassName(dataSourceClassName);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        log.warn("Configuring solo Datasource if you need master + slave, check mda.slave.enable in application.yml");
        log.warn("Use jdbcUrl= {}, username={} ", jdbcUrl, username);
        return new HikariDataSource(config);
    }
}
