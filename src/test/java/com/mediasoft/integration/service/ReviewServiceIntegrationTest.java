package com.mediasoft.integration.service;

import com.mediasoft.dto.*;
import com.mediasoft.entity.Sex;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.exception.ValidationException;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.service.RestaurantService;
import com.mediasoft.service.VisitorService;
import com.mediasoft.service.impl.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RestaurantService restaurantService;

    private VisitorResponseDTO visitor1;
    private VisitorResponseDTO visitor2;
    private RestaurantResponseDTO restaurant1;
    private RestaurantResponseDTO restaurant2;

    @BeforeEach
    void setUp() {
        // Создаем посетителей
        VisitorRequestDTO visitorRequest1 = new VisitorRequestDTO(
                "Иван Иванов",
                25,
                Sex.MALE.toString()
        );
        visitor1 = visitorService.create(visitorRequest1);

        VisitorRequestDTO visitorRequest2 = new VisitorRequestDTO(
                "Анна Петрова",
                30,
                Sex.FEMALE.toString()
        );
        visitor2 = visitorService.create(visitorRequest2);

        // Создаем рестораны
        RestaurantRequestDTO restaurantRequest1 = new RestaurantRequestDTO(
                "Ресторан 1",
                "Описание ресторана 1",
                "ITALIAN",
                BigDecimal.valueOf(1500)
        );
        restaurant1 = restaurantService.create(restaurantRequest1);

        RestaurantRequestDTO restaurantRequest2 = new RestaurantRequestDTO(
                "Ресторан 2",
                "Описание ресторана 2",
                "CHINESE",
                BigDecimal.valueOf(2000)
        );
        restaurant2 = restaurantService.create(restaurantRequest2);
    }

    @Test
    void shouldCreateReview() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Тут был хороший борщ с капусткой, но не красной. Сосисочки. Ещё есть какой-то непонятный салат, куда крошат морковку, капусту и яблоки с ананасами. Вообще он меня бесит. Вот. Ещё чего. Вкусный чай. Он так утоляет жажду. Я чувствую себя человеком! Я - Никита Литвинков!"
        );

        ReviewResponseDTO created = reviewService.create(reviewRequest);

        assertThat(created).isNotNull();
        assertThat(created.getRestaurantId()).isEqualTo(restaurant1.getId());
        assertThat(created.getVisitorId()).isEqualTo(visitor1.getId());
        assertThat(created.getRating()).isEqualTo(5);
        assertThat(created.getComment()).isEqualTo("Тут был хороший борщ с капусткой, но не красной. Сосисочки. Ещё есть какой-то непонятный салат, куда крошат морковку, капусту и яблоки с ананасами. Вообще он меня бесит. Вот. Ещё чего. Вкусный чай. Он так утоляет жажду. Я чувствую себя человеком! Я - Никита Литвинков!");
    }

    @Test
    void shouldUpdateRestaurantRatingAfterCreatingReview() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                4,
                "Норм"
        );

        reviewService.create(reviewRequest);

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.valueOf(4.0));
    }

    @Test
    void shouldCalculateAverageRatingCorrectly() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor2.getId(),
                3,
                "Средне"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.valueOf(4.0));
    }

    @Test
    void shouldThrowValidationExceptionWhenRestaurantNotSpecified() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                null,
                visitor1.getId(),
                5,
                "Отзыв"
        );

        assertThatThrownBy(() -> reviewService.create(reviewRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Ресторан не указан");
    }

    @Test
    void shouldThrowValidationExceptionWhenVisitorNotSpecified() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                null,
                5,
                "Отзыв"
        );

        assertThatThrownBy(() -> reviewService.create(reviewRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Посетитель не указан");
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRestaurantNotExists() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                999L,
                visitor1.getId(),
                5,
                "Отзыв"
        );

        assertThatThrownBy(() -> reviewService.create(reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ресторан или посетитель не найдены");
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenVisitorNotExists() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                999L,
                5,
                "Отзыв"
        );

        assertThatThrownBy(() -> reviewService.create(reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ресторан или посетитель не найдены");
    }

    @Test
    void shouldGetReviewById() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отличный ресторан!"
        );

        ReviewResponseDTO created = reviewService.create(reviewRequest);
        ReviewResponseDTO found = reviewService.getById(visitor1.getId(), restaurant1.getId());

        assertThat(found).isNotNull();
        assertThat(found.getRestaurantId()).isEqualTo(restaurant1.getId());
        assertThat(found.getVisitorId()).isEqualTo(visitor1.getId());
        assertThat(found.getRating()).isEqualTo(5);
        assertThat(found.getComment()).isEqualTo("Отличный ресторан!");
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenReviewNotExists() {
        assertThatThrownBy(() -> reviewService.getById(999L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Отзыв не найден для посетителя с ID 999 и ресторана с ID 999");
    }

    @Test
    void shouldGetAllReviews() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant2.getId(),
                visitor2.getId(),
                3,
                "Средне"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        List<ReviewResponseDTO> reviews = reviewService.getAll();

        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .extracting(ReviewResponseDTO::getRating)
                .containsExactlyInAnyOrder(5, 3);
    }

    @Test
    void shouldUpdateReview() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отличный ресторан!"
        );

        reviewService.create(reviewRequest);

        ReviewUpdateRequestDTO updateRequest = new ReviewUpdateRequestDTO(
                "Изменил мнение",
                2
        );

        ReviewResponseDTO updated = reviewService.update(visitor1.getId(), restaurant1.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getRating()).isEqualTo(2);
        assertThat(updated.getComment()).isEqualTo("Изменил мнение");
    }

    @Test
    void shouldUpdateRestaurantRatingAfterUpdatingReview() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        reviewService.create(reviewRequest);

        ReviewUpdateRequestDTO updateRequest = new ReviewUpdateRequestDTO(
                "Изменил мнение",
                2
        );

        reviewService.update(visitor1.getId(), restaurant1.getId(), updateRequest);

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.valueOf(2.0));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentReview() {
        ReviewUpdateRequestDTO updateRequest = new ReviewUpdateRequestDTO(
                "Бургер Кинг - говно!",
                1
        );

        assertThatThrownBy(() -> reviewService.update(999L, 999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Отзыв не найден для посетителя с ID 999 и ресторана с ID 999");
    }

    @Test
    void shouldDeleteReview() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отличный ресторан!"
        );

        reviewService.create(reviewRequest);
        reviewService.delete(visitor1.getId(), restaurant1.getId());

        assertThatThrownBy(() -> reviewService.getById(visitor1.getId(), restaurant1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateRestaurantRatingAfterDeletingReview() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor2.getId(),
                3,
                "Средне"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        reviewService.delete(visitor1.getId(), restaurant1.getId());

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.valueOf(3.0));
    }

    @Test
    void shouldSetRestaurantRatingToZeroWhenAllReviewsDeleted() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        reviewService.create(reviewRequest);
        reviewService.delete(visitor1.getId(), restaurant1.getId());

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldSilentlyIgnoreWhenDeletingNonExistentReview() {
        reviewService.delete(999L, 999L);
    }

    @Test
    void shouldGetReviewsSortedByRatingAsc() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant2.getId(),
                visitor2.getId(),
                3,
                "Средне"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsSortedByRatingAsc(pageable);

        assertThat(reviews.getContent()).hasSize(2);
        assertThat(reviews.getContent())
                .extracting(ReviewResponseDTO::getRating)
                .containsExactly(3, 5);
    }

    @Test
    void shouldGetReviewsSortedByRatingDesc() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                5,
                "Отлично!"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant2.getId(),
                visitor2.getId(),
                3,
                "Средне"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsSortedByRatingDesc(pageable);

        assertThat(reviews.getContent()).hasSize(2);
        assertThat(reviews.getContent())
                .extracting(ReviewResponseDTO::getRating)
                .containsExactly(5, 3);
    }

    @Test
    void shouldHandlePaginationForSortedReviews() {
        // Создаем несколько отзывов
        for (int i = 1; i <= 5; i++) {
            VisitorRequestDTO visitorRequest = new VisitorRequestDTO(
                    "Посетитель " + i,
                    20 + i,
                    Sex.MALE.toString()
            );
            VisitorResponseDTO visitor = visitorService.create(visitorRequest);

            ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                    restaurant1.getId(),
                    visitor.getId(),
                    i,
                    "Отзыв " + i
            );
            reviewService.create(reviewRequest);
        }

        Pageable firstPage = PageRequest.of(0, 3);
        Pageable secondPage = PageRequest.of(1, 3);

        Page<ReviewResponseDTO> first = reviewService.getReviewsSortedByRatingAsc(firstPage);
        Page<ReviewResponseDTO> second = reviewService.getReviewsSortedByRatingAsc(secondPage);

        assertThat(first.getContent()).hasSize(3);
        assertThat(second.getContent()).hasSize(2);
        assertThat(first.getTotalElements()).isEqualTo(5);
        assertThat(first.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsExist() {
        List<ReviewResponseDTO> reviews = reviewService.getAll();
        assertThat(reviews).isEmpty();
    }

    @Test
    void shouldMaintainRatingPrecision() {
        ReviewRequestDTO review1 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor1.getId(),
                4,
                "Хорошо"
        );

        ReviewRequestDTO review2 = new ReviewRequestDTO(
                restaurant1.getId(),
                visitor2.getId(),
                5,
                "Отлично"
        );

        reviewService.create(review1);
        reviewService.create(review2);

        RestaurantResponseDTO updatedRestaurant = restaurantService.getById(restaurant1.getId());
        assertThat(updatedRestaurant.getRating()).isEqualTo(BigDecimal.valueOf(4.5));
    }
}
