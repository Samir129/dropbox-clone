package org.example.dropboxbackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dropboxbackend.controller.AuthController;
import org.example.dropboxbackend.exception.CustomValidationException;
import org.example.dropboxbackend.model.User;
import org.example.dropboxbackend.model.UserAuthentication;
import org.example.dropboxbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public void registerUser(AuthController.RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Requested user already exists...");
            throw new CustomValidationException("User already exists!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        user.setDateCreated(LocalDateTime.now());

        userRepository.save(user);
        log.info("New user saved");
    }

    public void deleteAll(){
        userRepository.deleteAll();
        log.info("All users deleted");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserAuthentication(user.orElse(null));
    }
}
