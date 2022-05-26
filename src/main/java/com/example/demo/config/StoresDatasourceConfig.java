package com.example.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.demo.stores",
    entityManagerFactoryRef = "storesEntityManagerFactory",
    transactionManagerRef= "storesTransactionManager"
)
public class StoresDatasourceConfig {

  @Bean
  @ConfigurationProperties("app.datasource.stores")
  public DataSourceProperties storesDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("app.datasource.stores.configuration")
  public DataSource storesDataSource(
      @Qualifier("storesDataSourceProperties") DataSourceProperties dataSourceProperties) {
    return dataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "storesEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean storesEntityManagerFactory(
      @Qualifier("storesDataSource") DataSource dataSource) {
    Properties jpaProperties = new Properties();
    jpaProperties.setProperty("hibernate.show_sql", "true");
    jpaProperties.setProperty("hibernate.format_sql", "true");
    jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(true);
    vendorAdapter.setGenerateDdl(false);
    LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setPackagesToScan("com.example.demo.stores");

    entityManagerFactory.setJpaProperties(jpaProperties);
    return entityManagerFactory;
  }

  @Bean
  public PlatformTransactionManager storesTransactionManager(
      final @Qualifier("storesEntityManagerFactory") LocalContainerEntityManagerFactoryBean storesEntityManagerFactory) {
    return new JpaTransactionManager(storesEntityManagerFactory.getObject());
  }

}
