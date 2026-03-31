package com.tripquest.backend.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String bio;

    @Column
    private String avatarUrl;

    @Builder.Default
    @Column(nullable = false)
    private Integer travelScore = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "user_visited_countries", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "country_code")
    @Builder.Default
    private List<String> visitedCountries = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_bucketlist", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "country_code")
    @Builder.Default
    private List<String> bucketlistCountries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Spring Security utilise getUsername() comme identifiant principal → retourne email
    @Override
    public String getUsername() {
        return email;
    }

    // Retourne le nom d'affichage (Lombok ne génère pas getUsername() car surchargé)
    public String getDisplayUsername() {
        return username;
    }

    // TODO: Add trips relationship
}
