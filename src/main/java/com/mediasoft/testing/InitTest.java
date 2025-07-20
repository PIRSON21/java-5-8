package com.mediasoft.testing;

import com.mediasoft.entity.*;
import com.mediasoft.service.RestaurantService;
import com.mediasoft.service.ReviewService;
import com.mediasoft.service.VisitorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InitTest {
    private final VisitorService visitorService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final Restaurant restaurant1;
    private final Restaurant restaurant2;
    private final Visitor visitor1;
    private final Visitor visitor2;
    private final ObjectProvider<Review> reviewProvider;

    @Autowired
    public InitTest(VisitorService visitorService, RestaurantService restaurantService, ReviewService reviewService,
                    @Qualifier("restaurant1") Restaurant restaurant1,
                    @Qualifier("restaurant2") Restaurant restaurant2,
                    @Qualifier("visitor1") Visitor visitor1,
                    @Qualifier("visitor2") Visitor visitor2,
                    ObjectProvider<Review> reviewProvider) {
        this.visitorService = visitorService;
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
        this.restaurant1 = restaurant1;
        this.restaurant2 = restaurant2;
        this.visitor1 = visitor1;
        this.visitor2 = visitor2;
        this.reviewProvider = reviewProvider;
    }

    @PostConstruct
    public void init() {
        visitorService.save(visitor1);
        visitorService.save(new Visitor(1L, "Джон Доу", 25, Sex.MALE));
        visitorService.save(new Visitor(2L, "Джейден Смит", 30, Sex.FEMALE));
        visitorService.save(new Visitor(3L, null, 10, Sex.MALE));

        restaurantService.save(restaurant1);
        restaurantService.save(new Restaurant(1L, "Крошка картошка", "Абоба", CuisineType.ITALIAN, BigDecimal.valueOf(300), BigDecimal.ZERO));
        restaurantService.save(new Restaurant(2L, "Жан Клод Мангал", "Aboba", CuisineType.CHINESE, BigDecimal.valueOf(500), BigDecimal.ZERO));
        restaurantService.save(new Restaurant(3L, "Тупак шампур", "Aboba", CuisineType.RUSSIAN, BigDecimal.valueOf(700), BigDecimal.ZERO));

        reviewService.save(new Review(1L, 1L, 5, "Аху**но отдохнули!"));
        reviewService.save(new Review(1L, 2L, 4, "Норм"));
        reviewService.save(new Review(2L, 1L, 3, null));

        List<Review> reviews = reviewService.findAll();
        for (Review review : reviews) {
            System.out.println("Отзыв: " + review.getVisitorId() + " - " + review.getRestaurantId() + " - " + review.getRating() + " - " + review.getComment());
        }

        List<Restaurant> restaurants = restaurantService.findAll();
        for (Restaurant restaurant : restaurants) {
            System.out.println("Ресторан: " + restaurant.getId() + " - " + restaurant.getName() + " - " + restaurant.getCuisineType() + " - " + restaurant.getRating());
        }

        List<Visitor> visitors = visitorService.findAll();
        for (Visitor visitor : visitors) {
            System.out.println("Посетитель: " + visitor.getId() + " - " + visitor.getName() + " - " + visitor.getAge() + " - " + visitor.getSex());
        }
    }
}
