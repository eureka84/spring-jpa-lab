package com.example.demo.products;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional("productsTransactionManager")
  public void rollback(){
    final Product product = createProduct();
    productRepository.save(product);
    throw new RuntimeException("BOOM");
  }

  @Transactional("productsTransactionManager")
  public void commit(){
    final Product product = createProduct();
    productRepository.save(product);
  }

  private Product createProduct() {
    final Product product = new Product();
    product.setName("Whatever");
    return product;
  }

}
