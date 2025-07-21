package com.mediasoft.mapper;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewResponseDTO toReviewResponseDTO(Review review);
    Review toReview(ReviewUpdateRequestDTO reviewUpdateRequestDTO);
    Review toReview(ReviewRequestDTO reviewRequestDTO);
}
