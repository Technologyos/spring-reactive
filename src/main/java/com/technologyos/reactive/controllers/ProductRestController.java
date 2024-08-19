package com.technologyos.reactive.controllers;

import com.technologyos.reactive.models.documents.Product;
import com.technologyos.reactive.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;


    @GetMapping
    public Flux<Product> index(){
        return productRepository.findAll()
            .map(product -> {
                product.setName(product.getName().toUpperCase());
                return product;
            })
            .doOnNext(prod -> log.info(prod.getName()));
    }

    @GetMapping("/{id}")
    public Mono<Product> show(@PathVariable String id){
        Flux<Product> products = productRepository.findAll();
        return products
            .filter(p -> p.getId().equals(id))
            .next()
            .doOnNext(prod -> log.info(prod.getName()));
    }
}
