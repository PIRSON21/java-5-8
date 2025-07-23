package com.mediasoft.integration.service;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.service.impl.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestaurantService restaurantService;

    private RestaurantRequestDTO restaurantRequestDTO1;
    private RestaurantRequestDTO restaurantRequestDTO2;

    @BeforeEach
    void setUp() {
        restaurantRequestDTO1 = new RestaurantRequestDTO(
                "Италия",
                "Уютный итальянский ресторан",
                "ITALIAN",
                BigDecimal.valueOf(1500)
        );

        restaurantRequestDTO2 = new RestaurantRequestDTO(
                "Дракон",
                "Аутентичная китайская кухня",
                "CHINESE",
                BigDecimal.valueOf(2000)
        );
    }

    @Test
    void shouldCreateRestaurant() {
        RestaurantResponseDTO created = restaurantService.create(restaurantRequestDTO1);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Италия");
        assertThat(created.getDescription()).isEqualTo("Уютный итальянский ресторан");
        assertThat(created.getCuisineType()).isEqualTo("ITALIAN");
        assertThat(created.getAvgCheck()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(created.getRating()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldGetAllRestaurants() {
        RestaurantResponseDTO restaurant1 = restaurantService.create(restaurantRequestDTO1);
        RestaurantResponseDTO restaurant2 = restaurantService.create(restaurantRequestDTO2);

        List<RestaurantResponseDTO> restaurants = restaurantService.getAll();

        assertThat(restaurants).hasSize(2);
        assertThat(restaurants)
                .extracting(RestaurantResponseDTO::getName)
                .containsExactlyInAnyOrder("Италия", "Дракон");
    }

    @Test
    void shouldGetRestaurantById() {
        RestaurantResponseDTO created = restaurantService.create(restaurantRequestDTO1);

        RestaurantResponseDTO found = restaurantService.getById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Италия");
        assertThat(found.getDescription()).isEqualTo("Уютный итальянский ресторан");
        assertThat(found.getCuisineType()).isEqualTo("ITALIAN");
        assertThat(found.getAvgCheck()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(found.getRating()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRestaurantNotExists() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> restaurantService.getById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ресторан с ID 999 не найден");
    }

    @Test
    void shouldUpdateRestaurant() {
        RestaurantResponseDTO created = restaurantService.create(restaurantRequestDTO1);

        RestaurantRequestDTO updateRequest = new RestaurantRequestDTO(
                "Новая Италия",
                "Обновленное описание",
                "RUSSIAN",
                BigDecimal.valueOf(1800)
        );

        RestaurantResponseDTO updated = restaurantService.update(created.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Новая Италия");
        assertThat(updated.getDescription()).isEqualTo("Обновленное описание");
        assertThat(updated.getCuisineType()).isEqualTo("RUSSIAN");
        assertThat(updated.getAvgCheck()).isEqualTo(BigDecimal.valueOf(1800));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentRestaurant() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> restaurantService.update(nonExistentId, restaurantRequestDTO1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ресторан с ID 999 не найден");
    }

    @Test
    void shouldDeleteRestaurant() {
        RestaurantResponseDTO created = restaurantService.create(restaurantRequestDTO1);

        restaurantService.delete(created.getId());

        assertThatThrownBy(() -> restaurantService.getById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ресторан с ID " + created.getId() + " не найден");
    }

    @Test
    void shouldSilentlyIgnoreWhenDeletingNonExistentRestaurant() {
        Long nonExistentId = 999L;

        // Должно пройти без исключений
        restaurantService.delete(nonExistentId);
    }

    @Test
    void shouldReturnEmptyListWhenNoRestaurantsExist() {
        List<RestaurantResponseDTO> restaurants = restaurantService.getAll();

        assertThat(restaurants).isEmpty();
    }

    @Test
    void shouldCreateMultipleRestaurantsWithDifferentData() {
        RestaurantRequestDTO restaurant3 = new RestaurantRequestDTO(
                "Фламинго",
                "Французская кухня высокого класса",
                "AMERICAN",
                BigDecimal.valueOf(3000)
        );

        RestaurantResponseDTO created1 = restaurantService.create(restaurantRequestDTO1);
        RestaurantResponseDTO created2 = restaurantService.create(restaurantRequestDTO2);
        RestaurantResponseDTO created3 = restaurantService.create(restaurant3);

        List<RestaurantResponseDTO> allRestaurants = restaurantService.getAll();
        assertThat(allRestaurants).hasSize(3);
        assertThat(allRestaurants)
                .extracting(RestaurantResponseDTO::getName)
                .containsExactlyInAnyOrder("Италия", "Дракон", "Фламинго");

        assertThat(allRestaurants)
                .extracting(RestaurantResponseDTO::getCuisineType)
                .containsExactlyInAnyOrder("ITALIAN", "CHINESE", "AMERICAN");
    }

    @Test
    void shouldMaintainDataIntegrityAfterUpdate() {
        RestaurantResponseDTO created1 = restaurantService.create(restaurantRequestDTO1);
        RestaurantResponseDTO created2 = restaurantService.create(restaurantRequestDTO2);

        RestaurantRequestDTO updateRequest = new RestaurantRequestDTO(
                "Обновленное Название",
                "Обновленное Описание",
                "RUSSIAN",
                BigDecimal.valueOf(2500)
        );

        RestaurantResponseDTO updated = restaurantService.update(created1.getId(), updateRequest);

        RestaurantResponseDTO found1 = restaurantService.getById(created1.getId());
        RestaurantResponseDTO found2 = restaurantService.getById(created2.getId());

        assertThat(found1.getName()).isEqualTo("Обновленное Название");
        assertThat(found1.getDescription()).isEqualTo("Обновленное Описание");
        assertThat(found1.getCuisineType()).isEqualTo("RUSSIAN");
        assertThat(found1.getAvgCheck()).isEqualTo(BigDecimal.valueOf(2500));

        assertThat(found2.getName()).isEqualTo("Дракон");
        assertThat(found2.getDescription()).isEqualTo("Аутентичная китайская кухня");
        assertThat(found2.getCuisineType()).isEqualTo("CHINESE");
        assertThat(found2.getAvgCheck()).isEqualTo(BigDecimal.valueOf(2000));
    }

    @Test
    void shouldSetRatingToZeroWhenCreatingRestaurant() {
        RestaurantResponseDTO created = restaurantService.create(restaurantRequestDTO1);

        assertThat(created.getRating()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCreateRestaurantsWithDifferentAvgChecks() {
        RestaurantRequestDTO lowBudget = new RestaurantRequestDTO(
                "Бюджетное кафе",
                "Недорогая еда",
                "RUSSIAN",
                BigDecimal.valueOf(500)
        );

        RestaurantRequestDTO premium = new RestaurantRequestDTO(
                "Премиум ресторан",
                "Высокая кухня",
                "AMERICAN",
                BigDecimal.valueOf(5000)
        );

        RestaurantResponseDTO budget = restaurantService.create(lowBudget);
        RestaurantResponseDTO premiumRestaurant = restaurantService.create(premium);

        assertThat(budget.getAvgCheck()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(premiumRestaurant.getAvgCheck()).isEqualTo(BigDecimal.valueOf(5000));
    }
}