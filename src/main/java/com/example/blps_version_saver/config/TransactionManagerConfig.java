package com.example.blps_version_saver.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private Environment environment;

    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean myDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("postgres");
        dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
        Properties xaProperties = new Properties();
        xaProperties.put("databaseName", "postgres");
        xaProperties.setProperty("user", environment.getProperty("spring.datasource.username"));
        xaProperties.setProperty("password", environment.getProperty("spring.datasource.password"));
        xaProperties.setProperty("serverName", "localhost");
        xaProperties.setProperty("portNumber", "5432");
        dataSource.setXaProperties(xaProperties);
        dataSource.setPoolSize(10);
        return dataSource;
    }

    @Bean
    public UserTransactionImp myTransactionImp() {
        return new UserTransactionImp();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager userTransactionManager() throws SystemException {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setTransactionTimeout(300);
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean
    public JtaTransactionManager transactionManager() throws SystemException {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager());
        jtaTransactionManager.setUserTransaction(userTransactionManager());
        return jtaTransactionManager;
    }
}
