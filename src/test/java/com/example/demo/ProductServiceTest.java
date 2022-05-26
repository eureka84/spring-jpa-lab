package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.products.ProductRepository;
import com.example.demo.products.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
class ProductServiceTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  void test() {
    assertTrue(products.isRunning());
  }


  @Test
  void rollBack() {
    assertThat(productRepository.count()).isZero();

    assertThrows(RuntimeException.class, () -> productService.rollback());

    assertThat(productRepository.count()).isZero();
  }

  @Test
  void commit() {
    assertThat(productRepository.count()).isZero();

    productService.commit();

    assertThat(productRepository.count()).isNotZero();
  }


  private static final MySQLContainer<?> products;
  private static final MySQLContainer<?> stores;

  static {
    products = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("products")
        .withUsername("testcontainers")
        .withPassword("Testcontain3rs!")
        .withReuse(true);
    stores = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("stores")
        .withUsername("testcontainers")
        .withPassword("Testcontain3rs!")
        .withReuse(true);
    products.start();
    stores.start();
  }

  @DynamicPropertySource
  public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
    registry.add("app.datasource.products.url", products::getJdbcUrl);
    registry.add("app.datasource.products.password", products::getPassword);
    registry.add("app.datasource.products.username", products::getUsername);

    registry.add("app.datasource.stores.url", stores::getJdbcUrl);
    registry.add("app.datasource.stores.password", stores::getPassword);
    registry.add("app.datasource.stores.username", stores::getUsername);
  }


}