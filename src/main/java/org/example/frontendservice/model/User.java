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
public class User implements UserDetails {

    private Long id;
    private String username;

    @ToString.Exclude
    private String password;

    private LocalDate birthDate;
    private String phoneNumber;
    private final boolean isActive = true;

    private Role role = Role.USER;

    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public User(String username, String password, LocalDate birthDate, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public void addPost(Post post) {
        getPosts().add(post);
    }

    public void addComment(Comment comment) {
        getComments().add(comment);
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
