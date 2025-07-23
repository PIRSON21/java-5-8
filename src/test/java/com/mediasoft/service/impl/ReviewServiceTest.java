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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ReviewMapper mapper;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewRequestDTO requestDTO;
    private ReviewResponseDTO responseDTO;
    private ReviewUpdateRequestDTO updateRequestDTO;
    private Review review;
    private Restaurant restaurant;
    private Visitor visitor;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setRating(BigDecimal.valueOf(4.0));

        visitor = new Visitor();
        visitor.setId(1L);

        review = new Review();
        review.setRestaurant(restaurant);
        review.setVisitor(visitor);
        review.setRating(5);
        review.setComment("Great food!");

        requestDTO = new ReviewRequestDTO(
                1L,
                1L,
                5,
                "Great food!"
        );
        responseDTO = new ReviewResponseDTO(
                1L,
                1L,
                "Great food!",
                5
        );
        updateRequestDTO = new ReviewUpdateRequestDTO(
                "Updated comment",
                4
        );
    }

    @Test
    void create_WhenValidData_ShouldCreateReview() {
        when(mapper.toReview(requestDTO)).thenReturn(review);
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(visitorRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByRestaurantId(1L)).thenReturn(Arrays.asList(review));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.create(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(mapper).toReview(requestDTO);
        verify(restaurantRepository).existsById(1L);
        verify(visitorRepository).existsById(1L);
        verify(reviewRepository).save(review);
        verify(restaurantRepository).save(restaurant);
        verify(mapper).toReviewResponseDTO(review);
    }

    @Test
    void create_WhenRestaurantIsNull_ShouldReturnNull() {
        Review reviewWithNullRestaurant = new Review();
        reviewWithNullRestaurant.setRestaurant(null);
        when(mapper.toReview(requestDTO)).thenReturn(reviewWithNullRestaurant);

        try {
            ReviewResponseDTO result = reviewService.create(requestDTO);
        } catch (ValidationException e) {
            assertEquals("Ресторан не указан", e.getMessage());
        }

        verify(mapper).toReview(requestDTO);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void create_WhenVisitorIsNull_ShouldReturnNull() {
        Review reviewWithNullVisitor = new Review();
        reviewWithNullVisitor.setRestaurant(restaurant);
        reviewWithNullVisitor.setVisitor(null);
        when(mapper.toReview(requestDTO)).thenReturn(reviewWithNullVisitor);

        try {
            ReviewResponseDTO result = reviewService.create(requestDTO);
        } catch (ValidationException e) {
            assertEquals("Посетитель не указан", e.getMessage());
        }

        verify(mapper).toReview(requestDTO);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void create_WhenRestaurantNotExists_ShouldReturnNull() {
        when(mapper.toReview(requestDTO)).thenReturn(review);
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        try {
            ReviewResponseDTO result = reviewService.create(requestDTO);
        } catch (ResourceNotFoundException e) {
            assertEquals("Ресторан или посетитель не найдены", e.getMessage());
        }

        verify(restaurantRepository).existsById(1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void create_WhenVisitorNotExists_ShouldReturnNull() {
        when(mapper.toReview(requestDTO)).thenReturn(review);
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(visitorRepository.existsById(1L)).thenReturn(false);

        try {
            ReviewResponseDTO result = reviewService.create(requestDTO);
        } catch (ResourceNotFoundException e) {
            assertEquals("Ресторан или посетитель не найдены", e.getMessage());
        }

        verify(visitorRepository).existsById(1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getById_WhenReviewExists_ShouldReturnReview() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.of(review));
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(mapper).toReviewResponseDTO(review);
    }

    @Test
    void getById_WhenReviewNotExists_ShouldReturnNull() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.empty());

        try {
            ReviewResponseDTO result = reviewService.getById(1L, 1L);
        } catch (ResourceNotFoundException e) {
            assertEquals("Отзыв не найден для посетителя с ID 1 и ресторана с ID 1", e.getMessage());
        }

        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(mapper, never()).toReviewResponseDTO(any());
    }

    @Test
    void getAll_ShouldReturnAllReviews() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findAll()).thenReturn(reviews);
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        List<ReviewResponseDTO> result = reviewService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));
        verify(reviewRepository).findAll();
        verify(mapper).toReviewResponseDTO(review);
    }

    @Test
    void update_WhenReviewExists_ShouldUpdateReview() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByRestaurantId(1L)).thenReturn(Arrays.asList(review));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.update(1L, 1L, updateRequestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        assertEquals(4.0, review.getRating());
        assertEquals("Updated comment", review.getComment());
        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(reviewRepository).save(review);
        verify(restaurantRepository).save(restaurant);
        verify(mapper).toReviewResponseDTO(review);
    }

    @Test
    void update_WhenReviewNotExists_ShouldReturnNull() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.empty());

        try {
            ReviewResponseDTO result = reviewService.update(1L, 1L, updateRequestDTO);
        } catch (ResourceNotFoundException e) {
            assertEquals("Отзыв не найден для посетителя с ID 1 и ресторана с ID 1", e.getMessage());
        }

        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void delete_WhenReviewExists_ShouldDeleteReview() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.of(review));
        when(reviewRepository.findByRestaurantId(1L)).thenReturn(Arrays.asList());
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        reviewService.delete(1L, 1L);

        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(reviewRepository).delete(review);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void delete_WhenReviewNotExists_ShouldNotDelete() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.empty());

        reviewService.delete(1L, 1L);

        verify(reviewRepository).findByVisitorIdAndRestaurantId(1L, 1L);
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void getReviewsSortedByRatingAsc_ShouldReturnSortedReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAllByOrderByRatingAsc(pageable)).thenReturn(reviewPage);
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        Page<ReviewResponseDTO> result = reviewService.getReviewsSortedByRatingAsc(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(reviewRepository).findAllByOrderByRatingAsc(pageable);
    }

    @Test
    void getReviewsSortedByRatingDesc_ShouldReturnSortedReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findAllByOrderByRatingDesc(pageable)).thenReturn(reviewPage);
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        Page<ReviewResponseDTO> result = reviewService.getReviewsSortedByRatingDesc(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(reviewRepository).findAllByOrderByRatingDesc(pageable);
    }

    @Test
    void updateRestaurantRating_ShouldCalculateAverageRating() {
        Review review1 = new Review();
        review1.setRating(4);
        Review review2 = new Review();
        review2.setRating(5);

        when(mapper.toReview(requestDTO)).thenReturn(review);
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(visitorRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findByRestaurantId(1L)).thenReturn(Arrays.asList(review1, review2));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(mapper.toReviewResponseDTO(review)).thenReturn(responseDTO);

        reviewService.create(requestDTO);

        verify(restaurantRepository).save(restaurant);
        assertEquals(BigDecimal.valueOf(4.5), restaurant.getRating());
    }
}