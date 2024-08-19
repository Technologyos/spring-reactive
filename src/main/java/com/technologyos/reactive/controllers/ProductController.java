package com.technologyos.reactive.controllers;

import com.technologyos.reactive.models.documents.Product;
import com.technologyos.reactive.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductRepository productRepository;


    @GetMapping({"/display", "/"})
    public String display(Model model) {

        Flux<Product> products = productRepository.findAll().map(product -> {

            product.setName(product.getName().toUpperCase());
            return product;
        });

        products.subscribe(prod -> log.info(prod.getName()));

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");
        return "display";
    }

    @GetMapping("/display-datadriver")
    public String displayDataDriver(Model model) {

        Flux<Product> products = productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        }).delayElements(Duration.ofSeconds(1));

        products.subscribe(prod -> log.info(prod.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
        model.addAttribute("title", "Listado de productos");
        return "display";
    }

    @GetMapping("/display-full")
    public String displayFull(Model model) {

        Flux<Product> products = productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        }).repeat(5000);

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");
        return "display";
    }

    @GetMapping("/display-chunked")
    public String displayChunked(Model model) {

        Flux<Product> products = productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        }).repeat(5000);

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");
        return "display-chunked";
    }
}
