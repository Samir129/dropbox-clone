package org.example.dropboxbackend.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dropboxbackend.exception.CustomExceptionResponse;
import org.example.dropboxbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // will check for ROLE_ADMIN
@AllArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        boolean isDeleted = userService.deleteUser(username);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully");
        } else {
            throw new CustomExceptionResponse("User not found", HttpStatus.NOT_FOUND);
        }

    }

//    @DeleteMapping("/deleteAll")
    public  ResponseEntity<?> deleteAllUsers(){
        userService.deleteAll();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
