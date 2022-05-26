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
    basePackages = "com.example.demo.products",
    entityManagerFactoryRef = "productsEntityManagerFactory",
    transactionManagerRef= "productsTransactionManager"
)
public class ProductsDatasourceConfig {

  @Bean
  @ConfigurationProperties("app.datasource.products")
  public DataSourceProperties productsDataSourceProperties() {
    return new DataSourceProperties();
  }
  @Bean
  @ConfigurationProperties("app.datasource.products.configuration")
  public DataSource productsDataSource(
      @Qualifier("productsDataSourceProperties") DataSourceProperties dataSourceProperties) {
    final HikariDataSource hikariDataSource = dataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
    hikariDataSource.setAutoCommit(true);
    return hikariDataSource;
  }

  @Bean(name = "productsEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean productsEntityManagerFactory(
      @Qualifier("productsDataSource") DataSource dataSource) {
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
    entityManagerFactory.setPackagesToScan("com.example.demo.products");

    entityManagerFactory.setJpaProperties(jpaProperties);
    return entityManagerFactory;
  }

  @Bean
  public PlatformTransactionManager productsTransactionManager(
      final @Qualifier("productsEntityManagerFactory") LocalContainerEntityManagerFactoryBean productsEntityManagerFactory) {
    return new JpaTransactionManager(productsEntityManagerFactory.getObject());
  }

}
