package com.technologyos.reactive;

import com.technologyos.reactive.models.Customer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class Fundamentals {

    public void fluxExample(){
        Flux<String> names = Flux.just("Armando", "Taylor", "Carol", "Arturo", "Amanda");

        names
            .map(String::toUpperCase)
            .filter(name -> name.startsWith("A"))
            .doOnNext(System.out::println)
            .map(Customer::new)
            .subscribe(customer ->  log.info(customer.getName().toLowerCase()));
    }
}
