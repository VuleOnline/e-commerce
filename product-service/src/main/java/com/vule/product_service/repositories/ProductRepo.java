package com.vule.product_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vule.product_service.entities.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {

	List<Product> findByCategory(String category);

	List<Product> findAllByOrderByCategoryAsc();

	List<Product> findAllByOrderByPriceAsc();

	List<Product> findAllByOrderByIdAsc();
	
	

}
