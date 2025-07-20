package com.mediasoft.repository;

import com.mediasoft.entity.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewRepository {
    private final List<Review> reviews = new ArrayList<>();


    public void save(Review review) {
        if (reviews.stream().anyMatch(r -> r.getVisitorId().equals(review.getVisitorId()) && r.getRestaurantId().equals(review.getRestaurantId()))) {
            remove(review.getVisitorId(), review.getRestaurantId());
        }
        reviews.add(review);
    }

    public List<Review> findAll() {
        return new ArrayList<>(reviews);
    }

    public Review findByVisitorAndRestaurant(Long visitorId, Long restaurantId) {
        return reviews.stream()
                .filter(review -> review.getVisitorId().equals(visitorId) && review.getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElse(null);
    }


    public List<Review> findByRestaurant(Long restaurantId) {
        return reviews.stream()
                .filter(review -> review.getRestaurantId().equals(restaurantId))
                .toList();
    }

    public List<Review> findByVisitor(Long visitorId) {
        return reviews.stream()
                .filter(review -> review.getVisitorId().equals(visitorId))
                .toList();
    }
    public void remove(Long visitorId, Long restaurantId) {
        reviews.removeIf(review -> review.getVisitorId().equals(visitorId) && review.getRestaurantId().equals(restaurantId));
    }
}
