package com.mediasoft.service;

import com.mediasoft.entity.Restaurant;
import com.mediasoft.entity.Review;
import com.mediasoft.repository.RestaurantRepository;
import com.mediasoft.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         RestaurantRepository restaurantRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void save(Review review) {
        reviewRepository.save(review);
        updateRestaurantRating(review.getRestaurantId());
    }

    private void updateRestaurantRating(Long restaurantId) {
        BigDecimal totalRating = BigDecimal.ZERO;
        List<Review> reviews = reviewRepository.findByRestaurant(restaurantId);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);

            totalRating = BigDecimal.valueOf(averageRating).
                    setScale(1, RoundingMode.HALF_UP);
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant != null) {
            restaurant.setRating(totalRating);
        }

    }

    public Review findByVisitorAndRestaurant(Long visitorId, Long restaurantId) {
        return reviewRepository.findByVisitorAndRestaurant(visitorId, restaurantId);
    }

    public void remove(Long visitorId, Long restaurantId) {
        reviewRepository.remove(visitorId, restaurantId);
    }

    public List<Review> findByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurant(restaurantId);
    }

    public List<Review> findByVisitor(Long visitorId) {
        return reviewRepository.findByVisitor(visitorId);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
}
