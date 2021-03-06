package com.letscode.users.repository;

import com.letscode.users.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,String> {

    Mono<User> findByUsername(String username);
}
