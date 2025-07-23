package com.mediasoft.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediasoft.controller.ReviewController;
import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReviewController.class)
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewRequestDTO requestDTO;
    private ReviewResponseDTO responseDTO;
    private ReviewUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ReviewRequestDTO(1L, 1L, 5, "Отличный ресторан!");
        responseDTO = new ReviewResponseDTO(1L, 1L, "Отличный ресторан!", 5);
        updateRequestDTO = new ReviewUpdateRequestDTO("Обновленный отзыв", 4);
    }

    @Test
    void create_ShouldReturnCreatedReview() throws Exception {
        // Given
        when(reviewService.create(any(ReviewRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.visitorId", is(1)))
                .andExpect(jsonPath("$.comment", is("Отличный ресторан!")))
                .andExpect(jsonPath("$.rating", is(5)));

        verify(reviewService).create(any(ReviewRequestDTO.class));
    }

    @Test
    void create_WithNullRestaurantId_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(null, 1L, 5, "Отзыв");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void create_WithNullVisitorId_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(1L, null, 5, "Отзыв");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void create_WithInvalidRatingTooLow_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(1L, 1L, 0, "Отзыв");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void create_WithInvalidRatingTooHigh_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(1L, 1L, 6, "Отзыв");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void create_WithBlankComment_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(1L, 1L, 5, "");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void getById_WhenReviewExists_ShouldReturnReview() throws Exception {
        // Given
        when(reviewService.getById(1L, 1L)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/reviews/{restaurantId}/{visitorId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.visitorId", is(1)))
                .andExpect(jsonPath("$.comment", is("Отличный ресторан!")))
                .andExpect(jsonPath("$.rating", is(5)));

        verify(reviewService).getById(1L, 1L);
    }

    @Test
    void getById_WhenReviewNotExists_ShouldReturnNull() throws Exception {
        // Given
        when(reviewService.getById(999L, 999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/reviews/{restaurantId}/{visitorId}", 999L, 999L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(reviewService).getById(999L, 999L);
    }

    @Test
    void getAll_ShouldReturnReviewsList() throws Exception {
        // Given
        ReviewResponseDTO secondReview = new ReviewResponseDTO(2L, 2L, "Хороший сервис", 4);
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO, secondReview);
        when(reviewService.getAll()).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].restaurantId", is(1)))
                .andExpect(jsonPath("$[0].visitorId", is(1)))
                .andExpect(jsonPath("$[0].rating", is(5)))
                .andExpect(jsonPath("$[1].restaurantId", is(2)))
                .andExpect(jsonPath("$[1].visitorId", is(2)))
                .andExpect(jsonPath("$[1].rating", is(4)));

        verify(reviewService).getAll();
    }

    @Test
    void getAll_WhenNoReviews_ShouldReturnEmptyList() throws Exception {
        // Given
        when(reviewService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(reviewService).getAll();
    }

    @Test
    void update_WhenReviewExists_ShouldReturnUpdatedReview() throws Exception {
        // Given
        ReviewResponseDTO updatedResponse = new ReviewResponseDTO(1L, 1L, "Обновленный отзыв", 4);
        when(reviewService.update(eq(1L), eq(1L), any(ReviewUpdateRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/reviews/{restaurantId}/{visitorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.visitorId", is(1)))
                .andExpect(jsonPath("$.comment", is("Обновленный отзыв")))
                .andExpect(jsonPath("$.rating", is(4)));

        verify(reviewService).update(eq(1L), eq(1L), any(ReviewUpdateRequestDTO.class));
    }

    @Test
    void update_WithInvalidRating_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewUpdateRequestDTO invalidRequest = new ReviewUpdateRequestDTO("Отзыв", 6);

        // When & Then
        mockMvc.perform(put("/api/reviews/{restaurantId}/{visitorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void update_WithBlankComment_ShouldReturnBadRequest() throws Exception {
        // Given
        ReviewUpdateRequestDTO invalidRequest = new ReviewUpdateRequestDTO("", 4);

        // When & Then
        mockMvc.perform(put("/api/reviews/{restaurantId}/{visitorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reviewService);
    }

    @Test
    void delete_ShouldCallServiceDelete() throws Exception {
        // Given
        doNothing().when(reviewService).delete(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/api/reviews/{restaurantId}/{visitorId}", 1L, 1L))
                .andExpect(status().isOk());

        verify(reviewService).delete(1L, 1L);
    }

    @Test
    void delete_WithNonExistentIds_ShouldStillReturn200() throws Exception {
        // Given
        doNothing().when(reviewService).delete(999L, 999L);

        // When & Then
        mockMvc.perform(delete("/api/reviews/{restaurantId}/{visitorId}", 999L, 999L))
                .andExpect(status().isOk());

        verify(reviewService).delete(999L, 999L);
    }

    @Test
    void getReviewsSortedByRatingAsc_ShouldReturnPagedResults() throws Exception {
        // Given
        List<ReviewResponseDTO> reviews = Arrays.asList(
                new ReviewResponseDTO(3L, 3L, "Средний ресторан", 3),
                new ReviewResponseDTO(1L, 1L, "Отличный ресторан!", 5)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewResponseDTO> page = new PageImpl<>(reviews, pageable, 2);
        when(reviewService.getReviewsSortedByRatingAsc(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/reviews/sorted/asc")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].rating", is(3)))
                .andExpect(jsonPath("$.content[1].rating", is(5)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(reviewService).getReviewsSortedByRatingAsc(any(Pageable.class));
    }

    @Test
    void getReviewsSortedByRatingDesc_ShouldReturnPagedResults() throws Exception {
        // Given
        List<ReviewResponseDTO> reviews = Arrays.asList(
                new ReviewResponseDTO(1L, 1L, "Отличный ресторан!", 5),
                new ReviewResponseDTO(3L, 3L, "Средний ресторан", 3)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewResponseDTO> page = new PageImpl<>(reviews, pageable, 2);
        when(reviewService.getReviewsSortedByRatingDesc(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/reviews/sorted/desc")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].rating", is(5)))
                .andExpect(jsonPath("$.content[1].rating", is(3)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(reviewService).getReviewsSortedByRatingDesc(any(Pageable.class));
    }

    @Test
    void getReviewsSortedByRatingAsc_WithCustomPagination_ShouldReturnCorrectPage() throws Exception {
        // Given
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        Pageable pageable = PageRequest.of(1, 5);
        Page<ReviewResponseDTO> page = new PageImpl<>(reviews, pageable, 10);
        when(reviewService.getReviewsSortedByRatingAsc(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/reviews/sorted/asc")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(10)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.number", is(1)));

        verify(reviewService).getReviewsSortedByRatingAsc(any(Pageable.class));
    }

    @Test
    void getReviewsSortedByRatingDesc_WithoutPaginationParams_ShouldUseDefaults() throws Exception {
        // Given
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        Page<ReviewResponseDTO> page = new PageImpl<>(reviews, PageRequest.of(0, 20), 1);
        when(reviewService.getReviewsSortedByRatingDesc(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/reviews/sorted/desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(reviewService).getReviewsSortedByRatingDesc(any(Pageable.class));
    }
}