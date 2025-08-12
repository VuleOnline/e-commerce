package com.vule.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vule.product_service.dto.ProductDto;
import com.vule.product_service.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
		ProductDto toDto(Product product);

	    @Mapping(target = "id", ignore = true)
	    Product toEntity(ProductDto productDto);

}
