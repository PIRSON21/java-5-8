package com.mediasoft.mapper;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.entity.CuisineType;
import com.mediasoft.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(source = "cuisineType", target = "cuisineType")
    @Mapping(source = "avgCheck", target = "avgCheck")
    Restaurant toRestaurant(RestaurantRequestDTO restaurantRequestDTO);
    @Mapping(source = "cuisineType", target = "cuisineType")
    RestaurantResponseDTO toRestaurantResponseDTO(Restaurant restaurant);

    default String map(CuisineType cuisineType) {
        return cuisineType != null ? cuisineType.name() : null;
    }

    default CuisineType map(String cuisineType) {
        return cuisineType != null ? CuisineType.valueOf(cuisineType) : null;
    }
}
