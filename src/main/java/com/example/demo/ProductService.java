package com.example.demo;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional
  public void rollback(){
    final Product product = createProduct();
    productRepository.save(product);
    throw new RuntimeException("BOOM");
  }

  @Transactional
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
