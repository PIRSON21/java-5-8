package com.mediasoft.service.impl;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.entity.Restaurant;
import com.mediasoft.entity.Review;
import com.mediasoft.mapper.ReviewMapper;
import com.mediasoft.repository.impl.RestaurantRepository;
import com.mediasoft.repository.impl.ReviewRepository;
import com.mediasoft.repository.impl.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements com.mediasoft.service.ReviewService {
    private final ReviewRepository reviewRepository;
    private final VisitorRepository visitorRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewMapper mapper;

    @Override
    public ReviewResponseDTO create(ReviewRequestDTO reviewRequestDTO) {
        Review review = mapper.toReview(reviewRequestDTO);
        if (save(review)) {
            return mapper.toReviewResponseDTO(review);
        }
        return null;
    }


    public boolean save(Review review) {
        if (restaurantRepository.findById(review.getRestaurantId()) == null) {
            return false;
        }
        if (visitorRepository.findById(review.getVisitorId()) == null) {
            return false;
        }
        reviewRepository.save(review);
        updateRestaurantRating(review.getRestaurantId());
        return true;
    }

    @Override
    public ReviewResponseDTO getById(Long visitorId, Long restaurantId) {
        Review review = reviewRepository.findByVisitorAndRestaurant(visitorId, restaurantId);
        if (review == null) {
            return null;
        }

        return mapper.toReviewResponseDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getAll() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(mapper::toReviewResponseDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO update(Long visitorId, Long restaurantId, ReviewUpdateRequestDTO reviewRequestDTO) {
        Review existingReview = reviewRepository.findByVisitorAndRestaurant(visitorId, restaurantId);
        if (existingReview == null) {
            return null;
        }

        existingReview.setRating(reviewRequestDTO.getRating());
        existingReview.setComment(reviewRequestDTO.getComment());
        if (save(existingReview)) {
            return mapper.toReviewResponseDTO(existingReview);
        }
        return null;
    }

    @Override
    public void delete(Long visitorId, Long restaurantId) {
        Review review = reviewRepository.findByVisitorAndRestaurant(visitorId, restaurantId);
        if (review != null) {
            reviewRepository.remove(review.getVisitorId(), review.getRestaurantId());
            updateRestaurantRating(review.getRestaurantId());
        }
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


    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
}
