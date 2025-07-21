package com.mediasoft.dto;

import com.mediasoft.entity.CuisineType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestaurantRequestDTO {
    @NotBlank(message = "Название ресторана не может быть пустым")
    String name;

    String description;

    @NotBlank(message = "Тип кухни не может быть пустым")
    String cuisineType;

    @NotNull(message = "Средний счет не может быть пустым")
    @DecimalMin(value="0.0", inclusive = false, message = "Средний счет должен быть больше нуля")
    BigDecimal avgCheck;
}