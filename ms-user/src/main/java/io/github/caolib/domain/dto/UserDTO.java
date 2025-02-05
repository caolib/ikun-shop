package io.github.caolib.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    @NotNull
    private Long id;
    @NotNull
    private String username;
    @NotNull
    private String phone;

    private LocalDateTime updateTime;
}
