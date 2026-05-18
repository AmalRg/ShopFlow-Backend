package com.example.shopflow.entity;

import com.example.shopflow.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    // Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Email @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @NotBlank
    @Column(nullable = false)
    @JsonIgnore
    private String motDePasse;

    @Setter
    @NotBlank private String prenom;
    @Setter
    @NotBlank private String nom;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Setter
    @Column(nullable = false)
    private boolean actif = true;

    @Column(updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private SellerProfile sellerProfile;

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    public User() {}

    // Getters
    public Long getId()                  { return id; }
    public String getEmail()             { return email; }
    public String getMotDePasse()        { return motDePasse; }
    public String getPrenom()            { return prenom; }
    public String getNom()               { return nom; }
    public Role getRole()                { return role; }
    public boolean isActif()             { return actif; }
    public LocalDateTime getDateCreation(){ return dateCreation; }
    public SellerProfile getSellerProfile(){ return sellerProfile; }
    public List<Address> getAddresses()  { return addresses; }

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword()             { return motDePasse; }
    @Override public String getUsername()             { return email; }
    @Override public boolean isEnabled()              { return actif; }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
}