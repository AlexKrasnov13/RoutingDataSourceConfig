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
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(value = "mda.slave.enable", havingValue = "true")
public class RoutingDataSourceConfiguration {
    private final Logger log = LoggerFactory.getLogger(RoutingDataSourceConfiguration.class);

    @Value("${spring.datasource.hikari.jdbcUrl:#{null}}")
    private String masterJdbcUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String masterUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String masterPassword;

    @Value("${spring.datasource.hikari.maximumPoolSize:10}")
    private int masterMaximumPoolSize;

    @Value("${spring.datasource.hikari.minimumIdle:5}")
    private int masterMinimumIdle;

    @Value("${spring.datasource.hikari.dataSourceClassName:#{null}}")
    private String masterDataSourceClassName;

    @Value("${spring.datasource.hikari.driverClassName:#{null}}")
    private String masterDriverClassName;

    @Value("${spring.slave-datasource.hikari.jdbcUrl:#{null}}")
    private String slaveJdbcUrl;

    @Value("${spring.slave-datasource.username:#{null}}")
    private String slaveUsername;

    @Value("${spring.slave-datasource.password:#{null}}")
    private String slavePassword;

    @Value("${spring.slave-datasource.hikari.maximumPoolSize:10}")
    private int slaveMaximumPoolSize;

    @Value("${spring.slave-datasource.hikari.minimumIdle:5}")
    private int slaveMinimumIdle;

    @Bean(name = "routingDataSource")
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource master = masterDataSource();
        DataSource slave = slaveDataSource();
        targetDataSources.put(DbType.MASTER,
            master);
        targetDataSources.put(DbType.SLAVE,
            slave);

        RoutingDataSource routingDataSource
            = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(master);
        return routingDataSource;
    }

    @Bean
    public DataSource masterDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(masterJdbcUrl);
        config.setUsername(masterUsername);
        config.setPassword(masterPassword);
        config.setDataSourceClassName(masterDataSourceClassName);
        config.setDriverClassName(masterDriverClassName);
        config.setMaximumPoolSize(masterMaximumPoolSize);
        config.setMinimumIdle(masterMinimumIdle);
        log.warn("Configuring Master Datasource");
        log.warn("Use jdbcUrl= {}, username={} ", masterJdbcUrl, masterUsername);

        return new HikariDataSource(config);
    }

    @Bean
    public DataSource slaveDataSource() {
        HikariConfig config = new HikariConfig();
        if (hasSlaveDataSource()) {
            config.setJdbcUrl(slaveJdbcUrl);
            config.setUsername(slaveUsername);
            config.setPassword(slavePassword);
            config.setDataSourceClassName(masterDataSourceClassName);
            config.setDriverClassName(masterDriverClassName);
            config.setMaximumPoolSize(slaveMaximumPoolSize);
            config.setMinimumIdle(slaveMinimumIdle);
            log.warn("Configuring Slave Datasource");
            log.warn("Use jdbcUrl= {}, username={} ", slaveJdbcUrl, slaveUsername);
            return new HikariDataSource(config);
        } else {
            log.warn("The application will use MASTER DATASOURCE ONLY. Please check your application.yml");
            return masterDataSource();
        }
    }

    private boolean hasSlaveDataSource() {
        return slaveJdbcUrl != null && slaveUsername != null ;
    }
}
