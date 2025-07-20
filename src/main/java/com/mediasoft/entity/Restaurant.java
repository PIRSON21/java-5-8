package com.mediasoft.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    private Long id;
    private String name;
    private String description;
    private CuisineType cuisineType;
    private BigDecimal avgCheck;
    private BigDecimal rating;
}
