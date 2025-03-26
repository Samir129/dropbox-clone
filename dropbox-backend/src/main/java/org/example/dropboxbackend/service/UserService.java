package org.example.dropboxbackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dropboxbackend.controller.AuthController;
import org.example.dropboxbackend.exception.CustomExceptionResponse;
import org.example.dropboxbackend.model.Role;
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

    public boolean registerUser(AuthController.RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Requested user already exists...");
            return false;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setDateCreated(LocalDateTime.now());

        userRepository.save(user);
        log.info("User {} saved", request.getUsername());
        return true;
    }

    public void registerAdmin(AuthController.RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Requested user already exists");
            throw new CustomExceptionResponse("Admin already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setDateCreated(LocalDateTime.now());

        userRepository.save(user);
        log.info("Admin {} saved", request.getUsername());
    }

    public void deleteAll(){
        userRepository.deleteAll();
        log.info("All users deleted");
    }

    public boolean deleteUser(String username){
        if (!userRepository.existsByUsername(username)) {
            return false;
        }
        userRepository.deleteByUsername(username);
        return true;
    }

    public void deleteAllUsers(){
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            log.error("Delete all users failed with error {}", e.getMessage());
            throw new CustomExceptionResponse("Delete Failed with error " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

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
