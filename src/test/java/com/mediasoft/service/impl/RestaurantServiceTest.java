package com.mediasoft.service.impl;

import com.mediasoft.dto.RestaurantRequestDTO;
import com.mediasoft.dto.RestaurantResponseDTO;
import com.mediasoft.entity.Restaurant;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.mapper.RestaurantMapper;
import com.mediasoft.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    private RestaurantRequestDTO requestDTO;
    private RestaurantResponseDTO responseDTO;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        requestDTO = new RestaurantRequestDTO(
                "Test Restaurant",
                "A cozy place with great food",
                "Italian",
                BigDecimal.valueOf(20.0)
        );
        responseDTO = new RestaurantResponseDTO(
                1L,
                "Test Restaurant",
                "A cozy place with great food",
                "Italian",
                BigDecimal.valueOf(20.0),
                BigDecimal.valueOf(4.5)
        );
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setRating(BigDecimal.valueOf(4.5));
    }

    @Test
    void create_ShouldCreateRestaurant() {
        when(restaurantMapper.toRestaurant(requestDTO)).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(restaurantMapper.toRestaurantResponseDTO(restaurant)).thenReturn(responseDTO);

        RestaurantResponseDTO result = restaurantService.create(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(restaurantMapper).toRestaurant(requestDTO);
        verify(restaurantRepository).save(restaurant);
        verify(restaurantMapper).toRestaurantResponseDTO(restaurant);
        assertEquals(BigDecimal.ZERO, restaurant.getRating());
    }

    @Test
    void getAll_ShouldReturnAllRestaurants() {
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        List<RestaurantResponseDTO> expectedResponse = Arrays.asList(responseDTO);

        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(restaurantMapper.toRestaurantResponseDTO(restaurant)).thenReturn(responseDTO);

        List<RestaurantResponseDTO> result = restaurantService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);
        verify(restaurantRepository).findAll();
        verify(restaurantMapper).toRestaurantResponseDTO(restaurant);
    }

    @Test
    void getById_WhenRestaurantExists_ShouldReturnRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toRestaurantResponseDTO(restaurant)).thenReturn(responseDTO);

        RestaurantResponseDTO result = restaurantService.getById(1L);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper).toRestaurantResponseDTO(restaurant);
    }

    @Test
    void getById_WhenRestaurantNotExists_ShouldReturnNull() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            RestaurantResponseDTO result = restaurantService.getById(1L);
        } catch (ResourceNotFoundException ex) {
            assertEquals("Ресторан с ID 1 не найден", ex.getMessage());
        }

        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper, never()).toRestaurantResponseDTO(any());
    }

    @Test
    void delete_WhenRestaurantExists_ShouldDeleteRestaurant() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        restaurantService.delete(1L);

        verify(restaurantRepository).existsById(1L);
        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void delete_WhenRestaurantNotExists_ShouldNotDelete() {
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        restaurantService.delete(1L);

        verify(restaurantRepository).existsById(1L);
        verify(restaurantRepository, never()).deleteById(anyLong());
    }

    @Test
    void update_WhenRestaurantExists_ShouldUpdateRestaurant() {
        Restaurant updatedRestaurant = new Restaurant();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toRestaurant(requestDTO)).thenReturn(updatedRestaurant);
        when(restaurantRepository.save(updatedRestaurant)).thenReturn(updatedRestaurant);
        when(restaurantMapper.toRestaurantResponseDTO(updatedRestaurant)).thenReturn(responseDTO);

        RestaurantResponseDTO result = restaurantService.update(1L, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        assertEquals(1L, updatedRestaurant.getId());
        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper).toRestaurant(requestDTO);
        verify(restaurantRepository).save(updatedRestaurant);
        verify(restaurantMapper).toRestaurantResponseDTO(updatedRestaurant);
    }

    @Test
    void update_WhenRestaurantNotExists_ShouldReturnNull() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            RestaurantResponseDTO result = restaurantService.update(1L, requestDTO);
        }
        catch (ResourceNotFoundException ex) {
            assertEquals("Ресторан с ID 1 не найден", ex.getMessage());
        }


        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper, never()).toRestaurant(any());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void getRestaurantsWithMinRating_ShouldReturnFilteredRestaurants() {
        double rating = 4.0;
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        List<RestaurantResponseDTO> expectedResponse = Arrays.asList(responseDTO);

        when(restaurantRepository.findByRatingGreaterThanEqual(rating)).thenReturn(restaurants);
        when(restaurantMapper.toRestaurantResponseDTO(restaurant)).thenReturn(responseDTO);

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsWithMinRating(rating);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);
        verify(restaurantRepository).findByRatingGreaterThanEqual(rating);
        verify(restaurantMapper).toRestaurantResponseDTO(restaurant);
    }

    @Test
    void getRestaurantsWithMinRatingJPQL_ShouldReturnFilteredRestaurants() {
        double rating = 4.0;
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        List<RestaurantResponseDTO> expectedResponse = Arrays.asList(responseDTO);

        when(restaurantRepository.findRestaurantsWithMinRating(rating)).thenReturn(restaurants);
        when(restaurantMapper.toRestaurantResponseDTO(restaurant)).thenReturn(responseDTO);

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsWithMinRatingJPQL(rating);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);
        verify(restaurantRepository).findRestaurantsWithMinRating(rating);
        verify(restaurantMapper).toRestaurantResponseDTO(restaurant);
    }
}
