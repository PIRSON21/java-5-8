package com.mediasoft.service.impl;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.entity.Restaurant;
import com.mediasoft.entity.Review;
import com.mediasoft.entity.Visitor;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.exception.ValidationException;
import com.mediasoft.mapper.ReviewMapper;
import com.mediasoft.repository.RestaurantRepository;
import com.mediasoft.repository.ReviewRepository;
import com.mediasoft.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

        Restaurant restaurant = review.getRestaurant();
        if (restaurant == null || restaurant.getId() == null) {
            throw new ValidationException("Ресторан не указан");
        }
        Visitor visitor = review.getVisitor();
        if (visitor == null || visitor.getId() == null) {
            throw new ValidationException("Посетитель не указан");
        }

        if (!restaurantRepository.existsById(restaurant.getId()) ||
        !visitorRepository.existsById(visitor.getId())) {
            throw new ResourceNotFoundException("Ресторан или посетитель не найдены");
        }
        Review savedReview = reviewRepository.save(review);
        updateRestaurantRating(savedReview.getRestaurant().getId());
        return mapper.toReviewResponseDTO(savedReview);
    }

    @Override
    public ReviewResponseDTO getById(Long visitorId, Long restaurantId) {
        Review review = reviewRepository.findByVisitorIdAndRestaurantId(visitorId, restaurantId).orElseThrow(
                () -> new ResourceNotFoundException("Отзыв не найден для посетителя с ID " + visitorId + " и ресторана с ID " + restaurantId)
        );

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
        Review existingReview = reviewRepository.findByVisitorIdAndRestaurantId(visitorId, restaurantId).orElseThrow(
                () -> new ResourceNotFoundException("Отзыв не найден для посетителя с ID " + visitorId + " и ресторана с ID " + restaurantId)
        );

        existingReview.setRating(reviewRequestDTO.getRating());
        existingReview.setComment(reviewRequestDTO.getComment());
        Review updated = reviewRepository.save(existingReview);
        updateRestaurantRating(updated.getRestaurant().getId());
        return mapper.toReviewResponseDTO(updated);
    }

    @Override
    public void delete(Long visitorId, Long restaurantId) {
        Optional<Review> reviewOpt = reviewRepository.findByVisitorIdAndRestaurantId(visitorId, restaurantId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            reviewRepository.delete(review);
            updateRestaurantRating(review.getRestaurant().getId());
        }
    }

    private void updateRestaurantRating(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        double averageRating = reviews.stream()
                .mapToDouble(review -> review.getRating())
                .average()
                .orElse(0.0);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant != null) {
            restaurant.setRating(BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP));
            restaurantRepository.save(restaurant);
        }
    }

    public Page<ReviewResponseDTO> getReviewsSortedByRatingAsc(Pageable pageable) {
        return reviewRepository.findAllByOrderByRatingAsc(pageable)
                .map(mapper::toReviewResponseDTO);
    }

    public Page<ReviewResponseDTO> getReviewsSortedByRatingDesc(Pageable pageable) {
        return reviewRepository.findAllByOrderByRatingDesc(pageable)
                .map(mapper::toReviewResponseDTO);
    }
}
