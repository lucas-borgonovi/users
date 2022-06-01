package com.letscode.users.controller;

import com.letscode.users.dto.Email;
import com.letscode.users.dto.IdUser;
import com.letscode.users.dto.UserDTO;
import com.letscode.users.model.User;
import com.letscode.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.header.Header;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class UserController {

    private static String BASE_URL = "/users";

    private final UserService userService;


//    @Bean
//    public RouterFunction<ServerResponse> getId(){
//
//        return RouterFunctions.route()
//                .POST(BASE_URL + "/id", request -> request
//                        .bodyToMono(Email.class)
//                        .flatMap(userService::getIdUser)
//                        .flatMap(result -> ServerResponse.status(HttpStatus.OK).body(Mono.just(result),IdUser.class))).build();
//
//    }

    @Bean
    public RouterFunction<ServerResponse> getId(){

        return RouterFunctions.route()
                .GET(BASE_URL + "/id", request -> {
                            Optional<String> queryParam = request.queryParam("email");
                            return ServerResponse.ok().body(userService.getIdUser(queryParam),IdUser.class);
                }).build();

    }


    @Bean
    public RouterFunction<ServerResponse> createUser(){
        return RouterFunctions.route()
                .POST(BASE_URL + "/create",request -> request
                        .bodyToMono(User.class)
                        .flatMap(userService::createUser)
                        .flatMap(user -> ServerResponse.status(HttpStatus.CREATED).body(Mono.just(user), User.class))).build();

    }

    @Bean
    public RouterFunction<ServerResponse> login(){
        return RouterFunctions.route()
                .POST(BASE_URL + "/login",request -> request
                        .bodyToMono(UserDTO.class)
                        .flatMap(userService::validateUser)
                        .flatMap(validate -> ServerResponse.status(HttpStatus.OK).body(Mono.just(validate),Boolean.class))).build();

    }



}
