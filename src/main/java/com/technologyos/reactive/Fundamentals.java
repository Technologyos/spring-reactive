package com.technologyos.reactive;

import com.technologyos.reactive.models.Customer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Fundamentals {

    //los observables son inmutables
    public static void fluxExample(){
        Flux<String> names = Flux.just("Armando", "Taylor", "Carol", "Arturo", "Amanda");

        names
            .map(String::toUpperCase)
            .filter(name -> name.startsWith("A"))
            .doOnNext(System.out::println)
            .map(Customer::new)
            .subscribe(customer ->  log.info(customer.getName().toLowerCase()));

        List<String> users = new ArrayList<>();
        users.add("Andres");
        users.add("Armando");
        users.add("Maria");
        users.add("Diego");
        users.add("Juan");
        users.add("Antonio");

        Flux<String> namesWithList = Flux.fromIterable(users);

        namesWithList
            .map(String::toUpperCase)
            .filter(name -> name.startsWith("A"))
            .doOnNext(System.out::println)
            .map(Customer::new)
            .subscribe(
                customer -> log.info(customer.toString()), error -> log.error(error.getMessage()),
                () -> log.info("Observable finished")
            );

        namesWithList
            .map(String::toUpperCase)
            .map(Customer::new)
            .flatMap(customer ->{
                if (customer.getName().startsWith("D")) {
                    return Mono.just(customer);
                } else {
                    return Mono.empty();
                }
            }).subscribe(c -> log.info(c.toString()));
    }
}
