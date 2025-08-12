package com.vule.product_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vule.product_service.dto.ProductDto;
import com.vule.product_service.entities.Product;
import com.vule.product_service.mapper.ProductMapper;
import com.vule.product_service.repositories.ProductRepo;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepo productRepo;
    private final ProductMapper productMapper;

	public List<ProductDto> getAllProducts(String sortBy) {
		 List<Product> products;
	        switch (sortBy.toLowerCase()) {
	            case "category":
	                products = productRepo.findAllByOrderByCategoryAsc();
	                break;
	            case "price":
	                products = productRepo.findAllByOrderByPriceAsc();
	                break;
	            default:
	                products = productRepo.findAllByOrderByIdAsc();
	                break;
	        }
	        return products.stream()
	                .map(productMapper::toDto)
	                .collect(Collectors.toList());
	}

	public List<ProductDto> getProductsByCategory(String category) {
		 List<Product> products = productRepo.findByCategory(category);
	        return products.stream()
	                .map(productMapper::toDto)
	                .collect(Collectors.toList());
	}

	public Optional<ProductDto> getProductById(Long id) {
		return productRepo.findById(id)
                .map(productMapper::toDto);
	}
	
	@Transactional
	public ProductDto addProduct(ProductDto productDto) {
	    Product product = productMapper.toEntity(productDto);
	    Product savedProduct = productRepo.save(product);
	    return productMapper.toDto(savedProduct);
	    }

	@Transactional
	public Optional<ProductDto> updateProduct(Long id, @Valid ProductDto productDto) {
		 Optional<Product> existingProduct = productRepo.findById(id);
	        if (existingProduct.isPresent()) {
	            Product updatedProduct = productMapper.toEntity(productDto);
	            updatedProduct.setId(id);
	            productRepo.save(updatedProduct);
	            return Optional.of(productMapper.toDto(updatedProduct));
	        }
	        return Optional.empty();

	}

	@Transactional
	public boolean deleteProduct(Long id) {
		if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
	}

	@Transactional
	public Optional<String> updateStock(Long id, int quantity) {
		 Optional<Product> product = productRepo.findById(id);
	        if (!product.isPresent()) {
	            return Optional.of("Product with ID " + id + " not found.");
	        }
	        Product existingProduct = product.get();
	        if (existingProduct.getStock() < quantity) {
	            return Optional.of("Insufficient stock for product ID " + id);
	        }
	        existingProduct.setStock(existingProduct.getStock() - quantity);
	        productRepo.save(existingProduct);
	        return Optional.of("Stock updated for product ID " + id);
	}

	@Transactional
	public Optional<String> increaseStock(Long id, int quantity) {
		 Optional<Product> product = productRepo.findById(id);
	        if (!product.isPresent()) {
	            return Optional.of("Product with ID " + id + " not found.");
	        }
	        Product existingProduct = product.get();
	        existingProduct.setStock(existingProduct.getStock() + quantity);
	        productRepo.save(existingProduct);
	        return Optional.of("Stock increased for product ID " + id);
	}

	

}
