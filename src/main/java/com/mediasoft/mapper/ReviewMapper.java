package com.mediasoft.mapper;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "visitorId", source = "visitor.id")
    ReviewResponseDTO toReviewResponseDTO(Review review);
    Review toReview(ReviewUpdateRequestDTO reviewUpdateRequestDTO);
    @Mapping(target = "id", expression = "java(new com.mediasoft.entity.ReviewID(reviewRequestDTO.getRestaurantId(), reviewRequestDTO.getVisitorId()))")
    @Mapping(target = "restaurant", expression = "java(new com.mediasoft.entity.Restaurant(reviewRequestDTO.getRestaurantId()))")
    @Mapping(target = "visitor", expression = "java(new com.mediasoft.entity.Visitor(reviewRequestDTO.getVisitorId()))")
    Review toReview(ReviewRequestDTO reviewRequestDTO);
}
