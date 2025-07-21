package com.mediasoft.dto;

import lombok.Value;

@Value
public class ReviewResponseDTO {
    Long restaurantId;
    Long visitorId;
    String comment;
    int rating;
}
