package com.datastax.kgdo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ProductController {

    private static final String template = "Product %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    DriverComponent component;

    @RequestMapping("/product")
    public Product product(@RequestParam(value = "name", defaultValue = "World") String name) {
        System.out.println("component = " + component.session());

        return new Product(counter.incrementAndGet(),
                String.format(template, name));
    }
}
