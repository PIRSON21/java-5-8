package com.mediasoft.controller;

import com.mediasoft.dto.ReviewRequestDTO;
import com.mediasoft.dto.ReviewResponseDTO;
import com.mediasoft.dto.ReviewUpdateRequestDTO;
import com.mediasoft.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponseDTO create(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        return reviewService.create(reviewRequestDTO);
    }

    @GetMapping("/{restaurantId}/{visitorId}")
    public ReviewResponseDTO getById(@PathVariable Long visitorId, @PathVariable Long restaurantId) {
        return reviewService.getById(visitorId, restaurantId);
    }

    @GetMapping
    public List<ReviewResponseDTO> getAll() {
        return reviewService.getAll();
    }

    @PutMapping("/{restaurantId}/{visitorId}")
    public ReviewResponseDTO update(@PathVariable Long visitorId, @PathVariable Long restaurantId,
                                     @Valid @RequestBody ReviewUpdateRequestDTO reviewRequestDTO) {
        return reviewService.update(visitorId, restaurantId, reviewRequestDTO);
    }

    @DeleteMapping("/{restaurantId}/{visitorId}")
    public void delete(@PathVariable Long visitorId, @PathVariable Long restaurantId) {
        reviewService.delete(visitorId, restaurantId);
    }
}
