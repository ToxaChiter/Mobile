package com.example.minishop_makarevich;

import java.io.Serializable;

public class Product implements Serializable {
    String name;
    int price;
    boolean box;

    Product(String _name, int _price,  boolean _box) {
        name = _name;
        price = _price;
        box = _box;
    }
}
