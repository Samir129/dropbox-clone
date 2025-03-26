package org.example.dropboxbackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.dropboxbackend.exception.CustomExceptionResponse;
import org.example.dropboxbackend.model.Role;
import org.example.dropboxbackend.service.UserService;
import org.example.dropboxbackend.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Validated
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        log.info("Register user endpoint -- enter");
        boolean isRegistered = userService.registerUser(request);

        if (isRegistered)
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(request.getUsername()));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest request){
        log.info("Requested admin registration");
        userService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterResponse(request.getUsername(), Role.ADMIN, "Admin registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        log.info("Login request -- enter");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                log.info("{} is authenticated", request.getUsername());
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String jwtToken = jwtUtil.generateToken(request.getUsername());
                return ResponseEntity.ok(new AuthResponse(jwtToken, userDetails.getUsername()));
            }
            return null;
        } catch (BadCredentialsException e){
            log.warn("Bad credentials entered by: {}", request.username);
            throw new CustomExceptionResponse("Bad Credentials", HttpStatus.FORBIDDEN);
        } catch (Exception e){
            throw new CustomExceptionResponse("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Getter
    @Setter
    public static class RegisterRequest{

        @NotNull(message = "Username cannot be null")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        private String username;
        @NotEmpty
        @Size(min = 5, message = "Password must be at least 5 characters")
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RegisterResponse{
        private String username;
        private Role role = Role.USER;
        private String message = "User registered successfully";

        public RegisterResponse(String username){
            this.username = username;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthRequest {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String username;
    }
}
