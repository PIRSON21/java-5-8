package com.mediasoft.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ReviewRequestDTO {
    @NotNull(message = "ID ресторана не может быть пустым")
    Long restaurantId;

    @NotNull(message = "ID посетителя не может быть пустым")
    Long visitorId;

    @Min(value = 1, message = "Рейтинг должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг должен быть от 1 до 5")
    int rating;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    String comment;
}
