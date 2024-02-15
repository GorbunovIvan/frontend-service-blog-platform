package org.example.frontendservice.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "username")
@ToString
public class User implements EntityWithId<Long>, UserDetails {

    private Long id;
    private String username;

    @ToString.Exclude
    private String password;

    private LocalDate birthDate;
    private String phoneNumber;

    private Role role = Role.USER;

    private final boolean isActive = true;

    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public User(Long id) {
        this.id = id;
    }

    public User(String username, String password, LocalDate birthDate, String phoneNumber) {
        this(null, username, password, birthDate, phoneNumber);
    }

    public User(Long id, String username, String password, LocalDate birthDate, String phoneNumber) {
        setId(id);
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    // Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRole().getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
