package br.com.hugofsantos.productsapi.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hugofsantos.productsapi.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
  
}
