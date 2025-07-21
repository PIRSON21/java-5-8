package com.mediasoft.repository;

import com.mediasoft.entity.Review;
import com.mediasoft.entity.ReviewID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, ReviewID>, PagingAndSortingRepository<Review, ReviewID> {

    Page<Review> findAllByOrderByRatingAsc(Pageable pageable);
    Page<Review> findAllByOrderByRatingDesc(Pageable pageable);

    Optional<Review> findByVisitorIdAndRestaurantId(Long visitorId, Long restaurantId);
    List<Review> findByVisitorId(Long visitorId);
    List<Review> findByRestaurantId(Long restaurantId);
}
