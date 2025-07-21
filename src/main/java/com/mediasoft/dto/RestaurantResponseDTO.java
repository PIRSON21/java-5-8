package com.mediasoft.dto;

import com.mediasoft.entity.CuisineType;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestaurantResponseDTO {
    Long id;
    String name;
    String description;
    String cuisineType;
    BigDecimal avgCheck;
    BigDecimal rating;
}
