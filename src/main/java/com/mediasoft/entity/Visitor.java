package com.mediasoft.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitor {
    private Long id;
    private String name;
    private int age;
    private Sex sex;
}
