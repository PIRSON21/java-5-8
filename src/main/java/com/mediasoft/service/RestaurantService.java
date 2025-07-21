package com.mediasoft.service;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;

import java.util.List;

public interface RestaurantService {
    RestaurantResponseDTO create(RestaurantRequestDTO restaurantRequestDTO);
    List<RestaurantResponseDTO> getAll();
    RestaurantResponseDTO getById(Long id);
    void delete(Long id);
    RestaurantResponseDTO update(Long id, RestaurantRequestDTO restaurantRequestDTO);
    List<RestaurantResponseDTO> getRestaurantsWithMinRating(double rating);
    List<RestaurantResponseDTO> getRestaurantsWithMinRatingJPQL(double rating);
}
