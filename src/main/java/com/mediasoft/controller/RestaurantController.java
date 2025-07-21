package com.mediasoft.controller;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public RestaurantResponseDTO create(@Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        return restaurantService.create(restaurantRequestDTO);
    }

    @GetMapping
    public List<RestaurantResponseDTO> getAll() {
        return restaurantService.getAll();
    }

    @GetMapping("/{id}")
    public RestaurantResponseDTO getById(@PathVariable Long id) {
        return restaurantService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponseDTO update(@PathVariable Long id, @Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        return restaurantService.update(id, restaurantRequestDTO);
    }
}
