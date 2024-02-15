package org.example.frontendservice.model.dto.users;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class UserDetailsDto {
    private String username;
    private String password;
}
