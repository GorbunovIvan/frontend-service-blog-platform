package org.example.frontendservice.model.dto.users;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class UserResponseDto {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String username;

    @ToString.Exclude
    private String password;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private String phoneNumber;
}
