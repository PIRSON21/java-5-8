package com.mediasoft.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(com.mediasoft.controller.RestaurantController.class)
@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    private RestaurantRequestDTO requestDTO;
    private RestaurantResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test Description",
                "RUSSIAN",
                BigDecimal.valueOf(1500.00)
        );

        responseDTO = new RestaurantResponseDTO(
                1L,
                "Test Restaurant",
                "Test Description",
                "RUSSIAN",
                BigDecimal.valueOf(1500.00),
                BigDecimal.valueOf(4.5)
        );
    }

    @Test
    void create_ShouldReturnCreatedRestaurant() throws Exception {
        when(restaurantService.create(any(RestaurantRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.cuisineType", is("RUSSIAN")))
                .andExpect(jsonPath("$.avgCheck", is(1500.00)))
                .andExpect(jsonPath("$.rating", is(4.5)));

        verify(restaurantService).create(any(RestaurantRequestDTO.class));
    }

    @Test
    void create_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RestaurantRequestDTO invalidRequest = new RestaurantRequestDTO(
                "", // Пустое имя ресторана - должно вызвать валидацию
                "Test Description",
                "RUSSIAN",
                BigDecimal.valueOf(1500.00)
        );

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }

    @Test
    void create_WithNullAvgCheck_ShouldReturnBadRequest() throws Exception {
        RestaurantRequestDTO invalidRequest = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test Description",
                "RUSSIAN",
                null
        );

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }

    @Test
    void create_WithZeroAvgCheck_ShouldReturnBadRequest() throws Exception {
        RestaurantRequestDTO invalidRequest = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test Description",
                "RUSSIAN",
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }

    @Test
    void getAll_ShouldReturnRestaurantsList() throws Exception {
        List<RestaurantResponseDTO> restaurants = Arrays.asList(responseDTO);
        when(restaurantService.getAll()).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Restaurant")))
                .andExpect(jsonPath("$[0].cuisineType", is("RUSSIAN")))
                .andExpect(jsonPath("$[0].avgCheck", is(1500.00)));

        verify(restaurantService).getAll();
    }

    @Test
    void getById_WhenRestaurantExists_ShouldReturnRestaurant() throws Exception {
        when(restaurantService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/restaurants/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.cuisineType", is("RUSSIAN")));

        verify(restaurantService).getById(1L);
    }

    @Test
    void getById_WhenRestaurantNotExists_ShouldReturnNull() throws Exception {
        when(restaurantService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/restaurants/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(restaurantService).getById(999L);
    }

    @Test
    void update_WhenRestaurantExists_ShouldReturnUpdatedRestaurant() throws Exception {
        RestaurantResponseDTO updatedResponse = new RestaurantResponseDTO(
                1L,
                "Updated Restaurant",
                "Updated Description",
                "ITALIAN",
                BigDecimal.valueOf(2000.00),
                BigDecimal.valueOf(4.8)
        );

        when(restaurantService.update(eq(1L), any(RestaurantRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/restaurants/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Restaurant")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.cuisineType", is("ITALIAN")))
                .andExpect(jsonPath("$.avgCheck", is(2000.00)));

        verify(restaurantService).update(eq(1L), any(RestaurantRequestDTO.class));
    }

    @Test
    void update_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RestaurantRequestDTO invalidRequest = new RestaurantRequestDTO(
                "", // Пустое имя ресторана
                "Test Description",
                "", // Пустой тип кухни
                BigDecimal.valueOf(1500.00)
        );

        mockMvc.perform(put("/api/restaurants/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }

    @Test
    void delete_ShouldCallServiceDelete() throws Exception {
        doNothing().when(restaurantService).delete(1L);

        mockMvc.perform(delete("/api/restaurants/{id}", 1L))
                .andExpect(status().isOk());

        verify(restaurantService).delete(1L);
    }

    @Test
    void getRestaurantsWithMinRating_ShouldReturnFilteredRestaurants() throws Exception {
        List<RestaurantResponseDTO> filteredRestaurants = Arrays.asList(responseDTO);
        when(restaurantService.getRestaurantsWithMinRating(4.0)).thenReturn(filteredRestaurants);

        mockMvc.perform(get("/api/restaurants/min-rating/convention")
                        .param("rating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating", is(4.5)))
                .andExpect(jsonPath("$[0].avgCheck", is(1500.00)));

        verify(restaurantService).getRestaurantsWithMinRating(4.0);
    }

    @Test
    void getRestaurantsWithMinRatingJPQL_ShouldReturnFilteredRestaurants() throws Exception {
        List<RestaurantResponseDTO> filteredRestaurants = Arrays.asList(responseDTO);
        when(restaurantService.getRestaurantsWithMinRatingJPQL(4.0)).thenReturn(filteredRestaurants);

        mockMvc.perform(get("/api/restaurants/min-rating/jpql")
                        .param("rating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating", is(4.5)))
                .andExpect(jsonPath("$[0].cuisineType", is("RUSSIAN")));

        verify(restaurantService).getRestaurantsWithMinRatingJPQL(4.0);
    }

    @Test
    void getRestaurantsWithMinRating_WithMissingParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/restaurants/min-rating/convention"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }
}
