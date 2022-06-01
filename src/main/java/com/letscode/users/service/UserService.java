package com.letscode.users.service;

import com.letscode.users.dto.Email;
import com.letscode.users.dto.IdUser;
import com.letscode.users.dto.UserDTO;
import com.letscode.users.model.User;
import com.letscode.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Mono<User> createUser(User user){

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);


    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByUsername(email)
                .cast(UserDetails.class);
    }

    public Mono<String> transformBase64toString(String authorizationBase64){

        byte[] authorizationBytes = Base64.getDecoder().decode(authorizationBase64);

        String email = Base64.getEncoder().encodeToString(authorizationBytes);


        return Mono.just(email);


    }



    public Mono<IdUser> getIdUser(Optional<String> email){


        Mono<User> userDB = userRepository.findByUsername(email.orElse(null));


        return userDB
                .cast(User.class)
                .map(user -> new IdUser(user.getId()))
                .switchIfEmpty(Mono.just(new IdUser()));


    }


    public Mono<Boolean> validateUser(UserDTO user){

       Mono<User> userValidated =  userRepository.findByUsername(user.getEmail());

       return  userValidated
               .cast(User.class)
               .map(userDB -> encoder.matches(user.getPassword() ,userDB.getPassword()))
               .switchIfEmpty(Mono.just(false));
    }


}
