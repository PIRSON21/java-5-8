package com.mediasoft.repository.impl;

import com.mediasoft.entity.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

//@Repository
//public class RestaurantRepository {
//    private final List<Restaurant> restaurants = new ArrayList<>();
//
//    public Restaurant save(Restaurant restaurant) {
//        for (int i = 0; i < restaurants.size(); i++) {
//            if (restaurants.get(i).getId().equals(restaurant.getId())) {
//                restaurants.set(i, restaurant);
//                return restaurant;
//            }
//        }
//        restaurant.setId((long) (restaurants.size() + 1));
//        restaurants.add(restaurant);
//
//        return restaurant;
//    }
//
//    public List<Restaurant> findAll() {
//        return new ArrayList<>(restaurants);
//    }
//
//    public Restaurant findById(Long id) {
//        return restaurants.stream()
//                .filter(restaurant -> restaurant.getId().equals(id))
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void remove(Long id) {
//        restaurants.removeIf(restaurant -> restaurant.getId().equals(id));
//    }
//}
