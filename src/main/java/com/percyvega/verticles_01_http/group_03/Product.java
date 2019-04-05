package com.percyvega.verticles_01_http.group_03;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private long id;
    private String description;

    public Product(String description) {
        this.description = description;
    }
}