package com.mediasoft.repository;

import com.mediasoft.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);
    List<Restaurant> findAll();
    Restaurant findById(Long id);
    void remove(Long id);
}
