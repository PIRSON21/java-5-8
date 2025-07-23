package com.mediasoft.integration.repository;

import com.mediasoft.entity.*;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.repository.RestaurantRepository;
import com.mediasoft.repository.VisitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private com.mediasoft.repository.ReviewRepository reviewRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Visitor visitor1;
    private Visitor visitor2;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        visitor1 = new Visitor();
        visitor1.setName("Иван Иванов");
        visitor1.setAge(25);
        visitor1.setSex(Sex.MALE);
        visitor1 = visitorRepository.save(visitor1);

        visitor2 = new Visitor();
        visitor2.setName("Анна Петрова");
        visitor2.setAge(30);
        visitor2.setSex(Sex.FEMALE);
        visitor2 = visitorRepository.save(visitor2);

        restaurant1 = new Restaurant();
        restaurant1.setName("Ресторан 1");
        restaurant1.setDescription("Описание ресторана 1");
        restaurant1.setCuisineType(CuisineType.ITALIAN);
        restaurant1.setAvgCheck(BigDecimal.valueOf(1500));
        restaurant1.setRating(BigDecimal.ZERO);
        restaurant1 = restaurantRepository.save(restaurant1);

        restaurant2 = new Restaurant();
        restaurant2.setName("Ресторан 2");
        restaurant2.setDescription("Описание ресторана 2");
        restaurant2.setCuisineType(CuisineType.CHINESE);
        restaurant2.setAvgCheck(BigDecimal.valueOf(2000));
        restaurant2.setRating(BigDecimal.ZERO);
        restaurant2 = restaurantRepository.save(restaurant2);

        review1 = new Review();
        review1.setId(new ReviewID(visitor1.getId(), restaurant1.getId()));
        review1.setVisitor(visitor1);
        review1.setRestaurant(restaurant1);
        review1.setRating(5);
        review1.setComment("Отличный ресторан!");

        review2 = new Review();
        review2.setId(new ReviewID(visitor2.getId(), restaurant1.getId()));
        review2.setVisitor(visitor2);
        review2.setRestaurant(restaurant1);
        review2.setRating(3);
        review2.setComment("Средне");

        review3 = new Review();
        review3.setId(new ReviewID(visitor1.getId(), restaurant2.getId()));
        review3.setVisitor(visitor1);
        review3.setRestaurant(restaurant2);
        review3.setRating(4);
        review3.setComment("Хорошо");

        reviewRepository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    void shouldSaveAndFindReviewByCompositeKey() {
        ReviewID reviewId = new ReviewID(visitor1.getId(), restaurant1.getId());

        Optional<Review> found = reviewRepository.findById(reviewId);

        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(5);
        assertThat(found.get().getComment()).isEqualTo("Отличный ресторан!");
        assertThat(found.get().getVisitor().getName()).isEqualTo("Иван Иванов");
        assertThat(found.get().getRestaurant().getName()).isEqualTo("Ресторан 1");
    }

    @Test
    void shouldFindReviewByVisitorIdAndRestaurantId() {
        Optional<Review> found = reviewRepository.findByVisitorIdAndRestaurantId(
                visitor1.getId(), restaurant1.getId()
        );

        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(5);
        assertThat(found.get().getComment()).isEqualTo("Отличный ресторан!");
    }

    @Test
    void shouldFindAllReviewsByVisitorId() {
        List<Review> reviews = reviewRepository.findByVisitorId(visitor1.getId());

        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .extracting(Review::getRating)
                .containsExactlyInAnyOrder(5, 4);
    }

    @Test
    void shouldFindAllReviewsByRestaurantId() {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurant1.getId());

        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .extracting(Review::getRating)
                .containsExactlyInAnyOrder(5, 3);
    }

    @Test
    void shouldFindAllReviewsOrderedByRatingAsc() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewRepository.findAllByOrderByRatingAsc(pageable);

        assertThat(reviews.getContent()).hasSize(3);
        assertThat(reviews.getContent())
                .extracting(Review::getRating)
                .containsExactly(3, 4, 5);
    }

    @Test
    void shouldFindAllReviewsOrderedByRatingDesc() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewRepository.findAllByOrderByRatingDesc(pageable);

        assertThat(reviews.getContent()).hasSize(3);
        assertThat(reviews.getContent())
                .extracting(Review::getRating)
                .containsExactly(5, 4, 3);
    }

    @Test
    void shouldNotFindNonExistentReview() {
        Long nonExistentVisitorId = 999L;
        Long nonExistentRestaurantId = 999L;

        Optional<Review> found = reviewRepository.findByVisitorIdAndRestaurantId(
                nonExistentVisitorId, nonExistentRestaurantId
        );

        assertThat(found).isEmpty();
    }

    @Test
    void shouldDeleteReview() {
        ReviewID reviewId = new ReviewID(visitor1.getId(), restaurant1.getId());

        reviewRepository.deleteById(reviewId);

        Optional<Review> found = reviewRepository.findById(reviewId);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldUpdateReviewRating() {
        ReviewID reviewId = new ReviewID(visitor1.getId(), restaurant1.getId());
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        review.setRating(2);
        review.setComment("Изменил мнение");
        Review updated = reviewRepository.save(review);

        assertThat(updated.getRating()).isEqualTo(2);
        assertThat(updated.getComment()).isEqualTo("Изменил мнение");
    }

    @Test
    void shouldHandlePagination() {
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        Page<Review> first = reviewRepository.findAllByOrderByRatingAsc(firstPage);
        Page<Review> second = reviewRepository.findAllByOrderByRatingAsc(secondPage);

        assertThat(first.getContent()).hasSize(2);
        assertThat(second.getContent()).hasSize(1);
        assertThat(first.getTotalElements()).isEqualTo(3);
        assertThat(first.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldTestCompositeKeyEquality() {
        ReviewID id1 = new ReviewID(visitor1.getId(), restaurant1.getId());
        ReviewID id2 = new ReviewID(visitor1.getId(), restaurant1.getId());
        ReviewID id3 = new ReviewID(visitor2.getId(), restaurant1.getId());

        assertThat(id1).isEqualTo(id2);
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}