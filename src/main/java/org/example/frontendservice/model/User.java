package org.example.frontendservice.model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "username")
@ToString
public class User {

    private Long id;
    private String username;
    private String password;
    private LocalDate birthDate;
    private String phoneNumber;

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
}
