package com.mediasoft.config;

import com.mediasoft.entity.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

//@Configuration
//public class TestDataConfig{
//    @Bean
//    public Restaurant restaurant1() {
//        return new Restaurant(
//                1L,
//                "Test Restaurant",
//                "Давай по одной",
//                CuisineType.ITALIAN,
//                BigDecimal.valueOf(100),
//                BigDecimal.valueOf(4.5)
//        );
//    }
//
//    @Bean
//    public Restaurant restaurant2() {
//        return new Restaurant(
//                2L,
//                "Another Test Restaurant",
//                "Хуйню не неси, Сим",
//                CuisineType.CHINESE,
//                BigDecimal.valueOf(150),
//                BigDecimal.valueOf(4.0)
//        );
//    }
//
//    @Bean
//    public Visitor visitor1() {
//        return new Visitor(
//                1L,
//                "Test Visitor",
//                30,
//                Sex.MALE);
//    }
//
//    @Bean
//    public Visitor visitor2() {
//        return new Visitor(
//                2L,
//                "Another Test Visitor",
//                25,
//                Sex.FEMALE);
//    }
//
//    @Bean
//    public Review testReview() {
//        return new Review(
//                1L,
//                1L,
//                5,
//                "Отличный ресторан, рекомендую!");
//    }
//}
