package com.mediasoft.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long visitorId;
    private Long restaurantId;
    private int rating;
    private String comment;
}
