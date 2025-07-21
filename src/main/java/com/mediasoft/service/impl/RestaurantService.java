package com.mediasoft.service.impl;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.entity.Restaurant;
import com.mediasoft.mapper.RestaurantMapper;
import com.mediasoft.repository.impl.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService implements com.mediasoft.service.RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;


    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id);
    }

    public void remove(Long id) {
        restaurantRepository.remove(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public RestaurantResponseDTO create(RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantRequestDTO);
        restaurant.setRating(BigDecimal.ZERO);
        return restaurantMapper.toRestaurantResponseDTO(restaurantRepository.save(restaurant));
    }

    @Override
    public List<RestaurantResponseDTO> getAll() {
        return restaurantRepository.findAll().stream()
                .map(restaurantMapper::toRestaurantResponseDTO)
                .toList();
    }

    @Override
    public RestaurantResponseDTO getById(Long id) {
        return restaurantMapper.toRestaurantResponseDTO(restaurantRepository.findById(id));
    }

    @Override
    public void delete(Long id) {
        restaurantRepository.remove(id);
    }

    @Override
    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant existingRestaurant = restaurantRepository.findById(id);
        if (existingRestaurant == null) {
            return null;
        }
        Restaurant updatedRestaurant = restaurantMapper.toRestaurant(restaurantRequestDTO);
        updatedRestaurant.setId(existingRestaurant.getId());
        return restaurantMapper.toRestaurantResponseDTO(restaurantRepository.save(updatedRestaurant));
    }
}
