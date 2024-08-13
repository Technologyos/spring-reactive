package com.technologyos.reactive;

import com.technologyos.reactive.models.Comment;
import com.technologyos.reactive.models.Customer;
import com.technologyos.reactive.models.UserComments;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Fundamentals {

    //los observables son inmutables
    public static void fluxMonoExamples(){
        Flux<String> names = Flux.just("Armando", "Taylor", "Carol", "Arturo", "Amanda");

        names
            .map(String::toUpperCase)
            .filter(name -> name.startsWith("A"))
            .doOnNext(System.out::println)
            .map(name -> new Customer(name, ""))
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
            .map(name -> new Customer(name, ""))
            .subscribe(
                customer -> log.info(customer.toString()), error -> log.error(error.getMessage()),
                () -> log.info("Observable finished")
            );

        namesWithList
            .map(String::toUpperCase)
            .map(name -> new Customer(name, ""))
            .flatMap(customer ->{
                if (customer.getName().startsWith("D")) {
                    return Mono.just(customer);
                } else {
                    return Mono.empty();
                }
            }).subscribe(c -> log.info(c.toString()));

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Andres", "Guzman"));
        customers.add(new Customer("Pedro", "Fulano"));
        customers.add(new Customer("Bruce", "Lee"));
        customers.add(new Customer("Bruce", "Willis"));

        Flux.fromIterable(customers)
            .map(customer -> customer.getName().toUpperCase().concat(" ").concat(customer.getLastname().toUpperCase()))
            .flatMap(name -> {
                if (name.contains("bruce".toUpperCase())) {
                    return Mono.just(name);
                } else {
                    return Mono.empty();
                }
            })
            .map(String::toLowerCase)
            .subscribe(log::info);

        Flux.fromIterable(customers)
            .collectList()
            .subscribe(list -> {
                list.forEach(item -> log.info(item.toString()));
            });

        Mono<Customer> customerMono = Mono.fromCallable(() -> new Customer("John", "Doe"));
        Mono<Customer> customerMono1 = Mono.just(new Customer("John", "Doe"));

        Mono<Comment> comments = Mono.fromCallable(() -> {
            Comment comment = new Comment();
            comment.addComment("Hola pepe, qué tal!");
            comment.addComment("Mañana voy a la playa!");
            comment.addComment("Estoy tomando el curso de spring con reactor");
            return comment;
        });

        Mono<UserComments> userWithComments = customerMono
            .flatMap(u -> comments.map(c -> new UserComments(u, c)));

        userWithComments.subscribe(uc -> log.info(uc.toString()));

        Mono<UserComments> userWithComments2 = customerMono.zipWith(comments, UserComments::new);

        userWithComments2.subscribe(uc -> log.info(uc.toString()));

        Mono<UserComments> userWithComments3 = customerMono
            .zipWith(comments)
            .map(tuple -> {
                Customer customer = tuple.getT1();
                Comment comment = tuple.getT2();
                return new UserComments(customer, comment);
            });

        userWithComments3.subscribe(uc -> log.info(uc.toString()));

        Flux<Integer> ranges = Flux.range(0, 4);
        Flux.just(1, 2, 3, 4).map(i -> (i * 2))
            .zipWith(ranges, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
            .subscribe(log::info);

        Flux<Long> delays = Flux.interval(Duration.ofSeconds(1));
        ranges.zipWith(delays, (ra, re) -> ra).doOnNext(i -> log.info(i.toString())).blockLast();

        Flux<Integer> ranges2 = Flux.range(1, 12).delayElements(Duration.ofSeconds(1))
            .doOnNext(i -> log.info(i.toString()));

        ranges2.blockLast();

        try {
            CountDownLatch latch = new CountDownLatch(1);

            Flux.interval(Duration.ofSeconds(1)).doOnTerminate(latch::countDown).flatMap(i -> {
                if (i >= 5) {
                    return Flux.error(new InterruptedException("Solo hasta 5!"));
                }
                return Flux.just(i);
            }).map(i -> "Hola " + i).retry(2).subscribe(log::info, e -> log.error(e.getMessage()));
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Flux.create(emitter -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                private Integer contador = 0;

                @Override
                public void run() {
                    emitter.next(++contador);
                    if (contador == 10) {
                        timer.cancel();
                        emitter.complete();
                    }

                    if (contador == 5) {
                        timer.cancel();
                        emitter.error(new InterruptedException("Error, se ha detenido el flux en 5!"));
                    }

                }
            }, 1000, 1000);
        }).subscribe(next -> log.info(next.toString()), error -> log.error(error.getMessage()),
            () -> log.info("Hemos terminado"));


    }
}
