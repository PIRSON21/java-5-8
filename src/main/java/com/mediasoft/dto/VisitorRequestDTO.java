package com.mediasoft.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class VisitorRequestDTO {
    String name;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    int age;

    @NotBlank(message = "Пол не может быть пустым")
    String sex;
}


