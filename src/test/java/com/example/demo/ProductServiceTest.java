package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    assertTrue(mySQLContainer.isRunning());
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


  private static final MySQLContainer<?> mySQLContainer;

  static {
    mySQLContainer = new MySQLContainer<>("mysql:8.0")
        .withUsername("testcontainers")
        .withPassword("Testcontain3rs!")
        .withReuse(true);
    mySQLContainer.start();
  }

  @DynamicPropertySource
  public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
  }


}