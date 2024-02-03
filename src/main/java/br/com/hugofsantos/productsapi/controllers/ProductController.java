package br.com.hugofsantos.productsapi.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hugofsantos.productsapi.dtos.ProductDTO;
import br.com.hugofsantos.productsapi.models.Product;
import br.com.hugofsantos.productsapi.repositories.ProductRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/products")
public class ProductController {
  @Autowired
  ProductRepository productRepository;

  @PostMapping()
  public ResponseEntity<Product> saveProduct(
    @RequestBody @Valid ProductDTO productDTO
  ) {
    try {
      final Product product = new Product();
      BeanUtils.copyProperties(productDTO, product);

      final Product savedProduct = this.productRepository.save(product);

      return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping()
  public ResponseEntity<List<Product>> getAllProducts() {
    try {
        final List<Product> products = this.productRepository.findAll();

        for(Product product: products) {
          UUID id = product.getIdProduct();
          product.add(
            linkTo(methodOn(ProductController.class).getOneProductById(id)).withSelfRel()
          );
        }

        return ResponseEntity.status(HttpStatus.OK).body(products);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getOneProductById(@PathVariable(value="id") UUID id) {
    try {
      Optional<Product> productOptional = productRepository.findById(id);
      
      if(productOptional.isEmpty())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");

      final Product product = productOptional.get();
      
      product.add(
        linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products")
      );

      return ResponseEntity.status(HttpStatus.OK).body(product);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateProductById(
    @PathVariable(value = "id") UUID id,
    @RequestBody @Valid ProductDTO productDTO
  ) {
    try {
      final Optional<Product> productOptional = this.productRepository.findById(id);

      if(productOptional.isEmpty())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");

      final Product product = productOptional.get();
      BeanUtils.copyProperties(productDTO, product);

      return ResponseEntity.status(HttpStatus.OK).body(this.productRepository.save(product));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);      
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProductById(@PathVariable(value="id") UUID id) {
    try {
      final Optional<Product> productOptional = this.productRepository.findById(id);

      if(productOptional.isEmpty())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");

      this.productRepository.delete(productOptional.get());

      return ResponseEntity.status(HttpStatus.OK).body(productOptional);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

} 
