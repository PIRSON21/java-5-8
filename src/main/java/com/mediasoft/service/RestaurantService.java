package com.mediasoft.service;

import com.mediasoft.entity.Restaurant;
import com.mediasoft.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

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

}
