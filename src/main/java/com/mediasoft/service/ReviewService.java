package com.mediasoft.service;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO create(ReviewRequestDTO reviewRequestDTO);
    List<ReviewResponseDTO> getAll();
    ReviewResponseDTO getById(Long visitorId, Long restaurantId);
    ReviewResponseDTO update(Long visitorId, Long restaurantId, ReviewUpdateRequestDTO reviewRequestDTO);
    void delete(Long visitorId, Long restaurantId);
}
