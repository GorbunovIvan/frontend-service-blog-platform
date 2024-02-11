package org.example.frontendservice.model.dto;

import lombok.*;
import org.example.frontendservice.model.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class UserRequestDto {

    private String username;

    @ToString.Exclude
    private String password;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private String phoneNumber;

    public UserRequestDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.birthDate = user.getBirthDate();
        this.phoneNumber = user.getPhoneNumber();
    }

    public User toUser() {
        return new User(
                getUsername(),
                getPassword(),
                getBirthDate(),
                getPhoneNumber()
        );
    }
}
