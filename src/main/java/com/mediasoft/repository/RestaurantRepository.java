package com.mediasoft.repository;

import com.mediasoft.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByRatingGreaterThanEqual(Double rating);

    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :rating")
    List<Restaurant> findRestaurantsWithMinRating(double rating);
}
