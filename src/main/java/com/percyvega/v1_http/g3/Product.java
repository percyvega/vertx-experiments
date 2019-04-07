package com.percyvega.v1_http.g3;

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
