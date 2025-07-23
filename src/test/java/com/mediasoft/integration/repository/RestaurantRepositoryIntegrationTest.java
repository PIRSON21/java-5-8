package com.mediasoft.integration.repository;

import com.mediasoft.entity.CuisineType;
import com.mediasoft.entity.Restaurant;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestaurantRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Restaurant restaurant3;

    @BeforeEach
    void setUp() {
        restaurant1 = new Restaurant();
        restaurant1.setName("Ресторан 1");
        restaurant1.setDescription("Описание ресторана 1");
        restaurant1.setCuisineType(CuisineType.ITALIAN);
        restaurant1.setAvgCheck(BigDecimal.valueOf(1500));
        restaurant1.setRating(BigDecimal.valueOf(4.5));

        restaurant2 = new Restaurant();
        restaurant2.setName("Ресторан 2");
        restaurant2.setDescription("Описание ресторана 2");
        restaurant2.setCuisineType(CuisineType.CHINESE);
        restaurant2.setAvgCheck(BigDecimal.valueOf(2000));
        restaurant2.setRating(BigDecimal.valueOf(3.8));

        restaurant3 = new Restaurant();
        restaurant3.setName("Ресторан 3");
        restaurant3.setDescription("Описание ресторана 3");
        restaurant3.setCuisineType(CuisineType.AMERICAN);
        restaurant3.setAvgCheck(BigDecimal.valueOf(500));
        restaurant3.setRating(BigDecimal.valueOf(3.2));
    }

    @Test
    void shouldSaveAndFindRestaurant() {
        Restaurant saved = restaurantRepository.save(restaurant1);
        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ресторан 1");
        assertThat(found.get().getRating()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
        assertThat(found.get().getAvgCheck()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    void shouldFindRestaurantsByRatingGreaterThanEqual() {
        restaurantRepository.saveAll(List.of(restaurant1, restaurant2, restaurant3));

        List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThanEqual(4.0);

        assertThat(restaurants).hasSize(1);
        assertThat(restaurants.get(0).getName()).isEqualTo("Ресторан 1");
        assertThat(restaurants.get(0).getRating()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
    }

    @Test
    void shouldFindRestaurantsWithMinRatingJPQL() {
        restaurantRepository.saveAll(List.of(restaurant1, restaurant2, restaurant3));

        List<Restaurant> restaurants = restaurantRepository.findRestaurantsWithMinRating(3.5);

        assertThat(restaurants).hasSize(2);
        assertThat(restaurants)
                .extracting(Restaurant::getName)
                .containsExactlyInAnyOrder("Ресторан 1", "Ресторан 2");
    }

    @Test
    void shouldReturnEmptyListWhenNoRestaurantsMatchRating() {
        restaurantRepository.saveAll(List.of(restaurant1, restaurant2, restaurant3));

        List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThanEqual(5.0);

        assertThat(restaurants).isEmpty();
    }

    @Test
    void shouldDeleteRestaurant() {
        Restaurant saved = restaurantRepository.save(restaurant1);

        restaurantRepository.deleteById(saved.getId());

        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldUpdateRestaurantRating() {
        Restaurant saved = restaurantRepository.save(restaurant1);

        saved.setRating(BigDecimal.valueOf(5.0));
        Restaurant updated = restaurantRepository.save(saved);

        assertThat(updated.getRating()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
    }
}
