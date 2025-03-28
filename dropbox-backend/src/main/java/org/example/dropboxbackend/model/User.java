package org.example.dropboxbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;  // USER or ADMIN
    private LocalDateTime dateCreated;

}
